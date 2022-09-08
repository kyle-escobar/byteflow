package dev.kyleescobar.byteflow.file

import dev.kyleescobar.byteflow.ClassGroup
import dev.kyleescobar.byteflow.reflect.ClassInfo
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.OutputStream
import java.util.zip.ZipFile

class MemoryClassFileLoader(private val group: ClassGroup) : ClassFileLoader() {

    private val streamClassMap = hashMapOf<String, ByteArrayOutputStream>()

    internal var inputFile: File? = null
    internal var outputFile: File? = null

    fun loadClassesFromJarFile(file: File): Array<ClassInfo?> {
        inputFile = file
        return loadClassesFromZipFile(ZipFile(file))
    }

    override fun outputStreamFor(name: String): OutputStream {
        return streamClassMap.getOrPut(name) { ByteArrayOutputStream() }
    }

    override fun outputStreamFor(info: ClassInfo): OutputStream {
        return outputStreamFor(info.name() + ".class")
    }

    override fun loadClass(name: String): ClassInfo {
        return group.getClass(name.replace(".class", ""))?.info ?: super.loadClass(name)
    }

    fun getClassBytes(name: String): ByteArray? = streamClassMap[name]?.toByteArray()

    fun getClassBytes(info: ClassInfo): ByteArray? = getClassBytes(info.name() + ".class")
}