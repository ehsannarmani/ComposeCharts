package ir.ehsannarmani.compose_charts.utils

import androidx.compose.ui.unit.Density
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
            val maxSpacingPx = with(density) { scrollMode.maxBarSpacing.toPx() }
            // Natural step between consecutive group start positions
            val naturalStep = (availableSize - everyDataSize) / (dataCount - 1)

            if (naturalStep < maxSpacingPx) {
                // Bars are too compressed — expand content using maxBarSpacing
                val effectiveSize = everyDataSize + (dataCount - 1) * maxSpacingPx
                ScrollConfig(effectiveSize, scrollEnabled = effectiveSize > availableSize)
            } else {
                // Enough room — keep the normal non-scrollable layout
                ScrollConfig(availableSize, scrollEnabled = false)
            }
        }

        is ScrollMode.Always -> {
            val spacingPx = with(density) { scrollMode.barSpacing.toPx() }
            val effectiveSize = everyDataSize + (dataCount - 1) * spacingPx
            ScrollConfig(effectiveSize, scrollEnabled = true)
        }
    }
}
