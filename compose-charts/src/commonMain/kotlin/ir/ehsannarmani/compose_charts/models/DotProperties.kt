package ir.ehsannarmani.compose_charts.models

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class DotProperties(
    val enabled: Boolean = false,
    val radius: Dp = 3.dp,
    val color: Brush = SolidColor(Color.Unspecified),
    val strokeWidth: Dp = 2.dp,
    val strokeColor: Brush = SolidColor(Color.Unspecified),
    val strokeStyle: StrokeStyle = StrokeStyle.Normal,
    val animationEnabled:Boolean = true,
    val animationSpec: AnimationSpec<Float> = tween(300),
    val confirmDraw: (dataIndex: Int, valueIndex: Int, value: Double) -> Boolean = { dataIndex, valueIndex, value ->
        true
    }
)