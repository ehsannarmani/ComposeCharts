package ir.ehsannarmani.compose_charts.extensions

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import ir.ehsannarmani.compose_charts.models.DividerProperties
import ir.ehsannarmani.compose_charts.models.GridProperties

fun DrawScope.drawGridLines(
    dividersProperties: DividerProperties,
    gridEnabled: Boolean,
    xAxisProperties: GridProperties.AxisProperties,
    yAxisProperties: GridProperties.AxisProperties,
    size: Size? = null,
    xPadding: Float = 0f,
) {

    val _size = size ?: this.size

    val xAxisPathEffect = xAxisProperties.style.pathEffect
    val yAxisPathEffect = yAxisProperties.style.pathEffect


    if (xAxisProperties.enabled && gridEnabled) {
        for (i in 0 until xAxisProperties.lineCount - 1) {
            val y = _size.height.spaceBetween(itemCount = xAxisProperties.lineCount, index = i)
            drawLine(
                brush = xAxisProperties.color,
                start = Offset(0f + xPadding, y),
                end = Offset(_size.width + xPadding, y),
                strokeWidth = xAxisProperties.thickness.toPx(),
                pathEffect = xAxisPathEffect
            )
        }
    }
    if (yAxisProperties.enabled && gridEnabled) {
        for (i in 1 until yAxisProperties.lineCount) {
            val x = _size.width.spaceBetween(itemCount = yAxisProperties.lineCount, index = i)
            drawLine(
                brush = xAxisProperties.color,
                start = Offset(x + xPadding, 0f),
                end = Offset(x + xPadding, _size.height),
                strokeWidth = xAxisProperties.thickness.toPx(),
                pathEffect = yAxisPathEffect
            )
        }
    }
    if (yAxisProperties.enabled && gridEnabled) {
        drawLine(
            brush = yAxisProperties.color,
            start = Offset(this.size.width, 0f),
            end = Offset(this.size.width, _size.height),
            strokeWidth = yAxisProperties.thickness.toPx(),
            pathEffect = yAxisPathEffect
        )
    }
    if (dividersProperties.xAxisProperties.enabled && dividersProperties.enabled) {
        drawLine(
            brush = dividersProperties.xAxisProperties.color,
            start = Offset(0f + xPadding, _size.height),
            end = Offset(_size.width + xPadding, _size.height),
            strokeWidth = dividersProperties.xAxisProperties.thickness.toPx(),
            pathEffect = dividersProperties.xAxisProperties.style.pathEffect
        )
    }
    if (dividersProperties.yAxisProperties.enabled && dividersProperties.enabled) {
        drawLine(
            brush = dividersProperties.yAxisProperties.color,
            start = Offset(0f + xPadding, 0f),
            end = Offset(0f + xPadding, _size.height),
            strokeWidth = dividersProperties.yAxisProperties.thickness.toPx(),
            pathEffect = dividersProperties.yAxisProperties.style.pathEffect
        )
    }
}