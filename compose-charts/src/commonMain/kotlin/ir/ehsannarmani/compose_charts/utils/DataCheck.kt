package ir.ehsannarmani.compose_charts.utils

import ir.ehsannarmani.compose_charts.models.Bars

/**
 * RC means Row & Column
 */
internal fun checkRCMaxValue(maxValue: Double, data: List<Bars>) {
    require(maxValue >= (data.maxOfOrNull { it.values.maxOfOrNull { it.value } ?: 0.0 } ?: 0.0)) {
        "Chart data must be at most $maxValue (Specified Max Value)"
    }
}

internal fun checkRCMinValue(minValue: Double, data: List<Bars>) {
    require(minValue <= 0) {
        "Min value in column chart must be 0 or lower."
    }
    require(minValue <= (data.minOfOrNull { it.values.minOfOrNull { it.value } ?: 0.0 } ?: 0.0)) {
        "Chart data must be at least $minValue (Specified Min Value)"
    }
}