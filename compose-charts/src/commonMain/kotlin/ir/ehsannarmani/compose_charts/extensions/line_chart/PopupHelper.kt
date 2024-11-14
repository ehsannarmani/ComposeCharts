package ir.ehsannarmani.compose_charts.extensions.line_chart

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import ir.ehsannarmani.compose_charts.utils.calculateOffset
import ir.ehsannarmani.compose_charts.utils.calculateValue
import kotlin.math.floor
import kotlin.math.pow

internal data class Value(
    val calculatedValue: Double,
    val offset: Offset,
)

internal fun getPopupValue(
    points: List<Double>,
    fraction: Double,
    rounded: Boolean = false,
    size: Size,
    minValue: Double,
    maxValue: Double
): Value {
    val index = fraction * (points.count() - 1)
    val roundedIndex = floor(index).toInt()
    return if (fraction == 1.0) {
        val lastPoint = points.last()
        val offset = Offset(
            x = size.width,
            y = size.height - calculateOffset(
                minValue = minValue,
                maxValue = maxValue,
                total = size.height,
                value = lastPoint.toFloat()
            ).toFloat()
        )
        Value(calculatedValue = points.last(), offset = offset)
    } else {
        if (rounded && points.count() > 1) {
            val calculateHeight = { value: Double ->
                calculateOffset(
                    maxValue = maxValue,
                    minValue = minValue,
                    total = size.height,
                    value = value.toFloat()
                )
            }
            val x1 = (roundedIndex * (size.width / (points.size - 1)))
            val x2 = ((roundedIndex + 1) * (size.width / (points.size - 1)))
            val y1 = size.height - calculateHeight(points[roundedIndex])
            val y2 = size.height - calculateHeight(points[roundedIndex + 1])
            val cx = (x1 + x2) / 2f

            val areaFraction = roundedIndex.toDouble() / (points.size - 1)

            val t = (fraction - areaFraction) * (points.size - 1)

            val outputY = ((1 - t).pow(3) * (y1) +
                    3 * t * (1 - t).pow(2) * (y1) +
                    3 * (1 - t) * t.pow(2) * (y2) +
                    t.pow(3) * y2).toFloat()
            val outputX = ((1 - t).pow(3) * (x1) +
                    3 * t * (1 - t).pow(2) * (cx) +
                    3 * (1 - t) * t.pow(2) * (cx) +
                    t.pow(3) * x2).toFloat()
            val calculatedValue = calculateValue(
                minValue = minValue,
                maxValue = maxValue,
                total = size.height,
                offset = size.height - outputY
            )

            Value(calculatedValue = calculatedValue, offset = Offset(x = outputX, y = outputY))
        } else {
            val p1 = points[roundedIndex]
            val p2 = points.getOrNull(roundedIndex + 1) ?: p1
            val calculatedValue = ((p2 - p1) * (index - roundedIndex) + p1)
            val offset = Offset(
                x = if (points.count() > 1) (fraction * size.width).toFloat() else 0f,
                y = size.height - calculateOffset(
                    minValue = minValue,
                    maxValue = maxValue,
                    total = size.height,
                    value = calculatedValue.toFloat()
                ).toFloat()
            )
            Value(calculatedValue = calculatedValue, offset = offset)
        }
    }
}