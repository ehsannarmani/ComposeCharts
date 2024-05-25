package ir.ehsannarmani.compose_charts.extensions

fun Double.split(step:Double):List<Double>{
    var current = this
    val result = mutableListOf<Double>()
    while (current >= 0){
        result.add(current)
        current -= step
    }
    return result
}