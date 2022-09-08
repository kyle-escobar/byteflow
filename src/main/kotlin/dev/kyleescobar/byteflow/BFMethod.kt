package dev.kyleescobar.byteflow

import dev.kyleescobar.byteflow.cfg.FlowGraph
import dev.kyleescobar.byteflow.codegen.CodeGenerator
import dev.kyleescobar.byteflow.editor.MethodEditor
import dev.kyleescobar.byteflow.reflect.MethodInfo
import dev.kyleescobar.byteflow.visitor.BFVisitor
import dev.kyleescobar.byteflow.visitor.ExprTreeVisitor

class BFMethod(val owner: BFClass, val info: MethodInfo, val editor: MethodEditor) {

    private var cfg: FlowGraph? = null

    val name: String get() = editor.name()

    val signature: String get() = editor.nameAndType().type().descriptor()

    fun cfg(): FlowGraph {
        if(cfg == null) {
            cfg = FlowGraph(editor)
        }
        return cfg!!
    }

    fun releaseCfg() {
        if(cfg != null) {
            var codegen = CodeGenerator(editor)
            codegen = CodeGenerator(editor)

            codegen.replacePhis(cfg)
            codegen.simplifyControlFlow(cfg)

            editor.clearCode()
            cfg!!.visit(codegen)

            cfg = null
        }
    }

    fun accept(visitor: BFVisitor) {
        visitor.visitMethod(this)
        if(visitor is ExprTreeVisitor) {
            cfg().visit(visitor)
        }
    }

    override fun toString(): String {
        return "$owner.$name$signature"
    }

    override fun hashCode(): Int {
        return (name.hashCode() and 0xFFFF) shl 16 or signature.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if(other is BFMethod) {
            if(other.owner == owner && other.name == name && other.signature == signature) {
                return true
            }
        }
        return false
    }
}