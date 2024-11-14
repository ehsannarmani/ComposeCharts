# Label Properties

!!! info "Usage"
    In every chart you can set properties of the labels (Apr, Jan, ...)

| Property                       | Type         | Default             | Description                                                                                                                                          |
|--------------------------------|--------------|---------------------|------------------------------------------------------------------------------------------------------------------------------------------------------|
| `enabled`                      | Boolean      | `true`              | specifies labels visibility                                                                                                                          |
| `textStyle`                    | TextStyle    | `TextStyle.Default` | specifies label textStyle                                                                                                                            |
| `verticalPadding`              | Dp           | `12.dp`             | specifies vertical padding of labels area                                                                                                            |
| `labels`                       | List<String> | `emptyList()`       | In line charts, specifies chart labels                                                                                                               |
| `rotationDegreeOnSizeConflict` | Float        | `-45`               | rotation degree of label on size confilict with other labels (See)                                                                                   |
| `forceRotation`                | Boolean      | `false`             | specifies force rotation for labels (in false mode, labels rotate only when there is a label whose width is more than 50% different from the others) |

### Label Rotation

<img src="https://github.com/ehsannarmani/ComposeCharts/blob/master/assets/rotation_degree_sample.png?raw=true" width="300">

!!! Example
    ```kotlin linenums="1"
    val labelProperties = LabelProperties(
        enabled = true,
        textStyle = MaterialTheme.typography.labelSmall,
        verticalPadding = 16.dp,
        labels = listOf("Apr","Mar",...)
    )
    ```
