package ir.ehsannarmani.compose_charts.extensions

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import ir.ehsannarmani.compose_charts.models.Pie
import kotlin.math.pow


internal fun Density.isInsidePie(
    touchTapOffset: Offset,
    pieceOffset: Offset,
    radius: Float,
    style: Pie.Style?,
): Boolean {
    val distanceSquared =
        (touchTapOffset.x - pieceOffset.x).pow(2) +
                (touchTapOffset.y - pieceOffset.y).pow(2)

    return when (style) {
        is Pie.Style.Stroke -> {
            val half = style.width.toPx() / 2f
            val inner = (radius - half).coerceAtLeast(0f)
            val outer = radius + half
            distanceSquared in (inner.pow(2))..(outer.pow(2))
        }
        else -> {
            distanceSquared <= radius.pow(2)
        }
    }
}