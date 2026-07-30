package ir.ehsannarmani.compose_charts.models

import androidx.compose.ui.unit.Dp

/**
 * Controls the scrolling behavior of bar charts (ColumnChart and RowChart).
 */
sealed interface ScrollMode {

    /**
     * No scrolling. Bars are sized to fit the available space.
     * This is the legacy behavior.
     */
    data object Disabled : ScrollMode

    /**
     * Automatically enables scrolling when bars would be too compressed.
     *
     * @param maxBarSpacing The desired spacing between bar groups when scrolling is active.
     *   If the natural spacing in the non-scrollable layout is less than this value,
     *   the chart switches to scrollable mode using [maxBarSpacing] as the actual spacing,
     *   expanding the content beyond the viewport.
     */
    data class Auto(
        val maxBarSpacing: Dp
    ) : ScrollMode

    /**
     * The chart is always laid out with the specified spacing and scroll is enabled.
     * Bars are never compressed to fit the viewport.
     *
     * @param barSpacing The fixed spacing between consecutive bar groups.
     */
    data class Always(
        val barSpacing: Dp
    ) : ScrollMode
}
