package ir.ehsannarmani.compose_charts.models

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect

data class SelectedBar(
    val bar: Bars.Data,
    val offset: Offset,
    val rect: Rect
)