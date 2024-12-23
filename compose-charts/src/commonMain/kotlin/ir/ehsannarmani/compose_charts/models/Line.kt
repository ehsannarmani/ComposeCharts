package ir.ehsannarmani.compose_charts.models

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Line(
    val label: String,
    val values: List<Double>,
    val color: Brush,
    val firstGradientFillColor: Color? = null,
    val secondGradientFillColor: Color? = null,
    val drawStyle: DrawStyle = DrawStyle.Stroke(2.dp),
    val strokeAnimationSpec: AnimationSpec<Float> = tween(2000),
    val gradientAnimationSpec: AnimationSpec<Float> = tween(2000),
    val gradientAnimationDelay: Long = 1000,
    val dotProperties: DotProperties? = null,
    val popupProperties: PopupProperties? = null,
    val curvedEdges:Boolean? = null,
    val strokeProgress: Animatable<Float, AnimationVector1D> = Animatable(0f),
    val gradientProgress: Animatable<Float, AnimationVector1D> = Animatable(0f),
    val viewRange: ViewRange = ViewRange()
)