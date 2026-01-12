package ir.ehsannarmani.compose_charts.models

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class ZeroLineProperties(
    val enabled: Boolean = false,
    val style: StrokeStyle = StrokeStyle.Normal,
    val color: Brush = SolidColor(Color.Gray),
    val thickness: Dp = (.5).dp,
    val animationSpec: AnimationSpec<Float> = tween(durationMillis = 1000, delayMillis = 300),
    val zType: ZType = ZType.Under
){
    enum class ZType{
        Under, Above
    }
}
