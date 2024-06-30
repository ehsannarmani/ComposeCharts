package ir.ehsannarmani.compose_charts.extensions

import kotlin.math.PI

fun split(
    count: Int,
    minValue: Double,
    maxValue: Double,
):List<Double>{
    val step = (maxValue - minValue) / (count - 1)
    val result = (0 until count).map { (maxValue - it * step) }
    return result
}