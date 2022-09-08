package dev.kyleescobar.byteflow.visitor

import dev.kyleescobar.byteflow.BFClass
import dev.kyleescobar.byteflow.BFField
import dev.kyleescobar.byteflow.BFMethod
import dev.kyleescobar.byteflow.ClassGroup

interface BFVisitor {
    fun visitStart() {}
    fun visitEnd() {}
    fun visitClassGroup(group: ClassGroup) {}
    fun visitClass(cls: BFClass) {}
    fun visitMethod(method: BFMethod) {}
    fun visitField(field: BFField) {}
}