package ir.ehsannarmani.compose_charts.models

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.random.Random

data class Bars(
    val label: String,
    val values: List<Data>
) {
    data class Data(
        val id:Int = Random.nextInt(0, 999999),
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

        sealed class RadiusPx {
            data object None : RadiusPx()
            data class Circular(val radius: Float) : RadiusPx()
            data class Rectangle(
              val topLeft: Float = 0f,
              val topRight: Float = 0f,
              val bottomLeft: Float = 0f,
              val bottomRight: Float = 0f
            ) : RadiusPx()
        }
    }
}

fun Bars.Data.Radius.asRadiusPx(density: Density): Bars.Data.RadiusPx {
  with(density) {
    return when (this@asRadiusPx) {
      is Bars.Data.Radius.None -> Bars.Data.RadiusPx.None
      is Bars.Data.Radius.Circular -> Bars.Data.RadiusPx.Circular(radius.toPx())
      is Bars.Data.Radius.Rectangle -> Bars.Data.RadiusPx.Rectangle(
        topLeft = topLeft.toPx(),
        topRight = topRight.toPx(),
        bottomLeft = bottomLeft.toPx(),
        bottomRight = bottomRight.toPx()
      )
    }
  }
}
