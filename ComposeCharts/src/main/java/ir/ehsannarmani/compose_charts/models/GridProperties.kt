package ir.ehsannarmani.compose_charts.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class GridProperties(
    val enabled: Boolean = true,
    val style: GridLineStyle = GridLineStyle.Normal,
    val color: Color = Color.Gray,
    val strokeWidth: Dp = (.5).dp,
    val lineCount:Int = 5
)
