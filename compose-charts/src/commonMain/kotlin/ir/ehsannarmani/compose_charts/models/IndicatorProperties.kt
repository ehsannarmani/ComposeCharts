package ir.ehsannarmani.compose_charts.models

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import ir.ehsannarmani.compose_charts.extensions.format

data class IndicatorProperties(
    val enabled:Boolean = true,
    val textStyle: TextStyle = TextStyle.Default.copy(fontSize = 12.sp),
    val count: Int = 5,
    val position: Position = Position.Start,
    val contentBuilder: (Double) -> String = {
        it.format(1)
    }
) {
    enum class Position {
        Start,
        End
    }
}
