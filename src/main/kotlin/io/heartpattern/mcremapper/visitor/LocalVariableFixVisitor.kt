package io.heartpattern.mcremapper.visitor

import io.heartpattern.mcremapper.model.LocalVariableFixType
import io.heartpattern.mcremapper.model.LocalVariableFixType.*
import io.heartpattern.mcremapper.renameKeywords
import io.heartpattern.mcremapper.toTypeName
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import java.nio.charset.StandardCharsets
import java.util.concurrent.atomic.AtomicInteger

/**
 * Fix local variable name is always $$<number>
 */
class LocalVariableFixVisitor(
    cv: ClassVisitor,
    val type: LocalVariableFixType
) : ClassVisitor(Opcodes.ASM9, cv) {
    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        val count = HashMap<String, AtomicInteger>()

        return object : MethodVisitor(Opcodes.ASM9, methodVisitor) {
            override fun visitLocalVariable(
                name: String?,
                descriptor: String?,
                signature: String?,
                start: Label?,
                end: Label?,
                index: Int
            ) {
                if (name == null || (StandardCharsets.US_ASCII.newEncoder().canEncode(name) && !name.startsWith("$$"))) {
                    super.visitLocalVariable(name, descriptor, signature, start, end, index)
                    return
                }

                when (type) {
                    RENAME -> {
                        val newName = Type.getType(descriptor).internalName.toTypeName().renameKeywords()
                        if (!count.containsKey(newName)) count[newName] = AtomicInteger()
                        var i = ""
                        if (count[newName]!!.getAndIncrement() > 0) i = count[newName]!!.get().toString()
                        super.visitLocalVariable("$newName$i", descriptor, signature, start, end, index)
                    }
                    NO -> super.visitLocalVariable(name, descriptor, signature, start, end, index)
                    DELETE -> Unit// Do nothing
                }
            }
        }
    }
}