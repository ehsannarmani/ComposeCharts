# Pie Chart

<img src="https://github.com/ehsannarmani/ComposeCharts/blob/master/assets/charts/pie1.gif?raw=true" width="300">

```kotlin linenums="1"
var data by remember {
    mutableStateOf(
        listOf(
            Pie(label = "Android", data = 20.0, color = Color.Red, selectedColor = Color.Green),
            Pie(label = "Windows", data = 45.0, color = Color.Cyan, selectedColor = Color.Blue),
            Pie(label = "Linux", data = 35.0, color = Color.Gray, selectedColor = Color.Yellow),
        )
    )
}
PieChart(
    modifier = Modifier.size(200.dp),
    data = data,
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

### Draw Style

You can change chart style to stroke:
<div>
   <img src="https://github.com/ehsannarmani/ComposeCharts/blob/master/assets/charts/pie2.gif?raw=true" width="300">
   <img src="https://github.com/ehsannarmani/ComposeCharts/blob/master/assets/charts/pie3.gif?raw=true" width="300">
</div>

```kotlin linenums="1" hl_lines="3 4 5"
PieChart(
    ...,
spaceDegree = 7f,
selectedPaddingDegree = 4f,
style = Pie.Style.Stroke(width = 100f)
)
```