# Dot Properties]

!!! info "Usage"
    In Line Charts you can set data dot shape properties with this property

| Property           | Type                 | Default                           | Description                                                      |
|--------------------|----------------------|-----------------------------------|------------------------------------------------------------------|
| `enabled`          | Boolean              | `false`                           | specifies dots visibility                                        |
| `radius`           | Dp                   | `3.dp`                            | specifies dot size                                               |
| `color`            | Brush                | ` SolidColor(Color.Unspecified) ` | specifies dot color                                              |
| `strokeWidth`      | Dp                   | `2.dp`                            | specifies dot stroke width                                       |
| `strokeColor`      | Brush                | ` SolidColor(Color.Unspecified) ` | specifies dot stroke color                                       |
| `strokeStyle`      | StrokeStyle          | `StrokeStyle.Normal`              | specifies dot stroke style                                       |
| `animationEnabled` | Boolean              | `true`                            | set `false` if you want to show dots without delay and animation |
| `animationSpec`    | AnimationSpec<Float> | `tween(300)`                      | specifies dots visibility animation spec                         |\

!!! Example
    ```kotlin linenums="1"
    val dotProperties = DotProperties(
        enabled = true,
        radius = 4.dp,
        color = SolidColor(Color.Red),
        strokeWidth = 3.dp,
        strokeColor = Color.White,
        strokeStyle = StrokeStyle.Normal,
        animationEnabled = true,
        animationSpec = tween(500)
    )
    ```