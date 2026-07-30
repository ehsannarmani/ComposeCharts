package ir.ehsannarmani.compose_charts

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import ir.ehsannarmani.compose_charts.components.RCChartLabelHelper
import ir.ehsannarmani.compose_charts.extensions.addRoundRect
import ir.ehsannarmani.compose_charts.extensions.drawGridLines
import ir.ehsannarmani.compose_charts.extensions.spaceBetween
import ir.ehsannarmani.compose_charts.extensions.split
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.BarPopupData
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.DividerProperties
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.IndicatorPosition
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.PopupProperties
import ir.ehsannarmani.compose_charts.models.ScrollMode
import ir.ehsannarmani.compose_charts.models.SelectedBar
import ir.ehsannarmani.compose_charts.models.ZeroLineProperties
import ir.ehsannarmani.compose_charts.models.asRadiusPx
import ir.ehsannarmani.compose_charts.utils.HorizontalLabels
import ir.ehsannarmani.compose_charts.utils.ImplementRCAnimation
import ir.ehsannarmani.compose_charts.utils.calculateOffset
import ir.ehsannarmani.compose_charts.utils.checkRCMaxValue
import ir.ehsannarmani.compose_charts.utils.checkRCMinValue
import ir.ehsannarmani.compose_charts.utils.calculateAverageSpacingBetweenBars
import ir.ehsannarmani.compose_charts.utils.calculateEveryDataSize
import ir.ehsannarmani.compose_charts.utils.computeScrollConfig
import ir.ehsannarmani.compose_charts.utils.rememberComputedChartMaxValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun ColumnChart(
    modifier: Modifier = Modifier,
    data: List<Bars>,
    barProperties: BarProperties = BarProperties(),
    onBarClick: ((BarPopupData) -> Unit)? = null,
    onBarLongClick: ((BarPopupData) -> Unit)? = null,
    labelProperties: LabelProperties = LabelProperties(
        textStyle = TextStyle.Default,
        enabled = true
    ),
    indicatorProperties: HorizontalIndicatorProperties = HorizontalIndicatorProperties(
        textStyle = TextStyle.Default
    ),
    dividerProperties: DividerProperties = DividerProperties(),
    zeroLineProperties: ZeroLineProperties = ZeroLineProperties(),
    gridProperties: GridProperties = GridProperties(),
    labelHelperProperties: LabelHelperProperties = LabelHelperProperties(),
    animationMode: AnimationMode = AnimationMode.Together { it * 200L },
    animationSpec: AnimationSpec<Float> = tween(500),
    animationDelay: Long = 100,
    textMeasurer: TextMeasurer = rememberTextMeasurer(),
    popupProperties: PopupProperties = PopupProperties(
        textStyle = TextStyle.Default.copy(
            color = Color.White,
            fontSize = 12.sp
        )
    ),
    barAlphaDecreaseOnPopup: Float = .4f,
    maxValue: Double = data.maxOfOrNull { it.values.maxOfOrNull { it.value } ?: 0.0 } ?: 0.0,
    minValue: Double = if (data.any { it.values.any { it.value < 0 } }) -maxValue else 0.0,
    scrollMode: ScrollMode = ScrollMode.Auto(4.dp),
) {
    checkRCMinValue(minValue, data)
    checkRCMaxValue(maxValue, data)

    val density = LocalDensity.current

    val onBarClick by rememberUpdatedState(onBarClick)
    val onBarLongClick by rememberUpdatedState(onBarLongClick)

    val everyDataWidth = remember(data, barProperties, density) {
        calculateEveryDataSize(data, barProperties, density)
    }
    val averageSpacingBetweenBars = remember(data, barProperties, density) {
        calculateAverageSpacingBetweenBars(data, barProperties, density)
    }

    val barWithRect = remember {
        mutableStateListOf<BarPopupData>()
    }

    val selectedValue = remember {
        mutableStateOf<SelectedBar?>(null)
    }

    val popupAnimation = remember {
        Animatable(0f)
    }

    val zeroLineAnimation = remember(data) {
        Animatable(0f)
    }

    LaunchedEffect(data, zeroLineProperties.enabled) {
        if (zeroLineProperties.enabled) {
            zeroLineAnimation.snapTo(0f)
            zeroLineAnimation.animateTo(1f, animationSpec = zeroLineProperties.animationSpec)
        }
    }

    val computedMaxValue =
        rememberComputedChartMaxValue(minValue, maxValue, indicatorProperties.count)
    val indicators = remember(minValue, computedMaxValue) {
        indicatorProperties.indicators.ifEmpty {
            split(
                count = indicatorProperties.count,
                minValue = minValue,
                maxValue = computedMaxValue
            )
        }
    }
    val indicatorAreaWidth = remember {
        if (indicatorProperties.enabled) {
            indicators.maxOf { textMeasurer.measure(indicatorProperties.contentBuilder(it)).size.width } + (indicatorProperties.padding.value * density.density)
        } else {
            0f
        }
    }

    val xPadding = remember {
        if (indicatorProperties.enabled && indicatorProperties.position == IndicatorPosition.Horizontal.Start) {
            indicatorAreaWidth
        } else {
            0f
        }
    }

    val isScrollMode = scrollMode !is ScrollMode.Disabled
    val scrollState = rememberScrollState()
    val scrollEnabled = remember(scrollMode) { mutableStateOf(scrollMode is ScrollMode.Always) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(selectedValue.value) {
        if (selectedValue.value != null) {
            delay(popupProperties.duration)
            popupAnimation.animateTo(0f, animationSpec = popupProperties.animationSpec)
            selectedValue.value = null
        }
    }

    ImplementRCAnimation(
        data = data,
        animationMode = animationMode,
        spec = { it.animationSpec ?: animationSpec },
        delay = animationDelay,
        before = {
            barWithRect.clear()
        }
    )

    // Shared drawing logic — parameterised by xPadding and whether to draw indicators/grid/bars
    fun DrawScope.drawChartContent(
        barsAreaWidth: Float,
        drawXPadding: Float,
        shouldDrawIndicators: Boolean,
        shouldDrawGrid: Boolean = true,
        shouldDrawBars: Boolean = true
    ) {
        val zeroY = size.height - calculateOffset(
            maxValue = computedMaxValue,
            minValue = minValue,
            total = size.height,
            value = 0.0f
        ).toFloat()

        // Draw Y-axis indicators inside the Canvas (Disabled mode only)
        if (shouldDrawIndicators && indicatorProperties.enabled) {
            indicators.forEachIndexed { index, indicator ->
                val measureResult =
                    textMeasurer.measure(
                        indicatorProperties.contentBuilder(indicator),
                        style = indicatorProperties.textStyle
                    )
                val x = when (indicatorProperties.position) {
                    IndicatorPosition.Horizontal.Start -> 0f
                    IndicatorPosition.Horizontal.End ->
                        barsAreaWidth + indicatorProperties.padding.value * density.density
                }
                drawText(
                    textLayoutResult = measureResult,
                    topLeft = Offset(
                        x = x,
                        y = (size.height - measureResult.size.height).spaceBetween(
                            itemCount = indicators.count(),
                            index
                        )
                    )
                )
            }
        }

        if (shouldDrawGrid) {
            drawGridLines(
                xPadding = drawXPadding,
                size = size.copy(width = barsAreaWidth),
                dividersProperties = dividerProperties,
                indicatorPosition = indicatorProperties.position,
                xAxisProperties = gridProperties.xAxisProperties,
                yAxisProperties = gridProperties.yAxisProperties,
                gridEnabled = gridProperties.enabled
            )
        }

        val drawZeroLine = {
            drawLine(
                brush = zeroLineProperties.color,
                start = Offset(x = drawXPadding, y = zeroY),
                end = Offset(
                    x = drawXPadding + barsAreaWidth * zeroLineAnimation.value,
                    y = zeroY
                ),
                pathEffect = zeroLineProperties.style.pathEffect,
                strokeWidth = zeroLineProperties.thickness.toPx()
            )
        }
        if (shouldDrawGrid && zeroLineProperties.enabled && zeroLineProperties.zType == ZeroLineProperties.ZType.Under) {
            drawZeroLine()
        }

        if (shouldDrawBars) {
            data.forEachIndexed { dataIndex, columnChart ->
                columnChart.values.forEachIndexed { valueIndex, col ->
                    if (col.value != 0.0) {
                        val stroke =
                            (col.properties?.thickness ?: barProperties.thickness).toPx()
                        val spacing =
                            (col.properties?.spacing ?: barProperties.spacing).toPx()

                        val barHeight =
                            ((col.value * size.height) / (computedMaxValue - minValue)) * col.animator.value
                        val everyBarWidth = (stroke + spacing)

                        val barX =
                            (valueIndex * everyBarWidth) + (barsAreaWidth - everyDataWidth).spaceBetween(
                                itemCount = data.count(),
                                index = dataIndex
                            ) + drawXPadding + (averageSpacingBetweenBars / 2).toFloat()
                        val rect = Rect(
                            offset = Offset(
                                x = barX,
                                y = (zeroY - barHeight.toFloat().coerceAtLeast(0f))
                            ),
                            size = Size(
                                width = stroke,
                                height = barHeight.absoluteValue.toFloat()
                            ),
                        )
                        if (barWithRect.none { it.rect == rect }) {
                            barWithRect.add(BarPopupData(col, rect, dataIndex, valueIndex))
                        }
                        val path = Path()

                        var radius =
                            (col.properties?.cornerRadius ?: barProperties.cornerRadius)
                        if (col.value < 0) {
                            radius = radius.reverse()
                        }

                        path.addRoundRect(rect = rect, radius = radius.asRadiusPx(this))
                        val alpha = if (rect == selectedValue.value?.rect) {
                            1f - (barAlphaDecreaseOnPopup * popupAnimation.value)
                        } else {
                            1f
                        }
                        drawPath(
                            path = path,
                            brush = col.color,
                            alpha = alpha,
                            style = (col.properties?.style
                                ?: barProperties.style).getStyle(density.density)
                        )
                    }
                }
            }
            if (shouldDrawGrid && zeroLineProperties.enabled && zeroLineProperties.zType == ZeroLineProperties.ZType.Above) {
                drawZeroLine()
            }
            selectedValue.value?.let { selectedValue ->
                drawPopup(
                    selectedBar = selectedValue,
                    properties = popupProperties,
                    textMeasurer = textMeasurer,
                    progress = popupAnimation.value
                )
            }
        }
    }

    // Shared pointer-input modifier for drag / tap / long-press on bars
    val chartPointerModifier = Modifier
        .pointerInput(scrollEnabled.value, popupProperties.enabled) {
            if (scrollEnabled.value) {
                /** Manually dispatch drag delta to the ScrollState because Compose Desktop
                 * does not support touch-like drag scrolling by default.
                 * This allows users to pan the chart content by clicking and dragging with a mouse.
                 **/
                detectHorizontalDragGestures { change, dragAmount ->
                    change.consume()
                    scrollState.dispatchRawDelta(-dragAmount)
                }
            } else if (popupProperties.enabled) {
                detectHorizontalDragGestures { change, _ ->
                    barWithRect
                        .lastOrNull { popupData ->
                            change.position.x in popupData.rect.left..popupData.rect.right
                        }
                        ?.let { popupData ->
                            selectedValue.value = SelectedBar(
                                bar = popupData.bar,
                                rect = popupData.rect,
                                offset = Offset(
                                    popupData.rect.left,
                                    if (popupData.bar.value < 0) popupData.rect.bottom else popupData.rect.top
                                ),
                                dataIndex = popupData.dataIndex,
                                valueIndex = popupData.valueIndex
                            )
                            scope.launch {
                                if (popupAnimation.value != 1f && !popupAnimation.isRunning) {
                                    popupAnimation.animateTo(
                                        1f,
                                        animationSpec = popupProperties.animationSpec
                                    )
                                }
                            }
                        }
                }
            }
        }
        .pointerInput(Unit) {
            detectTapGestures(
                onTap = {
                    val position = Offset(it.x, it.y)
                    barWithRect
                        .lastOrNull { popupData ->
                            popupData.rect.contains(position)
                        }
                        ?.let { popupData ->
                            if (popupProperties.enabled) {
                                selectedValue.value = SelectedBar(
                                    bar = popupData.bar,
                                    rect = popupData.rect,
                                    offset = Offset(
                                        popupData.rect.left,
                                        if (popupData.bar.value < 0) popupData.rect.bottom else popupData.rect.top
                                    ),
                                    dataIndex = popupData.dataIndex,
                                    valueIndex = popupData.valueIndex
                                )
                                scope.launch {
                                    popupAnimation.snapTo(0f)
                                    popupAnimation.animateTo(
                                        1f,
                                        animationSpec = popupProperties.animationSpec
                                    )
                                }
                            }
                            onBarClick?.invoke(popupData)
                        }
                },
                onLongPress = {
                    val position = Offset(it.x, it.y)
                    barWithRect
                        .lastOrNull { popupData ->
                            popupData.rect.contains(position)
                        }
                        ?.let { popupData ->
                            onBarLongClick?.invoke(popupData)
                        }
                }
            )
        }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Column(modifier = modifier) {
            if (labelHelperProperties.enabled) {
                RCChartLabelHelper(
                    data = data,
                    properties = labelHelperProperties
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                val totalWidthPx = constraints.maxWidth.toFloat()
                val availableWidthPx = (totalWidthPx - indicatorAreaWidth).coerceAtLeast(0f)

                val scrollConfig = computeScrollConfig(
                    scrollMode = scrollMode,
                    availableSize = availableWidthPx,
                    everyDataSize = everyDataWidth,
                    dataCount = data.count(),
                    density = density
                )
                val isScrollable = scrollConfig.scrollEnabled
                scrollEnabled.value = isScrollable

                val barsAreaWidth =
                    if (isScrollable) scrollConfig.effectiveSize else availableWidthPx

                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                    ) {
                        if (isScrollMode) {
                            // ── Scroll layout: indicators in separate fixed Canvases ──

                            // Fixed Y-axis indicators (Start position)
                            if (indicatorProperties.enabled && indicatorProperties.position == IndicatorPosition.Horizontal.Start) {
                                Canvas(
                                    modifier = Modifier
                                        .width(with(density) { indicatorAreaWidth.toDp() })
                                        .fillMaxHeight()
                                ) {
                                    indicators.forEachIndexed { index, indicator ->
                                        val measureResult =
                                            textMeasurer.measure(
                                                indicatorProperties.contentBuilder(indicator),
                                                style = indicatorProperties.textStyle
                                            )
                                        drawText(
                                            textLayoutResult = measureResult,
                                            topLeft = Offset(
                                                x = 0f,
                                                y = (size.height - measureResult.size.height).spaceBetween(
                                                    itemCount = indicators.count(),
                                                    index
                                                )
                                            )
                                        )
                                    }
                                }
                            }

                            // Bar area with fixed grid lines layer and scrollable bars layer
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clipToBounds()
                            ) {
                                // Grid lines layer (always measured to viewport size)
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawChartContent(
                                        barsAreaWidth = availableWidthPx,
                                        drawXPadding = 0f,
                                        shouldDrawIndicators = false,
                                        shouldDrawGrid = true,
                                        shouldDrawBars = false
                                    )
                                }

                                // Scrollable bars layer
                                val barsContainerModifier = if (isScrollable) {
                                    Modifier
                                        .fillMaxSize()
                                        .horizontalScroll(scrollState)
                                } else {
                                    Modifier.fillMaxSize()
                                }

                                Box(modifier = barsContainerModifier) {
                                    val canvasModifier = if (isScrollable) {
                                        Modifier
                                            .width(with(density) { barsAreaWidth.toDp() })
                                            .fillMaxHeight()
                                    } else {
                                        Modifier.fillMaxSize()
                                    }

                                    Canvas(
                                        modifier = canvasModifier.then(chartPointerModifier)
                                    ) {
                                        drawChartContent(
                                            barsAreaWidth = barsAreaWidth,
                                            drawXPadding = 0f,
                                            shouldDrawIndicators = false,
                                            shouldDrawGrid = false,
                                            shouldDrawBars = true
                                        )
                                    }
                                }
                            }

                            // Fixed Y-axis indicators (End position)
                            if (indicatorProperties.enabled && indicatorProperties.position == IndicatorPosition.Horizontal.End) {
                                Canvas(
                                    modifier = Modifier
                                        .width(with(density) { indicatorAreaWidth.toDp() })
                                        .fillMaxHeight()
                                ) {
                                    indicators.forEachIndexed { index, indicator ->
                                        val measureResult =
                                            textMeasurer.measure(
                                                indicatorProperties.contentBuilder(indicator),
                                                style = indicatorProperties.textStyle
                                            )
                                        drawText(
                                            textLayoutResult = measureResult,
                                            topLeft = Offset(
                                                x = indicatorProperties.padding.value * density.density,
                                                y = (size.height - measureResult.size.height).spaceBetween(
                                                    itemCount = indicators.count(),
                                                    index
                                                )
                                            )
                                        )
                                    }
                                }
                            }
                        } else {
                            // ── Disabled layout: original single Canvas ──
                            Canvas(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .then(chartPointerModifier)
                            ) {
                                drawChartContent(
                                    barsAreaWidth = availableWidthPx,
                                    drawXPadding = xPadding,
                                    shouldDrawIndicators = true
                                )
                            }
                        }
                    }

                    // X-axis labels
                    if (isScrollMode) {
                        Row {
                            if (indicatorProperties.enabled && indicatorProperties.position == IndicatorPosition.Horizontal.Start) {
                                Spacer(Modifier.width(with(density) { indicatorAreaWidth.toDp() }))
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                            ) {
                                val labelModifier = if (isScrollable) {
                                    Modifier
                                        .horizontalScroll(scrollState)
                                        .width(with(density) { barsAreaWidth.toDp() })
                                } else {
                                    Modifier.fillMaxWidth()
                                }
                                Box(modifier = labelModifier) {
                                    HorizontalLabels(
                                        labelProperties = labelProperties,
                                        labels = labelProperties.labels.ifEmpty {
                                            data.map { it.label }
                                        },
                                        indicatorProperties = indicatorProperties,
                                        chartWidth = barsAreaWidth,
                                        density = density,
                                        textMeasurer = textMeasurer,
                                        xPadding = 0f,
                                        everyDataWidth = everyDataWidth,
                                        averageSpacingBetweenBars = averageSpacingBetweenBars
                                    )
                                }
                            }
                            if (indicatorProperties.enabled && indicatorProperties.position == IndicatorPosition.Horizontal.End) {
                                Spacer(Modifier.width(with(density) { indicatorAreaWidth.toDp() }))
                            }
                        }
                    } else {
                        HorizontalLabels(
                            labelProperties = labelProperties,
                            labels = labelProperties.labels.ifEmpty {
                                data.map { it.label }
                            },
                            indicatorProperties = indicatorProperties,
                            chartWidth = availableWidthPx,
                            density = density,
                            textMeasurer = textMeasurer,
                            xPadding = xPadding,
                            everyDataWidth = everyDataWidth,
                            averageSpacingBetweenBars = averageSpacingBetweenBars
                        )
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawPopup(
    selectedBar: SelectedBar,
    properties: PopupProperties,
    textMeasurer: TextMeasurer,
    progress: Float,
) {
    val popup = PopupProperties.Popup(
        dataIndex = selectedBar.dataIndex,
        valueIndex = selectedBar.valueIndex,
        value = selectedBar.bar.value
    )
    if (!properties.confirmDraw(popup)) return

    val measure = textMeasurer.measure(
        properties.contentBuilder(popup),
        style = properties.textStyle.copy(
            color = properties.textStyle.color.copy(
                alpha = 1f * progress
            )
        )
    )

    val textSize = measure.size.toSize()
    val popupSize = Size(
        width = (textSize.width + (properties.contentHorizontalPadding.toPx() * 2)),
        height = textSize.height + properties.contentVerticalPadding.toPx() * 2
    )
    val value = selectedBar.bar.value
    val barRect = selectedBar.rect
    val barWidth = barRect.right - barRect.left
    val barHeight = barRect.bottom - barRect.top
    var popupPosition = selectedBar.offset.copy(
        y = selectedBar.offset.y - popupSize.height + barHeight / 10,
        x = selectedBar.offset.x + barWidth / 2
    )
    if (value < 0) {
        popupPosition = popupPosition.copy(
            y = selectedBar.offset.y - barHeight / 10
        )
    }
    val outOfCanvas = popupPosition.x + popupSize.width > size.width
    if (outOfCanvas) {
        popupPosition = popupPosition.copy(
            x = (selectedBar.offset.x - popupSize.width) + barWidth / 2
        )
    }
    val cornerRadius =
        CornerRadius(
            properties.cornerRadius.toPx(),
            properties.cornerRadius.toPx()
        )
    drawPath(
        path = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(
                        offset = popupPosition,
                        size = popupSize.copy(
                            width = popupSize.width * progress
                        ),
                    ),
                    topRight = if (value < 0 && outOfCanvas) CornerRadius.Zero else cornerRadius,
                    topLeft = if (value < 0 && !outOfCanvas) CornerRadius.Zero else cornerRadius,
                    bottomRight = if (value > 0 && outOfCanvas) CornerRadius.Zero else cornerRadius,
                    bottomLeft = if (value > 0 && !outOfCanvas) CornerRadius.Zero else cornerRadius
                )
            )
        },
        color = properties.containerColor,
    )
    drawText(
        textLayoutResult = measure,
        topLeft = popupPosition.copy(
            x = popupPosition.x + properties.contentHorizontalPadding.toPx(),
            y = popupPosition.y + properties.contentVerticalPadding.toPx()
        ),
    )
}
