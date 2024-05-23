package ir.ehsannarmani.compose_charts.models

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor

data class DotProperties(
    val enabled: Boolean = false,
    val radius: Float = 10f,
    val color: Brush = SolidColor(Color.Unspecified),
    val strokeWidth: Float = 3f,
    val strokeColor: Brush = SolidColor(Color.Unspecified),
    val strokeStyle: StrokeStyle = StrokeStyle.Normal,
    val animationEnabled:Boolean = true,
    val animationSpec: AnimationSpec<Float> = tween(300)
)