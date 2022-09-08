package dev.kyleescobar.byteflow.visitor

import dev.kyleescobar.byteflow.BFClass
import dev.kyleescobar.byteflow.BFMethod
import dev.kyleescobar.byteflow.ClassGroup
import dev.kyleescobar.byteflow.tree.TreeVisitor

open class ExprTreeVisitor : TreeVisitor(), BFVisitor {

    lateinit var group: ClassGroup private set
    lateinit var cls: BFClass private set
    lateinit var method: BFMethod private set

    override fun visitClassGroup(group: ClassGroup) {
        this.group = group
    }

    override fun visitClass(cls: BFClass) {
        this.cls = cls
    }

    override fun visitMethod(method: BFMethod) {
        this.method = method
    }

}