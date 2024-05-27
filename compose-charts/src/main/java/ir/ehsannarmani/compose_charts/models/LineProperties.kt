package ir.ehsannarmani.compose_charts.models

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class LineProperties(
    val enabled: Boolean = true,
    val style: StrokeStyle = StrokeStyle.Normal,
    val color: Brush = SolidColor(Color.Gray),
    val thickness: Dp = (.5).dp,
)
