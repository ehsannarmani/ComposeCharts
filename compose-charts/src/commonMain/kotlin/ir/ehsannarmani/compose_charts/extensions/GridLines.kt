package ir.ehsannarmani.compose_charts.extensions

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import ir.ehsannarmani.compose_charts.models.DividerProperties
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.IndicatorPosition

fun DrawScope.drawGridLines(
    dividersProperties: DividerProperties,
    indicatorPosition: IndicatorPosition,
    gridEnabled: Boolean,
    xAxisProperties: GridProperties.AxisProperties,
    yAxisProperties: GridProperties.AxisProperties,
    size: Size? = null,
    xPadding: Float = 0f,
    yPadding: Float = 0f
) {

    val _size = size ?: this.size

    val xAxisPathEffect = xAxisProperties.style.pathEffect
    val yAxisPathEffect = yAxisProperties.style.pathEffect


    if (xAxisProperties.enabled && gridEnabled) {
        for (i in 0 until xAxisProperties.lineCount) {
            val y = _size.height.spaceBetween(itemCount = xAxisProperties.lineCount, index = i)
            drawLine(
                brush = xAxisProperties.color,
                start = Offset(0f + xPadding, y + yPadding),
                end = Offset(_size.width + xPadding, y + yPadding),
                strokeWidth = xAxisProperties.thickness.toPx(),
                pathEffect = xAxisPathEffect
            )
        }
    }
    if (yAxisProperties.enabled && gridEnabled) {
        for (i in 0 until yAxisProperties.lineCount) {
            val x = _size.width.spaceBetween(itemCount = yAxisProperties.lineCount, index = i)
            drawLine(
                brush = yAxisProperties.color,
                start = Offset(x + xPadding, 0f + yPadding),
                end = Offset(x + xPadding, _size.height + yPadding),
                strokeWidth = yAxisProperties.thickness.toPx(),
                pathEffect = yAxisPathEffect
            )
        }
    }
    if (dividersProperties.xAxisProperties.enabled && dividersProperties.enabled) {
        val y = if (indicatorPosition == IndicatorPosition.Vertical.Top) 0f else _size.height
        drawLine(
            brush = dividersProperties.xAxisProperties.color,
            start = Offset(0f + xPadding, y + yPadding),
            end = Offset(_size.width + xPadding, y + yPadding),
            strokeWidth = dividersProperties.xAxisProperties.thickness.toPx(),
            pathEffect = dividersProperties.xAxisProperties.style.pathEffect
        )
    }
    if (dividersProperties.yAxisProperties.enabled && dividersProperties.enabled) {
        val x = if (indicatorPosition == IndicatorPosition.Horizontal.End)  _size.width else  0f
        drawLine(
            brush = dividersProperties.yAxisProperties.color,
            start = Offset(x + xPadding, 0f + yPadding),
            end = Offset(x + xPadding, _size.height + yPadding),
            strokeWidth = dividersProperties.yAxisProperties.thickness.toPx(),
            pathEffect = dividersProperties.yAxisProperties.style.pathEffect
        )
    }
}