package io.heartpattern.mcremapper

import java.io.File
import java.net.URL

/**
 * Convert internal class name to familiar class name, which simply replace '/' to ','
 */
fun String.fromInternal(): String {
    return replace('/', '.')
}

/**
 * Convert familiar class name to internal class name, which simply replace '.' to ','
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
                this.replace("(.*/|)(.*)".toRegex(), "$2").decapitalize()
            }
        }
    }
}
