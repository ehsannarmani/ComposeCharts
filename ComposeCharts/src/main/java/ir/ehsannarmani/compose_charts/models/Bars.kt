package ir.ehsannarmani.compose_charts.models

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Bars(
    val label: String,
    val values: List<Data>
) {
    data class Data(
        val label: String? = null,
        val value: Double,
        val color: Color,
        val barStroke: Dp? = null,
        val barSpacing: Dp? = null,
        val barRadius: Radius? = null,
        val animationSpec: AnimationSpec<Float>? = null,
        val animator:Animatable<Float, AnimationVector1D> = Animatable(0f)
    ) {
        sealed class Radius() {
            data object None : Radius()
            data class Circular(val radius: Dp) : Radius()
            data class Rectangle(
                val topLeft: Dp = 0.dp,
                val topRight: Dp = 0.dp,
                val bottomLeft: Dp = 0.dp,
                val bottomRight: Dp = 0.dp
            ) : Radius()
        }
    }
    sealed class AnimationMode{
        data class Together(val delayBuilder:(index:Int)->Long = {0}):AnimationMode()
        data object OneByOne:AnimationMode()
    }
}