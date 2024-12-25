package ir.ehsannarmani.compose_charts.extensions.line_chart

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import ir.ehsannarmani.compose_charts.models.ViewRange
import ir.ehsannarmani.compose_charts.utils.calculateOffset

internal data class PathData(
    val path: Path,
    val xPositions: List<Double>,
    val startIndex: Int,
    val endIndex: Int
)
internal fun DrawScope.getLinePath(
    dataPoints: List<Float>,
    maxValue: Float,
    minValue: Float,
    rounded: Boolean = true,
    size: Size? = null,
    startIndex : Int,
    endIndex: Int
): PathData {

    val _size = size ?: this.size
    val path = Path()
    if (dataPoints.isEmpty()) return PathData(path = path, xPositions = emptyList(),0, Int.MAX_VALUE)
    val calculateHeight = { value: Float ->
        calculateOffset(
            maxValue = maxValue.toDouble(),
            minValue = minValue.toDouble(),
            total = _size.height,
            value = value
        )
    }

    if(startIndex == 0) {
        path.moveTo(0f, (_size.height - calculateHeight(dataPoints[0])).toFloat())
    }else {
        val x = (startIndex * (_size.width / (dataPoints.size - 1)))
        val y = _size.height - calculateHeight(dataPoints[startIndex]).toFloat()
        path.moveTo(x, y)
    }

    val xPositions = mutableListOf<Double>()
    for (i in 0 until dataPoints.size - 1) {
        val x1 = (i * (_size.width / (dataPoints.size - 1)))
        val y1 = _size.height - calculateHeight(dataPoints[i]).toFloat()
        val x2 = ((i + 1) * (_size.width / (dataPoints.size - 1)))
        val y2 = _size.height - calculateHeight(dataPoints[i + 1]).toFloat()

        if (i in startIndex..<endIndex) {
            if (rounded) {
                val cx = (x1 + x2) / 2f
                path.cubicTo(x1 = cx, y1 = y1, x2 = cx, y2 = y2, x3 = x2, y3 = y2)
            } else {
                path.cubicTo(x1, y1, x1, y1, (x1 + x2) / 2, (y1 + y2) / 2)
                path.cubicTo((x1 + x2) / 2, (y1 + y2) / 2, x2, y2, x2, y2)
            }
        }

        xPositions.add(x1.toDouble())
    }
    xPositions.add(_size.width.toDouble())
    return PathData(path = path, xPositions = xPositions,startIndex,endIndex)
}