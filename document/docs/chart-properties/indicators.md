# Indicator Properties

!!! info "Usage"
    In every chart you can set properties of counters next to the chart

| Property         | Type               | Default                        | Description                                                                                                      |
|------------------|--------------------|--------------------------------|------------------------------------------------------------------------------------------------------------------|
| `enabled`        | Boolean            | `true`                         | specifies indicator visiblity                                                                                    |
| `textStyle`      | TextStyle          | `TextStyle.Default`            | specifies counter style                                                                                          |
| `count`          | IndicatorCount     | `IndicatorCount.CountBased(5)` | specifies counters type and count                                                                                |
| `position`       | IndicatorPosition  | `Depends on chart`             | specifies indicator position, in line & column charts can be: start or end, in line charts can be: top or bottom |
| `padding`        | Dp                 | `12.dp`                        | specifies indicator area padding with chart area                                                                 |
| `contentBuilder` | (Double) -> String | `{ "%.2f".format(it) }`        | specifies counter content creation template                                                                      |

!!! Example
    === "Line & Column Chart"
        ```kotlin linenums="1"
        val indicatorProperties = HorizontalIndicatorProperties(
            enabled = true,
            textStyle = MaterialTheme.typography.labelSmall,
            count = IndicatorCount.CountBased(count = 5),
            position = IndicatorPosition.Horizontal.End,
            padding = 32.dp,
            contentBuilder = { indicator ->
                "%.2f".format(indicator) + " Million"
            }
        )
        ```
    === "Row Chart"
        ```kotlin linenums="1"
        val indicatorProperties = VerticalIndicatorProperties(
            enabled = true,
            textStyle = MaterialTheme.typography.labelSmall,
            count = IndicatorCount.CountBased(count = 5),
            position = IndicatorPosition.Vertical.Top,
            padding = 32.dp,
            contentBuilder = { indicator ->
                "%.2f".format(indicator) + " Million"
            }
        )
        ```

<hr/>

### Indicators Bases

!!! tip "You can specify type of indicator counts, we have two type: CountBased, StepBased"
    - <strong>`CountBased`</strong>: it will receive a count number and will divide & calculate and in
    the end it will show the requested count
    of indicators.
    
    - <strong>`StepBased`</strong>: it will receive a stepBy value and will split step by given value until it reach min value, for example
    if (max value = 20, min value = -10) and stepBy value is 5, the indicators will be: 20,15,10,5,0,-5,-10

