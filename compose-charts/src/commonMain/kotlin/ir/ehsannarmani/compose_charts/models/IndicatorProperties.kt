package ir.ehsannarmani.compose_charts.models

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.ehsannarmani.compose_charts.extensions.format

sealed class IndicatorProperties(
    open val enabled:Boolean,
    open val textStyle: TextStyle,
    open val count: IndicatorCount,
    open val position: IndicatorPosition,
    open val padding: Dp,
    open val contentBuilder: (Double) -> String,
    open val indicators:List<Double> = emptyList()
)


data class VerticalIndicatorProperties(
    override val enabled: Boolean = true,
    override val textStyle: TextStyle = TextStyle.Default.copy(fontSize = 12.sp),
    override val count: IndicatorCount = IndicatorCount.CountBased(count = 5),
    override val position: IndicatorPosition.Vertical = IndicatorPosition.Vertical.Bottom,
    override val padding: Dp = 12.dp,
    override val contentBuilder: (Double) -> String = {
        it.format(1)
    },
    override val indicators: List<Double> = emptyList()
) : IndicatorProperties(
    enabled = enabled,
    textStyle = textStyle,
    count = count,
    position = position,
    contentBuilder = contentBuilder,
    padding = padding,
    indicators = indicators
)

data class HorizontalIndicatorProperties(
    override val enabled: Boolean = true,
    override val textStyle: TextStyle = TextStyle.Default.copy(fontSize = 12.sp),
    override val count: IndicatorCount = IndicatorCount.CountBased(count = 5),
    override val position: IndicatorPosition.Horizontal = IndicatorPosition.Horizontal.Start,
    override val padding: Dp = 12.dp,
    override val contentBuilder: (Double) -> String = {
        it.format(1)
    },
    override val indicators: List<Double> = emptyList(),
) : IndicatorProperties(
    enabled = enabled,
    textStyle = textStyle,
    count = count,
    position = position,
    contentBuilder = contentBuilder,
    padding = padding ,
    indicators = indicators
)

sealed interface IndicatorPosition {
    enum class Vertical : IndicatorPosition {
        Top,
        Bottom
    }
    enum class Horizontal: IndicatorPosition {
        Start,
        End
    }
}

sealed class IndicatorCount {
    data class CountBased(val count: Int) : IndicatorCount()
    data class StepBased(val stepBy: Double) : IndicatorCount()
}