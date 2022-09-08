package dev.kyleescobar.byteflow.file

import dev.kyleescobar.byteflow.reflect.ClassInfo
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream
import java.util.zip.ZipFile

class JarClassFileLoader : ClassFileLoader(ClassSource { ClassLoader.getSystemClassLoader().loadClass(it) }) {

    var memoryMode = false

    internal var inputFile: File? = null
    internal var outputFile: File? = null

    private val memoryOutputStreamMap = hashMapOf<String, ByteArrayOutputStream>()
    private val classInfos = mutableMapOf<String, ClassInfo>()

    private lateinit var jos: CustomJarOutputStream

    fun loadClassesFromJarFile(file: File): Array<ClassInfo?> {
        if(!file.name.endsWith(".jar")) throw IllegalArgumentException("File input must be jar file.")
        inputFile = file
        return loadClassesFromZipFile(ZipFile(file))
    }

    fun setOutputJarFile(file: File) {
        outputFile = file
        jos = CustomJarOutputStream(file)
        super.setOutputDir(file)
    }

    override fun outputStreamFor(name: String): OutputStream {
        return if(memoryMode) {
            memoryOutputStreamMap.getOrPut(name) { ByteArrayOutputStream() }
        } else {
            if(!this::jos.isInitialized) throw IllegalStateException("Output jar file not yet set.")
            jos.putNextEntry(JarEntry(name))
            jos
        }
    }

    override fun outputStreamFor(info: ClassInfo): OutputStream {
        return outputStreamFor(info.name() + ".class")
    }

    override fun done() {
        jos.closeStream()
    }

    fun addClassInfo(info: ClassInfo) {
        if(!classInfos.containsKey(info.name())) {
            classInfos[info.name()] = info
        }
    }

    override fun loadClass(name: String): ClassInfo {
        return if(classInfos.containsKey(name)) {
            classInfos[name]!!
        } else {
            super.loadClass(name)
        }
    }

    fun getMemoryOutputStream(name: String): ByteArrayOutputStream? {
        if(!memoryMode) throw IllegalStateException("Memory output mode is not set.")
        return memoryOutputStreamMap[name]
    }
    fun getMemoryOutputStream(info: ClassInfo): ByteArrayOutputStream? = getMemoryOutputStream(info.name() + ".class")

    private inner class CustomJarOutputStream(file: File) : JarOutputStream(FileOutputStream(file)) {

        override fun close() {
            if(memoryMode) {
                super.close()
            } else {
                closeEntry()
            }
        }

        fun closeStream() {
            super.close()
        }
    }
}