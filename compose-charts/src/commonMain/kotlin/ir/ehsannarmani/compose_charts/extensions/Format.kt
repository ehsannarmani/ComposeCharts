package ir.ehsannarmani.compose_charts.extensions

import kotlin.text.StringBuilder

fun String.format(vararg args: Any): String {
    val formatted = StringBuilder(this)
    var argIndex = 0
    var currentIndex = 0

    while (currentIndex < formatted.length) {
        if (formatted[currentIndex] == '%' && currentIndex + 1 < formatted.length) {
            when (val nextChar = formatted[currentIndex + 1]) {
                's' -> {
                    if (argIndex < args.size) {
                        formatted.replaceRange(currentIndex, currentIndex + 2, args[argIndex].toString())
                        argIndex++
                        currentIndex += args[argIndex - 1].toString().length // Adjust index
                    } else {
                        // Handle missing argument
                        formatted.replaceRange(currentIndex, currentIndex + 2, "??")
                    }
                }
                'd' -> {
                    if (argIndex < args.size) {
                        formatted.replaceRange(currentIndex, currentIndex + 2, args[argIndex].toString())
                        argIndex++
                        currentIndex += args[argIndex - 1].toString().length // Adjust index
                    } else {
                        // Handle missing argument
                        formatted.replaceRange(currentIndex, currentIndex + 2, "??")
                    }
                }
                else -> { // Other format specifiers
                    formatted.replaceRange(currentIndex, currentIndex + 2, "%${nextChar}")
                }
            }
            currentIndex++ // Skip the next character
        } else {
            currentIndex++ // Move to the next character
        }
    }

    return formatted.toString()
}