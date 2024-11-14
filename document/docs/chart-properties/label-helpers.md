# LabelHelper Properties

!!! info "Usage"
    In every chart you can set properties of the labels helper which positioned in top of chart

| Property    | Type      | Default             | Description                        |
|-------------|-----------|---------------------|------------------------------------|
| `enabled`   | Boolean   | `true`              | specifies label helpers visibility |
| `textStyle` | TextStyle | `TextStyle.Default` | specifies label helper textStyle   |

!!! Example
    ```kotlin linenums="1"
    val labelHelperProperties = LabelHelperProperties(
        enabled = true,
        textStyle = MaterialTheme.typography.labelMedium
    )
    ```
