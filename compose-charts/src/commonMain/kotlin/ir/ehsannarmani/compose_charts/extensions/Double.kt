package ir.ehsannarmani.compose_charts.extensions

import kotlin.math.PI
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

fun Double.toRadians(): Double {
    return this * PI / 180.0
}

fun Double.toDegrees(): Double {
    return this * 180.0 / PI
}