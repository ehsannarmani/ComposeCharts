# Scroll Mode

!!! info "Usage"
    In `ColumnChart` and `RowChart`, you can control chart scrolling behavior using the `scrollMode` parameter.

| Mode | Parameters | Default | Description |
|------|------------|---------|-------------|
| `ScrollMode.Disabled` | None | Default | Disables scrolling. All bars are fitted within the visible chart area. Drag gestures trigger popups. |
| `ScrollMode.Auto` | `maxBarSpacing: Dp` | `16.dp` | Automatically enables scrolling if bars become too compressed in the available viewport. |
| `ScrollMode.Always` | `barSpacing: Dp` | `16.dp` | Always enables scrolling with a fixed empty gap (`barSpacing`) between adjacent bars. |

---

## How `barSpacing` Works

The `barSpacing` parameter represents the **empty gap between two adjacent bars**, not the distance between their origins.

```
distanceBetweenBarOrigins = barWidth + barSpacing
```

### Examples:

- **`barWidth = 24.dp`**, **`barSpacing = 0.dp`**:
  Bars are directly adjacent with no gap and no overlap.

- **`barWidth = 24.dp`**, **`barSpacing = 8.dp`**:
  An 8.dp empty gap separates consecutive bars.

---

## How `ScrollMode.Auto` Works

In `ScrollMode.Auto(maxBarSpacing)`, the chart evaluates the available viewport size at layout time:

1. It calculates the natural empty gap between consecutive bar groups based on the viewport size and data count.
2. If `naturalGap < maxBarSpacing`, the chart switches to scroll mode and expands its content size so bars maintain the requested `maxBarSpacing`.
3. If `naturalGap >= maxBarSpacing`, the chart remains non-scrollable and fits within the viewport.

---

## Layout & Fixed Layers Behavior

When scrolling is enabled (`ScrollMode.Always` or resolved `ScrollMode.Auto`):

- **Fixed Grid Lines & Axis Indicators**: Remain stationary in the viewport layer and never scroll off-screen.
- **Scrollable Content**: Bars and X/Y labels scroll smoothly behind the fixed grid lines layer.
- **Viewport Bounds**: The outer chart container stays bounded to parent constraints and never overflows horizontally or vertically.

---

## Desktop Mouse Drag-to-Scroll Support

On Desktop platforms (Compose Desktop / JVM):

- **When Scrolling is Enabled**: Users can click and drag anywhere on the chart content area with the mouse left button to grab and scroll horizontally (`ColumnChart`) or vertically (`RowChart`).
- **Mouse Wheel**: Mouse wheel scrolling (and Shift + wheel for horizontal panning) continues to work seamlessly.
- **When Scrolling is Disabled**: Left mouse drag triggers bar popup inspection tooltips as usual.

---

## Code Examples

### 1. Disabled (Default)
```kotlin linenums="1"
ColumnChart(
    data = chartData,
    scrollMode = ScrollMode.Disabled
)
```

### 2. Auto Scroll Mode
```kotlin linenums="1"
ColumnChart(
    data = chartData,
    scrollMode = ScrollMode.Auto(maxBarSpacing = 20.dp)
)
```

### 3. Always Scroll Mode
```kotlin linenums="1"
RowChart(
    data = chartData,
    scrollMode = ScrollMode.Always(barSpacing = 16.dp)
)
```
