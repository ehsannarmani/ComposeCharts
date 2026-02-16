# Line Chart

<div>
   <img src="https://github.com/ehsannarmani/ComposeCharts/blob/master/assets/charts/line2.gif?raw=true" width="300">
   <img src="https://github.com/ehsannarmani/ComposeCharts/blob/master/assets/charts/line8.gif?raw=true" width="300">
</div>

```kotlin linenums="1"
LineChart(
    modifier = Modifier.fillMaxSize().padding(horizontal = 22.dp),
    data = remember {
        listOf(
            Line(
                label = "Windows",
                values = listOf(28.0, 41.0, 5.0, 10.0, 35.0),
                color = SolidColor(Color(0xFF23af92)),
                firstGradientFillColor = Color(0xFF2BC0A1).copy(alpha = .5f),
                secondGradientFillColor = Color.Transparent,
                strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                gradientAnimationDelay = 1000,
                drawStyle = DrawStyle.Stroke(width = 2.dp),
            )
        )
    },
    animationMode = AnimationMode.Together(delayBuilder = {
        it * 500L
    }),
)
```

### Zero Line & Negative Values

You can set min & max value for all charts and show zero line:

<img src="https://github.com/ehsannarmani/ComposeCharts/blob/master/assets/charts/zero_line.gif?raw=true" width="300">

```kotlin linenums="1" hl_lines="11 12 13 14 15 16"
LineChart(
    data = remember {
        listOf(
            Line(
                label = "Temperature",
                values = listOf(28.0, 41.0, -15.0, 27.0, 54.0),
                color = Brush.radialGradient(...)
            ),
        )
    },
    zeroLineProperties = LineProperties(
        enabled = true,
        color = SolidColor(Color.Red),
    ),
    minValue = -20.0,
    maxValue = 100.0
)
```

Max value by default is highest value of chart data and Min value is 0 when there is no value under
the zero, otherwise it's the lowest data.

### Line Color

You can set gradient color for lines:

<img src="https://github.com/ehsannarmani/ComposeCharts/blob/master/assets/charts/line7.gif?raw=true" width="300">

```kotlin linenums="1" hl_lines="7"
LineChart(
    data = remember {
        listOf(
            Line(
                label = "Linux",
                values = listOf(71.0, 0.0, 100.0, 50.0, 50.0),
                color = Brush.radialGradient(...)
            ),
        )
    },
)
```

### Line Count

You can add how many lines you want:

<img src="https://github.com/ehsannarmani/ComposeCharts/blob/master/assets/charts/line1.gif?raw=true" width="300">

```kotlin linenums="1" hl_lines="4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21"
LineChart(
    data = remember {
        listOf(
            Line(
                label = "Windows",
                values = listOf(...),
                color = Color.Green,
                curvedEdges = true
            ),
            Line(
                label = "Linux",
                values = listOf(...),
                color = Color.Orange,
                curvedEdges = false
            ),
            Line(
                label = "Linux",
                values = listOf(...),
                color = Color.Blue,
                curvedEdges = true
            ),
        )
    },
    ...
)
```

### Dots & Line Curving

You can show dots and disable line edge curving:

<img src="https://github.com/ehsannarmani/ComposeCharts/blob/master/assets/charts/line3.gif?raw=true" width="300">

```kotlin linenums="1" hl_lines="28"
LineChart(
    data = remember {
        listOf(
            Line(
                label = "Windows",
                values = listOf(...),
                color = Color.Orange,
                curvedEdges = true,
                dotProperties = DotProperties(
                    enabled = true,
                    color = SolidColor(Color.White),
                    strokeWidth = 4f,
                    radius = 7f,
                    strokeColor = SolidColor(Color.Orange),
                )
            ),
            Line(
                label = "Linux",
                values = listOf(...),
                color = Color.Cyan,
                curvedEdges = false,
                dotProperties = DotProperties(
                    ...
                )
            ),
        )
    },
    curvedEdges = false
)
```

### Stroke Style

You can make chart line dashed:

<img src="https://github.com/ehsannarmani/ComposeCharts/blob/master/assets/charts/line6.gif?raw=true" width="300">

```kotlin linenums="1" hl_lines="9"
LineChart(
    data = remember {
        listOf(
            Line(
                label = "Windows",
                values = listOf(...),
                drawStyle = DrawStyle.Stroke(
                    width = 3.dp,
                    strokeStyle = StrokeStyle.Dashed(intervals = floatArrayOf(10f, 10f), phase = 15f)
                )
                ...
            ),
            Line(
                label = "Linux",
                values = listOf(...),
            ),
        )
    },
)
```

### Fill Color

You can make chart fill color:

<img src="https://github.com/ehsannarmani/ComposeCharts/blob/master/assets/charts/line9.gif?raw=true" width="300">

```kotlin linenums="1" hl_lines="7"
LineChart(
    data = listOf(
         Line(
            label = "Windows",
            values = listOf(...),
            color = Color.Orange,
            drawStyle = DrawStyle.Fill,
            ...
         ),
    ),
)
```


