# Label Properties

!!! info "Usage"
In every chart you can set properties of the labels (Apr, Jan, ...)

| Property          | Type                     | Default                      | Description                                  |
|-------------------|--------------------------|------------------------------|----------------------------------------------|
| `enabled`         | Boolean                  | `true`                       | specifies labels visibility                  |
| `textStyle`       | TextStyle                | `TextStyle.Default`          | specifies label textStyle                    |
| `verticalPadding` | Dp                       | `12.dp`                      | specifies vertical padding of labels area    |
| `labels`          | List<String>             | `emptyList()`                | overrides the chart labels                   |
| `builder`         | Composable Function      | `null`                       | build the every label component              |
| `rotation`        | LabelProperties.Rotation | `LabelProperties.Rotation()` | manage the labels rotation on size conflicts |

!!! Example
    ```kotlin linenums="1"
    val labelProperties = LabelProperties(
        enabled = true,
        textStyle = MaterialTheme.typography.labelSmall,
        verticalPadding = 16.dp,
        labels = listOf("Apr","Mar",...),
        builder = {modifier,label,shouldRotate,index->
            Text(modifier=modifier,text=label)
        }
    )
    ```

### Label Rotation

| Property  | Type          | Default            | Description                         |
|-----------|---------------|--------------------|-------------------------------------|
| `mode`    | Rotation.Mode | `Mode.IfNecessary` | specifies rotation mode             |
| `degree`  | Float         | `-45f`             | specifies rotation degree           |
| `padding` | Dp?           | `null`             | specifies padding of rotated labels |

<img src="https://github.com/ehsannarmani/ComposeCharts/blob/master/assets/rotation_degree_sample.png?raw=true" width="300">

!!! Example
    ```kotlin linenums="1"
    val labelProperties = LabelProperties(
        enabled = true,
        textStyle = MaterialTheme.typography.labelSmall,
        verticalPadding = 16.dp,
        rotation = Rotation(
            mode = LabelProperties.Rotation.Mode.Force,
            degree = -45f
        )
    )
    ```


