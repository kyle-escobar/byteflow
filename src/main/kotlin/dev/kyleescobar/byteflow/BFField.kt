package dev.kyleescobar.byteflow

import dev.kyleescobar.byteflow.editor.FieldEditor
import dev.kyleescobar.byteflow.reflect.FieldInfo
import dev.kyleescobar.byteflow.visitor.BFVisitor

class BFField(val owner: BFClass, val info: FieldInfo, val editor: FieldEditor) {

    val name: String get() = editor.name()

    val signature: String get() = editor.type().descriptor()

    fun accept(visitor: BFVisitor) {
        visitor.visitField(this)
    }

    override fun toString(): String {
        return "${owner.name}.$name"
    }

    override fun hashCode(): Int {
        return (name.hashCode() and 0xffff) shl 16 or signature.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if(other is BFField) {
            if(other.owner == owner && other.name == name && other.signature == signature) {
                return true
            }
        }
        return false
    }
}