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

### Start Degree
You can set specific start degree for starting slices
```kotlin linenums="1" hl_lines="3"
    PieChart(
        ...,
        startDegree = -90f
    )
```

### Labeling

Display labels directly on pie slices using the **OnPie** labeling mode.

<div>
   <img src="https://github.com/ehsannarmani/ComposeCharts/blob/master/assets/charts/pie_chart_label2.gif?raw=true" width="300">
</div>

```kotlin linenums="1" hl_lines="3"
    PieChart(
        ...,
        labelMode = LabelMode.OnPie()
    )
```

When `LabelMode.OnPie()` is enabled, two different labels can be displayed:

* **Outer Label** – Displays the data label outside the slice and connects it with a guide line.
* **Inner Label** – Displays additional information inside the slice. By default, this is the slice percentage.

---

### Configuration

`LabelMode.OnPie()` provides several customization options:

| Parameter           | Description                                                                                                                                     | Default                             |
| ------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------- | ----------------------------------- |
| `outerLineSize`     | Length of the guide line drawn outside each slice. The first value controls the radial segment, and the second controls the horizontal segment. | `12.dp to 16.dp`                    |
| `outerLineStyle`    | Controls the appearance of the guide line (width, stroke style, etc.).                                                                          | `Stroke(width = 1.5.dp)`            |
| `innerLabelStyle`   | Text style used for the inner label.                                                                                                            | White, **12.sp**, Bold              |
| `show`              | Determines whether labels should be shown for a specific slice.                                                                                 | `{ true }`                          |
| `innerLabelContent` | Controls the content displayed inside each slice.                                                                                               | `PieInnerLabelContent.Percentage()` |
| `animationSpec`     | Animation used when labels appear.                                                                                                              | `tween(300, delayMillis = 100)`     |

---

### Showing Labels Conditionally

The `show` callback allows you to display labels only for specific slices.

For example, show labels only for selected slices:
<div>
   <img src="https://github.com/ehsannarmani/ComposeCharts/blob/master/assets/charts/pie_chart_label.gif?raw=true" width="300">
</div>
```kotlin linenums="1" hl_lines="3 4 5 6 7"
    PieChart(
        ...,
        labelMode = LabelMode.OnPie(
            show = { pie ->
                pie.id == selectedPie?.id
            }
        )
    )
```

This is useful for hiding labels on very small slices and keeping the chart readable.

---

### Customizing the Inner Label

By default, the inner label displays the percentage of each slice.

You can replace it with any supported `PieInnerLabelContent` implementation:

```kotlin linenums="1" hl_lines="3 4 5 6 7"
    PieChart(
        ...,
        labelMode = LabelMode.OnPie(
            innerLabelContent = PieInnerLabelContent.Custom { pie->
                "Hello ${pie.id}"
            }
        )
    )
```
```kotlin linenums="1" hl_lines="3 4 5 6 7"
    PieChart(
        ...,
        labelMode = LabelMode.OnPie(
            innerLabelContent = PieInnerLabelContent.Percentage { percentage->
                "${percentage} of total"
            }
        )
    )
```

!!! note
    `PieInnerLabelContent` is responsible only for the content displayed **inside** the slice. The outer label always displays the slice label.

---

### Styling

Customize both the guide line and the text appearance:

```kotlin linenums="1" hl_lines="3 4 5 6 7 8 9 10 11 12 13"
    PieChart(
        ...,
        labelMode = LabelMode.OnPie(
            outerLineSize = 18.dp to 24.dp,
            outerLineStyle = DrawStyle.Stroke(
                width = 2.dp
            ),
            innerLabelStyle = TextStyle(
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        )
    )
```

---

### Animating Labels

Labels are animated independently from the pie chart.

```kotlin linenums="1" hl_lines="3 4 5 6 7 8"
    PieChart(
        ...,
        labelMode = LabelMode.OnPie(
            animationSpec = tween(
                durationMillis = 600,
                delayMillis = 200
            )
        )
    )
```

Set `animationSpec` to any Compose `AnimationSpec<Float>` implementation to achieve the desired animation behavior.

!!! info
    Label animations start after the corresponding slice animation reaches its visible state, resulting in a smoother appearance.


