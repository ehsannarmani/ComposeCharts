package ir.ehsannarmani.compose_charts.ui

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import ir.ehsannarmani.compose_charts.models.*

val labelHelperProperties: LabelHelperProperties
    @Composable get() =
        LabelHelperProperties(textStyle = MaterialTheme.typography.labelMedium.copy(color = LocalContentColor.current))
val labelProperties: LabelProperties
    @Composable get() = LabelProperties(
        enabled = true,
        textStyle = MaterialTheme.typography.labelMedium.copy(color = LocalContentColor.current)
    )

val indicatorProperties: HorizontalIndicatorProperties
    @Composable
    get() = HorizontalIndicatorProperties(
        textStyle = MaterialTheme.typography.labelMedium.copy(color = LocalContentColor.current),
        count = IndicatorCount.CountBased(count = 4),
        position = IndicatorPosition.Horizontal.Start,
    )