package ir.ehsannarmani.compose_charts.models

import androidx.compose.ui.text.TextStyle

data class IndicatorProperties(
    val enabled:Boolean = true,
    val textStyle: TextStyle,
    val count: Int = 4,
    val contentBuilder: (Double) -> String = {
        "%.1f".format(it)
    },
)
