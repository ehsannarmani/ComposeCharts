<h1>Compose Charts</h1>
<hr/>

![banner](https://github.com/ehsannarmani/ComposeCharts/blob/master/assets/banner.png?raw=true)
![mockup](https://github.com/ehsannarmani/ComposeCharts/blob/master/assets/mokup.png?raw=true)

## Table of Contents

1. [Quick Start](#quick-start)
1. [All Charts](#documentation)
1. [Examples](#examples)
   1. Pie Chart
   2. Line Chart
   3. Row Chart
   4. Column Chart

### Gradle Setup
```gradle
repositories {
    maven("https://jitpack.io")
}

dependencies {
    implementation ("com.github.ehsannarmani:ComposeCharts:0.0.1")
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
   spaceDegreeAnimEnterSpec = floatSpec,
   colorAnimEnterSpec = tween(300),
   scaleAnimEnterSpec = floatSpec,
   colorAnimExitSpec = tween(300),
   scaleAnimExitSpec = tween(300),
   spaceDegreeAnimExitSpec = tween(300),
   selectedPaddingDegree = 0f,
   style = Pie.Style.Fill
)
```

> [!NOTE]
> You can change chart style to stroke:

<img src="https://github.com/ehsannarmani/ComposeCharts/blob/master/assets/charts/pie2.gif?raw=true" width="300">
<img src="https://github.com/ehsannarmani/ComposeCharts/blob/master/assets/charts/pie3.gif?raw=true" width="300">

```kotlin
PieChart(
   ...
   spaceDegree = 7f,
   selectedPaddingDegree = 4f,
   style = Pie.Style.Stroke(width = 100f)
)
```


