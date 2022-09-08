package dev.kyleescobar.byteflow

import dev.kyleescobar.byteflow.context.PersistentBloatContext
import dev.kyleescobar.byteflow.editor.ClassHierarchy
import dev.kyleescobar.byteflow.file.ClassFile
import dev.kyleescobar.byteflow.file.JarClassFileLoader
import dev.kyleescobar.byteflow.reflect.ClassInfo
import dev.kyleescobar.byteflow.visitor.BFVisitor
import java.io.DataInputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream

class ClassGroup {

    var loader = JarClassFileLoader()
        private set

    var context = PersistentBloatContext(loader)
        private set

    private val classMap = hashMapOf<String, BFClass>()

    val classes get() = classMap.values.toList()

    init {
        ClassHierarchy.RELAX = true
    }

    fun addClass(info: ClassInfo): Boolean {
        if(classMap.containsKey(info.name())) return false
        classMap[info.name()] = BFClass(this, info, context.editClass(info))
        return true
    }

    fun addClass(name: String, data: ByteArray): Boolean {
        val cls = ClassFile(File("$name.class"), loader, DataInputStream(data.inputStream()))
        loader.addClassInfo(cls)
        return addClass(cls)
    }

    fun removeClass(info: ClassInfo): Boolean {
        if(!classMap.containsKey(info.name())) return false
        classMap.remove(info.name())
        return true
    }

    fun getClass(name: String): BFClass? = classMap[name]

    fun addJarFile(file: File) {
        if(!file.exists()) throw FileNotFoundException("Jar file: ${file.path} does not exist.")
        val classes = loader.loadClassesFromJarFile(file).filterNotNull()
        classes.forEach { cls ->
            if(!addClass(cls)) throw IllegalStateException("Failed to add class: ${cls.name()} to class group from jar file.")
        }
    }

    fun saveToJarFile(file: File) {
        if(file.exists()) file.deleteRecursively()
        loader.memoryMode = true
        loader.setOutputJarFile(file)
        this.commit()

        val jos = JarOutputStream(FileOutputStream(file))
        classes.forEach { cls ->
            val bytes = loader.getMemoryOutputStream(cls.info)?.toByteArray() ?: error("Failed to get bytes for class: ${cls.name}.")
            jos.putNextEntry(JarEntry(cls.name + ".class"))
            jos.write(bytes)
            jos.closeEntry()
        }
        jos.close()
        loader.memoryMode = false
    }

    fun accept(visitor: BFVisitor) {
        visitor.visitClassGroup(this)
        visitor.visitStart()
        classes.forEach { cls ->
            cls.accept(visitor)
        }
        visitor.visitEnd()
    }

    fun clear() {
        classMap.clear()
        loader = JarClassFileLoader()
        context = PersistentBloatContext(loader)
    }

    fun commit() {
        classMap.values.forEach { cls ->
            cls.methods.forEach { method ->
                method.releaseCfg()
                method.editor.commit()
            }
            cls.fields.forEach { field ->
                field.editor.commit()
            }
            cls.editor.commit()
            cls.info.commit()
        }
    }

}