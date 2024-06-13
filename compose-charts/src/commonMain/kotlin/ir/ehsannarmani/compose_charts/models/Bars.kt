package ir.ehsannarmani.compose_charts.models

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.ui.graphics.Brush
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
        val color: Brush,
        val properties: BarProperties? = null,
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

            fun reverse(horizontal:Boolean = false):Radius{
                return when(this){
                    is Circular, is None->{
                        this
                    }
                    is Rectangle->{
                        if (horizontal){
                            copy(
                                topLeft = topRight,
                                topRight = topLeft,
                                bottomLeft = bottomRight,
                                bottomRight = bottomLeft
                            )
                        }else{
                            copy(
                                topLeft = bottomLeft,
                                topRight = bottomRight,
                                bottomLeft = topLeft,
                                bottomRight = topRight
                            )
                        }
                    }
                }
            }
        }
    }
}