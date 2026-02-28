package ir.ehsannarmani.compose_charts.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.TextLayoutResult
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


fun Float.toRadians(): Float {
    return this * PI.toFloat() / 180
}

fun Offset.rotate(degrees: Float): Offset {
    val c = cos(degrees.toRadians())
    val s = sin(degrees.toRadians())
    return Offset(x * c - y * s, x * s + y * c)
}

data class Bounds(
    val left: Float,
    val right: Float,
    val top: Float,
    val bottom: Float,
) {
    constructor(center: Offset, width: Float, height: Float) : this(
        top = center.y - height / 2f,
        bottom = center.y + height / 2f,
        left = center.x - width / 2f,
        right = center.x + width / 2f
    )

    val width = right - left
    val height = bottom - top
    val center: Offset = Offset((right + left) / 2, (top + bottom) / 2)
    val corners: List<Offset> = listOf(
        Offset(left, top),
        Offset(left, bottom),
        Offset(right, top),
        Offset(right, bottom),
    )

    fun rotate(degrees: Float): Bounds {
        val rotated = corners.map { (it - center).rotate(degrees) }
        val yValues = rotated.map { it.y }
        val xValues = rotated.map { it.x }
        return Bounds(center, xValues.max() - xValues.min(), yValues.max() - yValues.min())
    }

    operator fun plus(offset: Offset): Bounds {
        return Bounds(
            center = center + offset,
            width = width,
            height = height
        )
    }
}

val TextLayoutResult.bounds: Bounds
    get() = Bounds(Offset(0f, 0f), size.width.toFloat(), size.height.toFloat())