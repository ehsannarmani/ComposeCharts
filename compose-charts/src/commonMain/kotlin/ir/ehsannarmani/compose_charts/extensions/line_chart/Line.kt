package ir.ehsannarmani.compose_charts.extensions.line_chart

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import ir.ehsannarmani.compose_charts.utils.calculateOffset

internal data class PathData(
    val path: Path,
    val xPositions: List<Double>,
    val startIndex: Int,
    val endIndex: Int
)

sealed class PairedDataSort {
    data object Ascending : PairedDataSort()
    data object Descending : PairedDataSort()
}

internal fun DrawScope.getLinePath(
    dataPoints: List<Pair<Float, Float>>,
    maxValue: Float,
    minValue: Float,
    rounded: Boolean = true,
    size: Size = this.size,
    sort: PairedDataSort = PairedDataSort.Ascending
): PathData {

    val xValues  = dataPoints.map {p -> p.first}
    val (xMin, xMax) = xValues.let { it.min() to it.max() }

    val path = Path()
    if (dataPoints.isEmpty()) return PathData(path = path, xPositions = emptyList(),0, Int.MAX_VALUE)
    val calculateHeight = { value: Float ->
        size.height - calculateOffset(
            maxValue = maxValue.toDouble(),
            minValue = minValue.toDouble(),
            total = size.height,
            value = value
        )
    }
    val calculateWidth = { value: Float ->
        calculateOffset(
            maxValue = xMax.toDouble(),
            minValue = xMin.toDouble(),
            total = size.width,
            value = value
        )
    }

    val sortedDataPoints = dataPoints.sortedWith(
        compareBy<Pair<Float, Float>> { it.first }
            .thenBy { if (sort == PairedDataSort.Ascending) it.second else -it.second }
    )

    val xPositions = mutableListOf<Double>()

    var lastPoint: Pair<Float, Float> = 0f to 0f
    for (i in 0 until sortedDataPoints.size) {
        val (x, y) = sortedDataPoints[i]
        val height = calculateHeight(y).toFloat()
        val width = calculateWidth(x).toFloat()
        if (i == 0) {
            path.moveTo(width, height)
        } else {
            if (!rounded) {
                path.lineTo(width, height)
            } else {
                val (lastWidth, lastHeight) = lastPoint
                val centerWidth = (width + lastWidth)/2
                path.cubicTo(centerWidth, lastHeight, centerWidth, height, width, height)
            }
        }
        lastPoint = width to height
        xPositions.add(width.toDouble())
    }
    return PathData(path = path, xPositions = xPositions, 0 , sortedDataPoints.size - 1)
}