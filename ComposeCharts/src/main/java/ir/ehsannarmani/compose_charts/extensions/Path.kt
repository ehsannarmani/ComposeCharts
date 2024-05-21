package ir.ehsannarmani.compose_charts.extensions

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure

fun Path.flatten(): MutableList<Pair<Int, Offset>> {
    val measure = PathMeasure()
    measure.setPath(this, false)
    val result = mutableListOf<Pair<Int, Offset>>()
    for (i in 0 until measure.length.toInt()) {
        result.add(i to measure.getPosition(i.toFloat()))
    }
    return result
}