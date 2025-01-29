package ir.ehsannarmani.compose_charts.extensions

import androidx.compose.ui.geometry.Offset
import kotlin.math.pow

fun isInsideCircle(touchTapOffset: Offset, pieceOffset: Offset, radius: Float): Boolean {
    // Calculate the squared distance between the touch point and the circle's center
    val distanceSquared =
        (touchTapOffset.x - pieceOffset.x).pow(2) + (touchTapOffset.y - pieceOffset.y).pow(2)

    // Compare the squared distance with the squared radius to avoid using sqrt (more efficient)
    return distanceSquared <= radius.pow(2)
}