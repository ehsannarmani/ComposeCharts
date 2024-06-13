package ir.ehsannarmani.compose_charts.extensions

import kotlin.math.pow

fun Double.format(decimalPlaces: Int): String {
    require(decimalPlaces >= 0) { "Decimal places must be non-negative." }

    if (decimalPlaces == 0) {
        return this.toInt().toString() // Handle whole numbers efficiently
    }

    val factor = 10.0.pow(decimalPlaces.toDouble())
    val roundedValue = kotlin.math.round(this * factor) / factor
    return roundedValue.toString()
}