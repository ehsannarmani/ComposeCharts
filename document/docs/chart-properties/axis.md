# Axis Properties

| Property    | Type        | Default              | Description                      |
|-------------|-------------|----------------------|----------------------------------|
| `enabled`   | Boolean     | `true`               | specifies axis line visibility   |
| `style`     | StrokeStyle | `StrokeStyle.Normal` | specifies axis line style        |
| `color`     | Color       | `Color.Gray`         | specifies axis line color        |
| `thickness` | Dp          | `(0.5).dp`           | specifies axis line stroke width |
| `lineCount` | Int         | `5`                  | specifies count of axis lines    |

!!! Example
```kotlin linenums="1"
val axisProperties = AxisProperties(
enabled = true,
style = StrokeStyle.Dashed(intervals = floatArrayOf(10f,10f)),
color = Color.Gray,
thickness = (.5).dp,
lineCount = 5
)
```