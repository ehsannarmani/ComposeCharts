package ir.ehsannarmani.compose_charts.models

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class BarProperties(
    val strokeWidth: Dp = 15.dp,
    val spacing: Dp = 6.dp,
    val radius: Bars.Data.Radius = Bars.Data.Radius.None,
)
