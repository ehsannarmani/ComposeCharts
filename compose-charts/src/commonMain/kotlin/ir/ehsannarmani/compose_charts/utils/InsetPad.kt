package ir.ehsannarmani.compose_charts.utils

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toSize

data class InsetPad(
    var left: Float,
    var right: Float,
    var top: Float,
    var bottom: Float,
    val shouldRotate: Boolean
) {
    fun width(size: IntSize): Float {
        return size.width - left - right
    }

    fun height(size: IntSize): Float {
        return size.height - top - bottom
    }

    fun toBounds(size: Size) = Bounds(
        left, size.width - right, top, size.height - bottom
    )
    fun toBounds(size: IntSize) = toBounds(size.toSize())
}