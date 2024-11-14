# Column Chart

<div>
   <img src="https://github.com/ehsannarmani/ComposeCharts/blob/master/assets/charts/column1.gif?raw=true" width="300">
   <img src="https://github.com/ehsannarmani/ComposeCharts/blob/master/assets/charts/column2.gif?raw=true" width="300">
</div>

```kotlin linenums="1"
ColumnChart(
    modifier = Modifier.fillMaxSize().padding(horizontal = 22.dp),
    data = remember {
        listOf(
            Bars(
                label = "Jan",
                values = listOf(
                    Bars.Data(label = "Linux", value = 50.0, color = Brush.verticalGradient(...),
                    Bars.Data(label = "Windows", value = 70.0, color = SolidColor(Color.Red))
                ),
            ),
            Bars(
                label = "Feb",
                values = listOf(
                    Bars.Data(label = "Linux", value = 80.0, color = Brush.verticalGradient(...),
                    Bars.Data(label = "Windows", value = 60.0, color = SolidColor(Color.Red))
                ),
            )
        )
    },
    barProperties = BarProperties(
        radius = Bars.Data.Radius.Rectangle(topRight = 6.dp, topLeft = 6.dp),
        spacing = 3.dp,
        strokeWidth = 20.dp
    ),
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    ),
)
```

### Data Count

You can set how many data you want for every bar:

<img src="https://github.com/ehsannarmani/ComposeCharts/blob/master/assets/charts/column3.gif?raw=true" width="300">

```kotlin linenums="1"
ColumnChart(
    data = remember {
        listOf(
            Bars(
                label = "1",
                values = listOf(
                    Bars.Data(value = 10.0, color = Color.Blue)
                )
            ),
            Bars(
                label = "2",
                values = listOf(
                    Bars.Data(value = 20.0, color = Color.Blue)
                )
            ),
            ...
        )
    },
    barProperties = BarProperties(
        spacing = 1.dp,
        strokeWidth = 10.dp,
    ),
    ...
)
```

### Negative Values

You can set negative values for this chart and define max value and min value:

<img src="https://github.com/ehsannarmani/ComposeCharts/blob/master/assets/charts/column_negative.gif?raw=true" width="300">

```kotlin linenums="1" hl_lines="7 19 20"
ColumnChart(
    data = remember {
        listOf(
            Bars(
                label = "1",
                values = listOf(
                    Bars.Data(value = -40.0, color = Color.Blue)
                )
            ),
            Bars(
                label = "2",
                values = listOf(
                    Bars.Data(value = 50.0, color = Color.Blue)
                )
            ),
            ...
        )
    },
    maxValue = 75.0,
    minValue = -75.0
    ...
)
```

By default, max value is the highest value of given data, min value is 0 when there is no value
under the zero in given data, otherwise if there is value under zero, min value will be (-maxValue)
