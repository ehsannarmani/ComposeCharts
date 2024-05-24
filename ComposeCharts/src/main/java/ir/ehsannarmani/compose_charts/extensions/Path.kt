package ir.ehsannarmani.compose_charts.extensions

import android.graphics.RectF
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import android.graphics.PathMeasure as AndroidPathMeasure
import android.graphics.Path as AndroidPath

fun Path.flatten(): MutableList<Pair<Int, Offset>> {
    val measure = PathMeasure()
    measure.setPath(this, false)
    val result = mutableListOf<Pair<Int, Offset>>()
    for (i in 0 until measure.length.toInt()) {
        result.add(i to measure.getPosition(i.toFloat()))
    }
    return result
}
