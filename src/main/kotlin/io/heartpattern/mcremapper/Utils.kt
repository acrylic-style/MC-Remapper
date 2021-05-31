package io.heartpattern.mcremapper

import java.io.File
import java.net.URL

/**
 * Convert a binary name of a class or an interface in its internal form to the normal form. Simply replace '/' to '.'
 * @see "Java Virtual Machine Specification §4.2.1"
 */
fun String.fromInternal(): String {
    return replace('/', '.')
}

/**
 * Convert a binary name of a class or an interface in its normal form to the internal form. Simply replace '.' to '/'
 * @see "Java Virtual Machine Specification §4.2.1"
 */
fun String.toInternal(): String {
    return replace('.', '/')
}

fun URL.download(prefix: String): File {
    val tempFile = File.createTempFile(prefix, null)
    tempFile.outputStream().use { output ->
        this.openStream().use { input ->
            input.copyTo(output)
        }
    }

    tempFile.deleteOnExit()
    return tempFile
}

fun String.toTypeName(): String {
    return when (this) {
        "[Z" -> "booleans"
        "[B" -> "bytes"
        "[C" -> "chars"
        "[S" -> "shorts"
        "[I" -> "ints"
        "[J" -> "longs"
        "[F" -> "floats"
        "[D" -> "doubles"
        "Z" -> "flag"
        "B" -> "b"
        "C" -> "c"
        "S" -> "s"
        "I" -> "i"
        "J" -> "l"
        "F" -> "f"
        "D" -> "d"
        else -> {
            return if (this.startsWith("[")) {
                this.replace("\\[+(.*)".toRegex(), "$1").toTypeName()
            } else {
                this.replace("(.*/|)(.*)".toRegex(), "$2")
                    .replace("(.*\\$|)(.*)".toRegex(), "$2")
                    .decapitalize()
            }
        }
    }
}

// rename all reserved keywords and weirds
fun String.renameKeywords(): String {
    // examples:
    // - AABB -> aABB -> aabb
    // - URI -> uRI -> uri
    // - UUID -> uUID -> uuid
    // - YELLINGCLASS -> yELLINGCLASS -> yellingclass
    if (this == "this") return this
    if (this.substring(0, 1).toLowerCase() + this.toUpperCase().substring(1) == this) return this.toLowerCase()
    return when (this) {
        "string" -> "s"
        "class" -> "clazz"
        "double" -> "d"
        "float" -> "f"
        "long" -> "l"
        "short" -> "s"
        "boolean" -> "flag"
        "char" -> "c"
        "int" -> "i"
        else -> (JavaTokens.appendIfToken(this) ?: this)
            .replace("[;]".toRegex(), "s")
    }
}
