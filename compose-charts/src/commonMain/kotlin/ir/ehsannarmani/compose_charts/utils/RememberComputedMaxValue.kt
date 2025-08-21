package ir.ehsannarmani.compose_charts.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import ir.ehsannarmani.compose_charts.models.IndicatorCount

@Composable
fun rememberComputedMaxValue(minValue: Double, maxValue: Double, count: IndicatorCount) : Double {
    return remember(minValue, maxValue, count) {
        when (count) {
            is IndicatorCount.CountBased -> maxValue
            is IndicatorCount.StepBased -> {
                val span = maxValue - minValue
                val remainder = span % count.stepBy

                if (remainder == 0.0) {
                    maxValue
                } else {
                    maxValue + (count.stepBy - remainder)
                }
            }
        }
    }
}