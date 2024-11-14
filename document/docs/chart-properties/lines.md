# Line Properties

| Property    | Type        | Default              | Description                      |
|-------------|-------------|----------------------|----------------------------------|
| `enabled`   | Boolean     | `true`               | specifies axis line visibility   |
| `style`     | StrokeStyle | `StrokeStyle.Normal` | specifies axis line style        |
| `color`     | Color       | `Color.Gray`         | specifies axis line color        |
| `thickness` | Dp          | `(0.5).dp`           | specifies axis line stroke width |

!!! Example
    ```kotlin linenums="1"
    val lineProperties = LineProperties(
        enabled = true,
        style = StrokeStyle.Dashed(intervals = floatArrayOf(10f,10f)),
        color = Color.Gray,
        thickness = (.5).dp,
    )
    ```