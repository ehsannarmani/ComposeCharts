# Popup Properties

!!! info "Usage"
    In every chart you can set properties of popup which shown when user click or drag on chart

| Property                          | Type                     | Default                 | Description                                                                                          |
|-----------------------------------|--------------------------|-------------------------|------------------------------------------------------------------------------------------------------|
| `enabled`                         | Boolean                  | `true`                  | specifies popup visibility                                                                           |
| `animationSpec`                   | AnimationSpec<Float>     | `tween(400)`            | specifies popup visibility animation spec                                                            |
| `duration`                        | Long                     | `1500`                  | in column/row charts, specifies how long the popup will be visible                                   |
| `textStyle`                       | TextStyle                | `TextStyle.Default`     | specifies popup text style                                                                           |
| `containerColor`                  | Color                    | `Color(0xff313131)`     | specifies popup background color                                                                     |
| `cornerRadius`                    | Dp                       | `6.dp`                  | specifies popup corner radius                                                                        |
| `contentHorizontalPadding`        | Dp                       | `4.dp`                  | specifies popup horizontal padding                                                                   |
| `contentVerticalPadding`          | Dp                       | `2.dp`                  | specifies popup vertical padding                                                                     |
| `mode`                            | PopupProperties.Mode     | `Mode.Normal`           | specifies popup visibility mode ( you can pass PointMode to make popup visible just on points )      |
| `contentBuilder`                  | (Int,Int,Double)->String | `{ "%.2f".format(it) }` | gives data index, value index and value and expect string to be returned ( specifies popup content ) |

!!! tip "Tip"
    In line charts your can set specific popup properties for each line, for example you can disable one
    of lines popup
    and e.g.

!!! Example
    ```kotlin linenums="1"
    val popupProperties = PopupProperties(
        enabled = true,
        animationSpec = tween(300),
        duration = 2000L,
        textStyle = MaterialTheme.typography.labelSmall,
        containerColor = Color.White,
        cornerRadius = 8.dp,
        contentHorizontalPadding = 4.dp,
        contentVerticalPadding = 2.dp,
        contentBuilder = { dataIndex, valueIndex, value->
            // data index: which data? (when you have more than one data item in your chart)
            // value index: which value?
            value.format(1)+" Million"
        }
    )
    ```
