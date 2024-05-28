<h1>Compose Charts</h1>

![banner](https://github.com/ehsannarmani/ComposeCharts/blob/master/assets/banner.png?raw=true)
![mockup](https://github.com/ehsannarmani/ComposeCharts/blob/master/assets/mokup.png?raw=true)

## Table of Contents

- [Gradle Setup](#gradle-setup)
- [All Charts](#all-charts)
- [Examples](#examples)
   - [Pie Chart](#pie-chart)
   - [Row Chart](#row-chart)
   - [Column Chart](#column-chart)
   - [Line Chart](#line-chart)
- [Animation Mode](#animation-mode)
- [Chart Properties](#chart-properties)
   - [Bars](#bars-barproperties)
   - [Dots](#dots-dotproperties)
   - [Indicators](#indicators-indicatorproperties)
   - [Grid Lines](#grid-lines-gridproperties)
   - [Axis](#axis-axisproperties)
   - [Dividers](#dividers-dividerproperties)
   - [Lines](#line-lineproperties)
   - [Labels](#labels-labelproperties)
   - [Label Helpers](#label-helpers-labelhelperproperties)
   - [Popups](#popups-popupproperties)

### Gradle Setup 
[![](https://jitpack.io/v/ehsannarmani/ComposeCharts.svg)](https://jitpack.io/#ehsannarmani/ComposeCharts)
```gradle
repositories {
    maven("https://jitpack.io")
}

dependencies {
    implementation ("com.github.ehsannarmani:ComposeCharts:latest_version")
}
```
### All Charts:
![mockup](https://github.com/ehsannarmani/ComposeCharts/blob/master/assets/full_chart.png?raw=true)

## Examples:

### Pie Chart:
<img src="https://github.com/ehsannarmani/ComposeCharts/blob/master/assets/charts/pie1.gif?raw=true" width="300">

```kotlin
PieChart(
   modifier = Modifier.size(200.dp),
   data = listOf(
      Pie(label = "Android",data = 20.0, color = Color.Red, selectedColor = Color.Green),
      Pie(label = "Windows",data = 45.0, color = Color.Cyan, selectedColor = Color.Blue),
      Pie(label = "Linux",data = 35.0, color = Color.Gray, selectedColor = Color.Yellow),
   ),
   onPieClick = {
       println("${it.label} Clicked")
       val pieIndex = data.indexOf(it)
       data = data.mapIndexed { mapIndex, pie -> pie.copy(selected = pieIndex == mapIndex) }
   },
   selectedScale = 1.2f,
   scaleAnimEnterSpec = spring<Float>(
       dampingRatio = Spring.DampingRatioMediumBouncy,
       stiffness = Spring.StiffnessLow
   ),
   colorAnimEnterSpec = tween(300),
   colorAnimExitSpec = tween(300),
   scaleAnimExitSpec = tween(300),
   spaceDegreeAnimExitSpec = tween(300),
   style = Pie.Style.Fill
)
```

> [!NOTE]
> You can change chart style to stroke:

<div>
   <img src="https://github.com/ehsannarmani/ComposeCharts/blob/master/assets/charts/pie2.gif?raw=true" width="300">
   <img src="https://github.com/ehsannarmani/ComposeCharts/blob/master/assets/charts/pie3.gif?raw=true" width="300">
</div>
<br/>

```kotlin
PieChart(
   ...,
   spaceDegree = 7f,
   selectedPaddingDegree = 4f,
   style = Pie.Style.Stroke(width = 100f)
)
```

<hr/>

### Column Chart:
<div>
   <img src="https://github.com/ehsannarmani/ComposeCharts/blob/master/assets/charts/column1.gif?raw=true" width="300">
   <img src="https://github.com/ehsannarmani/ComposeCharts/blob/master/assets/charts/column2.gif?raw=true" width="300">
</div>
<br/>

```kotlin
ColumnChart(
    modifier= Modifier.fillMaxSize().padding(horizontal = 22.dp),
    data = listOf(
        Bars(
            label = "Jan",
            values = listOf(
               Bars.Data(label = "Linux", value = 50.0, color = Brush.verticalGradient(...)),
               Bars.Data(label = "Windows", value = 70.0, color = SolidColor(Color.Red)),
            )
        ),
        Bars(
            label = "Feb",
            values = listOf(
               Bars.Data(label = "Linux", value = 80.0, color = Brush.verticalGradient(...)),
               Bars.Data(label = "Windows", value = 60.0, color = SolidColor(Color.Red)),
            )
        ),
        ...
    ),
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

> [!NOTE]
> You can set how many data you want for every bar:

<img src="https://github.com/ehsannarmani/ComposeCharts/blob/master/assets/charts/column3.gif?raw=true" width="300">

```kotlin
ColumnChart(
    data = listOf(
        Bars(
            label = "1", values = listOf(
               Bars.Data(value = 10.0, color = Color.Blue)
            )
        ),
        Bars(
            label = "2", values = listOf(
               Bars.Data(value = 20.0, color = Color.Blue)
            )
        ),
        ...
    ),
    barProperties = BarProperties(
        spacing = 1.dp,
        strokeWidth = 10.dp,
    ),
    ...
)
```

> [!TIP]
> You can set negative values for this chart and define max value and min value:

<img src="https://github.com/ehsannarmani/ComposeCharts/blob/master/assets/charts/column_negative.gif?raw=true" width="250">

#### Example:

```kotlin
ColumnChart(
    data = listOf(
        Bars(
            label = "1", values = listOf(
               Bars.Data(value = -40.0, color = Color.Blue)
            )
        ),
        Bars(
            label = "2", values = listOf(
               Bars.Data(value = 50.0, color = Color.Blue)
            )
        ),
        ...
    ),
    maxValue = 75.0,
    minValue = -75.0
    ...
)
```

> [!NOTE]
> By default, max value is the highest value of given data, min value is 0 when there is no value under the zero in given data, otherwise if there is value under zero, min value will be (-maxValue)


<hr/>

### Row Chart:
<div>
   <img src="https://github.com/ehsannarmani/ComposeCharts/blob/master/assets/charts/row1.gif?raw=true" width="250">
   <img src="https://github.com/ehsannarmani/ComposeCharts/blob/master/assets/charts/row2.gif?raw=true" width="250">
   <img src="https://github.com/ehsannarmani/ComposeCharts/blob/master/assets/charts/row3.gif?raw=true" width="250">
</div>
<br>


```kotlin
RowChart(
    modifier= Modifier.fillMaxSize().padding(horizontal = 22.dp),
    data = listOf(
        Bars(
            label = "Jan",
            values = listOf(
               Bars.Data(label = "Linux", value = 50.0, color = Brush.verticalGradient(...)),
               Bars.Data(label = "Windows", value = 70.0, color = SolidColor(Color.Red)),
            )
        ),
        Bars(
            label = "Feb",
            values = listOf(
               Bars.Data(label = "Linux", value = 80.0, color = Brush.verticalGradient(...)),
               Bars.Data(label = "Windows", value = 60.0, color = SolidColor(Color.Red)),
            )
        ),
        ...
    ),
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

> [!TIP]
> You can set negative values for this chart and define max value and min value:

<img src="https://github.com/ehsannarmani/ComposeCharts/blob/master/assets/charts/row_negative.gif?raw=true" width="250">

#### Example:

```kotlin
RowChart(
    data = listOf(
        Bars(
            label = "1", values = listOf(
               Bars.Data(value = -40.0, color = Color.Blue)
            )
        ),
        Bars(
            label = "2", values = listOf(
               Bars.Data(value = 50.0, color = Color.Blue)
            )
        ),
        ...
    ),
    maxValue = 75.0,
    minValue = -75.0
    ...
)
```

> [!NOTE]
> By default, max value is the highest value of given data, min value is 0 when there is no value under the zero in given data, otherwise if there is value under zero, min value will be (-maxValue)


<hr/>

### Line Chart:
<div>
   <img src="https://github.com/ehsannarmani/ComposeCharts/blob/master/assets/charts/line2.gif?raw=true" width="250">
   <img src="https://github.com/ehsannarmani/ComposeCharts/blob/master/assets/charts/line8.gif?raw=true" width="250">
</div>
<br>

```kotlin
LineChart(
    modifier = Modifier.fillMaxSize().padding(horizontal = 22.dp),
    data = listOf(
         Line(
            label = "Windows",
            values = listOf(28.0,41.0,5.0,10.0,35.0),
            color = SolidColor(Color(0xFF23af92)),
            firstGradientFillColor = Color(0xFF2BC0A1).copy(alpha = .5f),
            secondGradientFillColor = Color.Transparent,
            strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
            gradientAnimationDelay = 1000,
            drawStyle = DrawStyle.Stroke(width = 2.dp),
         )
    ),
    animationMode = AnimationMode.Together(delayBuilder = {
        it * 500L
    }),
)
```
<hr/>

> [!NOTE]
> You can set min & max value for all charts and show zero line:

<img src="https://github.com/ehsannarmani/ComposeCharts/blob/master/assets/charts/zero_line.gif?raw=true" width="250">

#### Example:

```kotlin
LineChart(
    data = listOf(
         Line(
            label = "Temperature",
            values = listOf(28.0,41.0,-5.0,10.0,35.0),
            color = Brush.radialGradient(...),
            ...
         )
    ),
    ...,
    zeroLineProperties = LineProperties(
        enabled = true,
        color = SolidColor(Color.Red),
    ),
    minValue = -20.0,
    maxValue = 100.0
)
```
> Max value by default is highest value of chart data and Min value is 0 when there is no value under the zero, otherwise it's the lowest data.

<hr/>

> [!NOTE]
> You can set gradient color for lines:

<img src="https://github.com/ehsannarmani/ComposeCharts/blob/master/assets/charts/line7.gif?raw=true" width="250">

```kotlin
LineChart(
    data = listOf(
         Line(
            label = "Linux",
            values = listOf(28.0,41.0,5.0,10.0,35.0),
            color = Brush.radialGradient(...),
            ...
         )
    ),
    ...
)
```
<hr/>

> [!NOTE]
> You can add how many lines you want:

<img src="https://github.com/ehsannarmani/ComposeCharts/blob/master/assets/charts/line1.gif?raw=true" width="250">

```kotlin
LineChart(
    data = listOf(
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
    ),
    ...
)
```

> [!NOTE]
> You can show dots and disable line edge curving:

<img src="https://github.com/ehsannarmani/ComposeCharts/blob/master/assets/charts/line3.gif?raw=true" width="250">

```kotlin
LineChart(
    data = listOf(
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
    ),
    curvedEdges = false
)
```
<hr/>

> [!NOTE]
> You can make chart line dashed:

<img src="https://github.com/ehsannarmani/ComposeCharts/blob/master/assets/charts/line6.gif?raw=true" width="250">

```kotlin
LineChart(
    data = listOf(
         Line(
            label = "Windows",
            values = listOf(...),
            drawStyle = DrawStyle.Stroke(
                width = 3.dp,
                strokeStyle = StrokeStyle.Dashed(intervals = floatArrayOf(10f,10f), phase = 15f)
            )
            ...
         ),
         Line(
            label = "Linux",
            values = listOf(...),
            ...
         ),
    ),
)
```
<hr/>

> [!NOTE]
> You can make chart fill color:

<img src="https://github.com/ehsannarmani/ComposeCharts/blob/master/assets/charts/line9.gif?raw=true" width="250">

```kotlin
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
<hr/>

## Animation Mode:
#### In Row/Column/Line charts you can set running animations at the same time types:
| Mode              | Description |
|-------------------|--------------|
| `OneByOne`        | Animations will run one by one, for example in line charts, lines will be drawn after previous line animation finished.      |
| `Together`        | By default, animations will run async, but you can set delay for next animations    | 

#### Example:
```kotlin
LineChart(
   ...,
   animationMode = AnimationMode.OneByOne
)

LineChart(
   ...,
   animationMode = AnimationMode.Together(delayBuilder = { index-> index*200 })
)
```
> [!NOTE]
> In the last example, every animation will be start 200ms after previous animations start.

<hr/>

## Chart Properties:

### Bars: `BarProperties`
> Usage: In Column/Row Charts you can set bar properties with this property

| Property       | Type             | Default               | Description         |
|----------------|------------------|-----------------------|---------------------|
| `thickness`    | Dp               | `20`                    | specifies bar width 
| `spacing`      | Dp               | `4`                     | specifies space between data bars when you have more than one bar in a data 
| `cornerRadius` | Bars.Data.Radius | `Bars.Data.Radius.None` | specifies space between data bars when you have more than one bar in a data 
| `style`        | DrawStyle        | `DrawStyle.Fill`        | specifies bar style 

#### Example:
```kotlin
val barProperties = BarProperties(
   thickness = 15.dp,
   spacing = 4.dp,
   cornerRadius = Bars.Data.Radius.Circular(6.dp),
   style = DrawStyle.Fill
)
```
<hr/>

### Dots: `DotProperties`
> Usage: In Line Charts you can set data dot shape properties with this property

| Property           | Type                 | Default                      | Description         |
|--------------------|----------------------|------------------------------|---------------------|
| `enabled`          | Boolean              | `false`                        | specifies dots visibility 
| `radius`           | Float                | `10f`                          | specifies dot size 
| `color`            | Brush                |` SolidColor(Color.Unspecified) `| specifies dot color 
| `strokeWidth`      | Float                | `3f`                           | specifies dot stroke width
| `strokeColor`      | Brush                |` SolidColor(Color.Unspecified) `| specifies dot stroke color
| `strokeStyle`      | StrokeStyle          | `StrokeStyle.Normal`           | specifies dot stroke style
| `animationEnabled` | Boolean              | `true`                         | set `false` if you want to show dots without delay and animation
| `animationSpec`    | AnimationSpec<Float> |`tween(300)`| specifies dots visibility animation spec

#### Example:
```kotlin
val dotProperties = DotProperties(
   enabled = true,
   radius = 10f,
   color = SolidColor(Color.Red),
   strokeWidth = 3f,
   strokeColor = Color.White,
   strokeStyle = StrokeStyle.Normal,
   animationEnabled = true,
   animationSpec = tween(500)
)
```
<hr/>

### Indicators: `IndicatorProperties`
> Usage: In every chart you can set properties of counters next to the chart

| Property         | Type               | Default                | Description         |
|------------------|--------------------|------------------------|---------------------|
| `enabled`        | Boolean            | `true`                 | specifies indicator visiblity 
| `textStyle`      | TextStyle          | `TextStyle.Default`    | specifies counter style 
| `count`          | Int                | `4`                    | specifies counters count 
| `contentBuilder` | (Double) -> String | `{ "%.2f".format(it) }`| specifies counter content creation template 

#### Example:
```kotlin
val indicatorProperties = IndicatorProperties(
   enabled = true,
   textStyle = MaterialTheme.typography.labelSmall,
   count = 5,
   contentBuilder = { indicator->
        "%.2f".format(indicator)+" Million"
   }
)
```

<hr/>

### Grid Lines: `GridProperties`
> Usage: In every chart you can set properties of grid lines

| Property          | Type           | Default              | Description         |
|-------------------|----------------|----------------------|---------------------|
| `enabled`         | Boolean        | `true`               | specifies grid lines visibility 
| `xAxisProperties` | AxisProperties | `AxisProperties(..)` | specifies grid horizontal lines properties 
| `yAxisProperties` | AxisProperties | `AxisProperties(..)` | specifies grid vertical lines properties 

#### Example:
```kotlin
val gridProperties = GridProperties(
   enabled = true,
   xAxisProperties = AxisProperties(
      ...
   ),
   yAxisProperties = AxisProperties(
      ...
   )
)
```
<hr/>

### Axis: `AxisProperties`

| Property    | Type        | Default              | Description         |
|-------------|-------------|----------------------|---------------------|
| `enabled`   | Boolean     | `true`               | specifies axis line visibility 
| `style`     | StrokeStyle | `StrokeStyle.Normal` | specifies axis line style 
| `color`     | Color       | `Color.Gray`         | specifies axis line color 
| `thickness` | Dp          | `(0.5).dp`           | specifies axis line stroke width
| `lineCount` | Int         | `5`                  | specifies count of axis lines

#### Example:
```kotlin
val axisProperties = AxisProperties(
   enabled = true,
   style = StrokeStyle.Dashed(intervals = floatArrayOf(10f,10f)),
   color = Color.Gray,
   thickness = (.5).dp,
   lineCount = 5
)
```
<hr/>

### Dividers: `DividerProperties`
> Usage: In every chart you can set properties of dividers between labels and chart, indicators and chart

| Property          | Type            | Default              | Description         |
|-------------------|-----------------|----------------------|---------------------|
| `enabled`         | Boolean         | `true`               | specifies dividers visibility 
| `xAxisProperties` | LineProperties  | `LineProperties(..)` | specifies horizontal divider properties 
| `yAxisProperties` | LineProperties  | `LineProperties(..)` | specifies vertical divider properties 

#### Example:
```kotlin
val dividerProperties = DividerProperties(
   enabled = true,
   xAxisProperties = LineProperties(
      ...
   ),
   yAxisProperties = LineProperties(
      ...
   )
)
```
### Line: `LineProperties`

| Property    | Type        | Default              | Description         |
|-------------|-------------|----------------------|---------------------|
| `enabled`   | Boolean     | `true`               | specifies axis line visibility 
| `style`     | StrokeStyle | `StrokeStyle.Normal` | specifies axis line style 
| `color`     | Color       | `Color.Gray`         | specifies axis line color 
| `thickness` | Dp          | `(0.5).dp`           | specifies axis line stroke width

#### Example:
```kotlin
val lineProperties = LineProperties(
   enabled = true,
   style = StrokeStyle.Dashed(intervals = floatArrayOf(10f,10f)),
   color = Color.Gray,
   thickness = (.5).dp,
)
```
<hr/>

### Labels: `LabelProperties`
> Usage: In every chart you can set properties of the labels (Apr, Jan, ...)

| Property          | Type         | Default             | Description                 |
|-------------------|--------------|---------------------|-----------------------------|
| `enabled`         | Boolean      | `true`              | specifies labels visibility 
| `textStyle`       | TextStyle    | `TextStyle.Default` | specifies label textStyle   
| `verticalPadding` | Dp           | `12.dp`             | specifies vertical padding of labels area  
| `labels`          | List<String> | `emptyList()`       | In line charts specifies chart labels  

#### Example:
```kotlin
val labelProperties = LabelProperties(
   enabled = true,
   textStyle = MaterialTheme.typography.labelSmall,
   verticalPadding = 16.dp,
   labels = listOf("Apr","Mar",...)
)
```

<hr/>

### Label Helpers: `LabelHelperProperties`
> Usage: In every chart you can set properties of the labels helper which positioned in top of chart

| Property          | Type         | Default             | Description                 |
|-------------------|--------------|---------------------|-----------------------------|
| `enabled`         | Boolean      | `true`              | specifies label helpers visibility 
| `textStyle`       | TextStyle    | `TextStyle.Default` | specifies label helper textStyle

#### Example:
```kotlin
val labelHelperProperties = LabelHelperProperties(
   enabled = true,
   textStyle = MaterialTheme.typography.labelMedium
)
```

<hr/>

### Popups: `PopupProperties`
> Usage: In every chart you can set properties of popup which shown when user click or drag on chart

| Property                   | Type                 | Default                 | Description                 |
|----------------------------|----------------------|-------------------------|-----------------------------|
| `enabled`                  | Boolean              | `true`                  | specifies popup visibility 
| `animationSpec`            | AnimationSpec<Float> | `tween(400)`            | specifies popup visibility animation spec   
| `duration`                 | Long                 | `1500`                  | in column/row charts, specifies how long the popup will be visible  
| `textStyle`                | TextStyle            | `TextStyle.Default`     | specifies popup text style  
| `containerColor`           | Color                | `Color(0xff313131)`     | specifies popup background color  
| `cornerRadius`             | Dp                   | `6.dp`                  | specifies popup corner radius  
| `contentHorizontalPadding` | Dp                   | `4.dp`                  | specifies popup horizontal padding  
| `contentVerticalPadding`   | Dp                   | `2.dp`                  | specifies popup vertical padding  
| `contentBuilder`           | (Double)->String     | `{ "%.2f".format(it) }` | specifies popup content creation template

#### Example:
```kotlin
val popupProperties = PopupProperties(
   enabled = true,
   animationSpec = tween(300),
   duration = 2000L,
   textStyle = MaterialTheme.typography.labelSmall,
   containerColor = Color.White,
   cornerRadius = 8.dp,
   contentHorizontalPadding = 4.dp,
   contentVerticalPadding = 2.dp,
   contentBuilder = { value->
      "%.1f".format(value)+"Million"
   }
)
```

<hr/>

## Todos:
1. [ ] Add Candle Stick Chart
2. [ ] Add Circle Progress Chart
3. [ ] MultiPlatform Support 







