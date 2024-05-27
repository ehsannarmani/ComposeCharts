package ir.ehsannarmani.compose_charts.extensions

import kotlin.math.min

fun Double.split(
    step:Double,
    minValue:Double
):List<Double>{
    var current = this
    val result = mutableListOf<Double>()
    while (true){
        result.add(current)
        current = (current-step)
        if (current <= minValue) {
            result.add(current.coerceAtLeast(minValue))
            break
        }
    }
    println("Min value: $minValue,step: $step, result: $result")
    return result
}