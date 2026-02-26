package ir.ehsannarmani.compose_charts.utils

/**
 * This function calculates offset as a percent of total
 */
internal fun calculateOffset(
    maxValue: Double,
    minValue: Double,
    total: Float,
    value: Float,
): Double {
    val percentage = (value - minValue) / (maxValue - minValue)
    return total * percentage
}

/**
 * This function is reverse of calculateOffset, calculates value from total value for a specific offset
 */
internal fun calculateValue(
    minValue: Double,
    maxValue: Double,
    total: Float,
    offset: Float
): Double {
    val percentage = offset / total
    val range = maxValue - minValue
    val value = minValue + percentage * range
    return value
}