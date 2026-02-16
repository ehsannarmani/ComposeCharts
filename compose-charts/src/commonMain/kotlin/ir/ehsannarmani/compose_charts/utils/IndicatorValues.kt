package ir.ehsannarmani.compose_charts.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import ir.ehsannarmani.compose_charts.models.IndicatorCount

@Composable
internal fun rememberComputedChartMaxValue(minValue: Double, maxValue: Double, count: IndicatorCount) : Double {
    return remember(minValue, maxValue, count) {
        computeChartMaxValue(minValue, maxValue, count)
    }
}

internal fun computeChartMaxValue(minValue: Double, maxValue: Double, count: IndicatorCount): Double {
    return when (count) {
        is IndicatorCount.CountBased -> maxValue
        is IndicatorCount.StepBased -> {
            /**
             * Why we are doing this?
             * if maxValue-minValue not dividable for `stepBy` (grater than zero remainder),
             * the generated numbers for indicators will have inconsistency
             * for example: (minValue=0,maxValue=5,stepBy=2) the generated indicator numbers will be: (5,3,1,0)
             * we will have inconsistency between 0 and 1 compared to other numbers, so indicators will show incorrect data in the end
             * so we check, if maxValue-minValue is not dividable for stepBy, we add the remainder to maxValue to avoid this issue
             */
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