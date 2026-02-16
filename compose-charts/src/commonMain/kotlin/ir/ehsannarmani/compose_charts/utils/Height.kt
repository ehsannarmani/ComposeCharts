package ir.ehsannarmani.compose_charts.utils

/**
 * This function calculates offset from total for a specific value
 */
internal fun calculateOffset(
    maxValue: Double,
    minValue: Double,
    total: Float,
    value:Float
): Double {
    val range = maxValue - minValue
    val percentage = (value - minValue) / range
    val offset = total * percentage
    return offset
}

/**
 * This function is reverse of calculateOffset, calculates value from total value for a specific offset
 */
internal fun calculateValue(minValue: Double, maxValue: Double, total: Float, offset:Float): Double {
    val percentage = offset / total
    val range = maxValue - minValue
    val value = minValue + percentage * range
    return value
}