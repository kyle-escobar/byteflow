package dev.kyleescobar.byteflow

import dev.kyleescobar.byteflow.context.PersistentBloatContext
import dev.kyleescobar.byteflow.editor.ClassHierarchy
import dev.kyleescobar.byteflow.file.ClassFile
import dev.kyleescobar.byteflow.file.MemoryClassFileLoader
import dev.kyleescobar.byteflow.reflect.ClassInfo
import dev.kyleescobar.byteflow.visitor.BFVisitor
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.ClassNode
import java.io.DataInputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream

class ClassGroup {

    var loader = MemoryClassFileLoader(this)
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
        return addClass(cls)
    }

    fun removeClass(info: ClassInfo): Boolean {
        if(!classMap.containsKey(info.name())) return false
        classMap.remove(info.name())
        return true
    }

    fun getClass(name: String): BFClass? = classMap[name]

    fun addFromJarFile(file: File) {
        if(!file.exists()) throw FileNotFoundException("Jar file: ${file.path} does not exist.")
        loader.resetStreams()
        loader.loadClassesFromJarFile(file).filterNotNull().forEach { info ->
            if(!addClass(info)) throw IllegalStateException("Failed to add class: ${info.name()} from jar file.")
        }
    }

    fun saveToJarFile(file: File) {
        if(file.exists()) file.deleteRecursively()
        this.commit()

        val jos = JarOutputStream(FileOutputStream(file))
        classes.forEach { cls ->
            val bytes = loader.getClassBytes(cls.info) ?: throw IllegalStateException("Failed to get class: ${cls.name} bytes.")

            val node = ClassNode()
            val reader = ClassReader(bytes)
            reader.accept(node, ClassReader.SKIP_FRAMES)

            val writer = ClassWriter(ClassWriter.COMPUTE_MAXS)
            node.accept(writer)

            jos.putNextEntry(JarEntry(cls.name + ".class"))
            jos.write(writer.toByteArray())
            jos.closeEntry()
        }
        jos.close()
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
        loader = MemoryClassFileLoader(this)
        context = PersistentBloatContext(loader)
    }

    fun commit() {
        loader.resetStreams()
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