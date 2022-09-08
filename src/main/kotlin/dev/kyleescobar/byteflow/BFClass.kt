package dev.kyleescobar.byteflow

import dev.kyleescobar.byteflow.editor.ClassEditor
import dev.kyleescobar.byteflow.reflect.ClassInfo
import dev.kyleescobar.byteflow.visitor.BFVisitor

class BFClass(val group: ClassGroup, val info: ClassInfo, val editor: ClassEditor) {

    private val methodMap = hashMapOf<String, BFMethod>()
    private val fieldMap = hashMapOf<String, BFField>()

    init {
        info.methods().forEach { methodInfo ->
            val bfmethod = BFMethod(this, methodInfo, editor.context().editMethod(methodInfo))
            methodMap["${bfmethod.name}${bfmethod.signature}"] = bfmethod
        }
        info.fields().forEach { fieldInfo ->
            val bffield = BFField(this, fieldInfo, editor.context().editField(fieldInfo))
            fieldMap["${bffield.name}${bffield.signature}"] = bffield
        }
    }

    val name: String get() = editor.name()

    val methods: List<BFMethod> get() = methodMap.values.toList()
    val fields: List<BFField> get() = fieldMap.values.toList()

    fun getMethod(name: String, signature: String): BFMethod? = methodMap["$name$signature"]

    fun getField(name: String, signature: String): BFField? = fieldMap["$name$signature"]

    fun accept(visitor: BFVisitor) {
        visitor.visitClass(this)
        methods.forEach { method ->
            method.accept(visitor)
        }
        fields.forEach { field ->
            field.accept(visitor)
        }
    }

    override fun toString(): String {
        return name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if(other is BFClass) {
            if(other.name == name) {
                return true
            }
        }
        return false
    }
}