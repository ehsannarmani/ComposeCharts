# Bar Properties

!!! info "Usage"
    In Column/Row Charts you can set bar properties with this property

| Property       | Type             | Default                 | Description                                                                 |
|----------------|------------------|-------------------------|-----------------------------------------------------------------------------|
| `thickness`    | Dp               | `20`                    | specifies bar width                                                         |
| `spacing`      | Dp               | `4`                     | specifies space between data bars when you have more than one bar in a data |
| `cornerRadius` | Bars.Data.Radius | `Bars.Data.Radius.None` | specifies space between data bars when you have more than one bar in a data |
| `style`        | DrawStyle        | `DrawStyle.Fill`        | specifies bar style                                                         |\

!!! Example
    ```kotlin linenums="1"
    val barProperties = BarProperties(
        thickness = 15.dp,
        spacing = 4.dp,
        cornerRadius = Bars.Data.Radius.Circular(6.dp),
        style = DrawStyle.Fill
    )
    ```