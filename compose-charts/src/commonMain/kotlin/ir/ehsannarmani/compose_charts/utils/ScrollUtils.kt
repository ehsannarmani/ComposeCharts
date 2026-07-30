package ir.ehsannarmani.compose_charts.utils

import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.ScrollMode

/**
 * Holds the computed scroll configuration for a chart axis.
 *
 * @param effectiveSize The total content size in pixels along the scroll axis.
 * @param scrollEnabled Whether scrolling should be active.
 */
internal data class ScrollConfig(
    val effectiveSize: Float,
    val scrollEnabled: Boolean
)

/**
 * Computes the scroll configuration for a chart based on the [scrollMode],
 * available viewport size, data dimensions, and item count.
 *
 * @param scrollMode The requested scroll behavior.
 * @param availableSize The viewport size in pixels along the scroll axis.
 * @param everyDataSize The width (or height) in pixels occupied by one data group's bars
 *   (sum of thickness + spacing for all values in the widest group).
 * @param dataCount The number of data groups (bar clusters).
 * @param density The current [Density] for Dp-to-px conversion.
 */
internal fun computeScrollConfig(
    scrollMode: ScrollMode,
    availableSize: Float,
    everyDataSize: Float,
    dataCount: Int,
    density: Density
): ScrollConfig {
    if (dataCount <= 1) return ScrollConfig(availableSize, scrollEnabled = false)

    return when (scrollMode) {
        is ScrollMode.Disabled -> ScrollConfig(availableSize, scrollEnabled = false)

        is ScrollMode.Auto -> {
            val targetGapPx = with(density) { scrollMode.maxBarSpacing.toPx() }
            // Natural step between consecutive group start positions in disabled layout
            val naturalStep = (availableSize - everyDataSize) / (dataCount - 1)
            // Natural empty gap between consecutive groups
            val naturalGap = naturalStep - everyDataSize

            if (naturalGap < targetGapPx) {
                // Bars are too compressed — expand content using targetGapPx as empty space between bars
                val targetStep = everyDataSize + targetGapPx
                val effectiveSize = everyDataSize + (dataCount - 1) * targetStep
                ScrollConfig(effectiveSize, scrollEnabled = effectiveSize > availableSize)
            } else {
                // Enough room — keep the normal non-scrollable layout
                ScrollConfig(availableSize, scrollEnabled = false)
            }
        }

        is ScrollMode.Always -> {
            val targetGapPx = with(density) { scrollMode.barSpacing.toPx() }
            val targetStep = everyDataSize + targetGapPx
            val effectiveSize = everyDataSize + (dataCount - 1) * targetStep
            ScrollConfig(effectiveSize, scrollEnabled = true)
        }
    }
}

/**
 * Calculates the total size in pixels occupied by a single data group's bars
 * (sum of thickness + spacing for all bars in the widest/tallest group).
 */
internal fun calculateEveryDataSize(
    data: List<Bars>,
    defaultBarProperties: BarProperties,
    density: Density
): Float = with(density) {
    data.maxOfOrNull { group ->
        group.values.sumOf { bar ->
            ((bar.properties?.thickness ?: defaultBarProperties.thickness).toPx() +
             (bar.properties?.spacing ?: defaultBarProperties.spacing).toPx()).toDouble()
        }.toFloat()
    } ?: 0f
}

/**
 * Calculates the average bar spacing across all bars in pixel space.
 */
internal fun calculateAverageSpacingBetweenBars(
    data: List<Bars>,
    defaultBarProperties: BarProperties,
    density: Density
): Double = with(density) {
    data.flatMap { it.values }
        .map { (it.properties?.spacing ?: defaultBarProperties.spacing).toPx().toDouble() }
        .average()
}

/**
 * Calculates the maximum text dimension (height or width) in Dp for labels, including padding.
 */
internal fun calculateLabelAreaDimensionDp(
    labelProperties: LabelProperties,
    data: List<Bars>,
    textMeasurer: TextMeasurer,
    density: Density,
    isWidth: Boolean
): Dp {
    if (!labelProperties.enabled) return 0.dp
    val labels = labelProperties.labels.ifEmpty { data.map { it.label } }
    if (labels.isEmpty()) return 0.dp

    val maxDimension = labels.maxOf { label ->
        val measuredSize = textMeasurer.measure(label, style = labelProperties.textStyle).size
        if (isWidth) measuredSize.width else measuredSize.height
    }
    return (maxDimension / density.density).dp + labelProperties.padding
}
