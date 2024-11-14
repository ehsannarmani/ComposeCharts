# Animation Modes

In Row/Column/Line charts you can set running animations at the same time types:

| Mode       | Description                                                                                                             |
|------------|-------------------------------------------------------------------------------------------------------------------------|
| `OneByOne` | Animations will run one by one, for example in line charts, lines will be drawn after previous line animation finished. |
| `Together` | By default, animations will run async, but you can set delay for next animations                                        | 

!!! Example
```kotlin linenums="1" hl_lines="3 8"
LineChart(
...,
animationMode = AnimationMode.OneByOne
)

    LineChart(
       ...,
       animationMode = AnimationMode.Together(delayBuilder = { index-> index*200 })
    )
    ```

In the second example, every animation will be start 200ms after previous animations start.