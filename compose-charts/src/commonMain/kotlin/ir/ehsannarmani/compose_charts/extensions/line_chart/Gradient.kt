package ir.ehsannarmani.compose_charts.extensions.line_chart

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.DrawScope

internal fun DrawScope.drawLineGradient(
    path: Path,
    color1: Color,
    color2: Color,
    progress: Float,
    size: Size? = null,
    startOffset: Float,
    endOffset: Float,
) {
    val _size = size ?: this.size
    val filled = Path().apply {
        addPath(path)
        lineTo(endOffset, _size.height)
        lineTo(startOffset, _size.height)
        close()
    }
    drawPath(
        path = filled,
        brush = Brush.verticalGradient(
            colors = listOf(
                color1.copy(alpha = color1.alpha * progress),
                color2.copy(alpha = color2.alpha * progress),
            ),
            startY = 0f,
            endY = _size.height,
            tileMode = TileMode.Mirror,
        ),
    )
}
