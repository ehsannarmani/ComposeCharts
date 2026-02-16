# Divider Properties

!!! info "Usage"
    In every chart you can set properties of dividers between labels and chart, indicators and chart

| Property          | Type           | Default              | Description                             |
|-------------------|----------------|----------------------|-----------------------------------------|
| `enabled`         | Boolean        | `true`               | specifies dividers visibility           
| `xAxisProperties` | LineProperties | `LineProperties(..)` | specifies horizontal divider properties 
| `yAxisProperties` | LineProperties | `LineProperties(..)` | specifies vertical divider properties   

!!! Example
    ```kotlin linenums="1"
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