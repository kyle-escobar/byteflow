package dev.kyleescobar.byteflow.visitor

import dev.kyleescobar.byteflow.BFClass
import dev.kyleescobar.byteflow.BFField
import dev.kyleescobar.byteflow.BFMethod
import dev.kyleescobar.byteflow.ClassGroup
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.commons.ClassRemapper
import org.objectweb.asm.commons.SimpleRemapper
import org.objectweb.asm.tree.ClassNode

internal class RenameVisitor : BFVisitor {

    private lateinit var group: ClassGroup

    private val classes = mutableListOf<ClassNode>()
    private val mappings = hashMapOf<String, String>()

    override fun visitClassGroup(group: ClassGroup) {
        this.group = group
    }

    override fun visitStart() {
        group.commit()
        group.classes.forEach { cls ->
            val bytes = group.loader.getClassBytes(cls.info) ?: throw IllegalStateException("Failed to get class: ${cls.name} bytes.")
            val node = ClassNode()
            val reader = ClassReader(bytes)
            reader.accept(node, ClassReader.SKIP_FRAMES)
            classes.add(node)
        }
    }

    override fun visitEnd() {
        /*
         * Apply the mappings.
         */
        this.applyMappings()

        val inputFile = group.loader.inputFile
        val outputFile = group.loader.outputFile

        group.clear()
        if(inputFile != null) group.loader.inputFile = inputFile
        if(outputFile != null) group.loader.outputFile = outputFile

        classes.forEach { cls ->
            val writer = ClassWriter(ClassWriter.COMPUTE_MAXS)
            cls.accept(writer)
            val bytes = writer.toByteArray()
            group.addClass(cls.name, bytes)
        }
    }

    fun renameClass(cls: BFClass, newName: String) {
        mappings[cls.name] = newName
    }

    fun renameMethod(method: BFMethod, newName: String) {
        mappings["${method.owner.name}.${method.name}${method.signature}"] = newName
    }

    fun renameField(field: BFField, newName: String) {
        mappings["${field.owner.name}.${field.name}"] = newName
    }

    private fun applyMappings() {
        val newClasses = mutableListOf<ClassNode>()
        val remapper = SimpleRemapper(mappings)

        classes.forEach { cls ->
            val newNode = ClassNode()
            cls.accept(ClassRemapper(newNode, remapper))
            newClasses.add(newNode)
        }

        classes.clear()
        classes.addAll(newClasses)
    }
}