package ir.ehsannarmani.compose_charts.utils

/**
 * This function calculates offset from total for a specific value
 */
fun calculateOffset(
    maxValue: Float,
    minValue: Float,
    total: Float,
    value:Float
): Float {
    val range = maxValue - minValue
    val percentage = (value - minValue) / range
    val offset = total * percentage
    return offset
}

/**
 * This function is reverse of calculateOffset, calculates value from total value for a specific offset
 */
fun calculateValue(minValue: Double, maxValue: Double, total: Float, offset:Float): Double {
    val percentage = offset / total
    val range = maxValue - minValue
    val value = minValue + percentage * range
    return value
}