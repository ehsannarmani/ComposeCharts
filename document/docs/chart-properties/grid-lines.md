# Grid Properties

!!! info "Usage"
In every chart you can set properties of grid lines

| Property          | Type           | Default              | Description                                |
|-------------------|----------------|----------------------|--------------------------------------------|
| `enabled`         | Boolean        | `true`               | specifies grid lines visibility            |
| `xAxisProperties` | AxisProperties | `AxisProperties(..)` | specifies grid horizontal lines properties |
| `yAxisProperties` | AxisProperties | `AxisProperties(..)` | specifies grid vertical lines properties   |

!!! Example
```kotlin linenums="1"
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