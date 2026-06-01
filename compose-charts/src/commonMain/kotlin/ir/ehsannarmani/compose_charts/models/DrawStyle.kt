package ir.ehsannarmani.compose_charts.models

import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.drawscope.DrawStyle as CanvasDrawStyle

sealed class DrawStyle() {
    data class Stroke(val width: Dp = 2.dp, val strokeStyle: StrokeStyle = StrokeStyle.Normal) :
        DrawStyle()


    data object Fill : DrawStyle()

    fun getStyle(density: Float): CanvasDrawStyle{
        return when (this) {
            is Stroke -> {
                Stroke(
                    width = width.value * density,
                    pathEffect = strokeStyle.pathEffect
                )
            }
            is Fill -> {
                androidx.compose.ui.graphics.drawscope.Fill
            }
        }
    }
}
