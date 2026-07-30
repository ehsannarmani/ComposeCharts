package ir.ehsannarmani.compose_charts

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.ExperimentalComposeUiApi
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
import ir.ehsannarmani.compose_charts.extensions.MotionEvent
import ir.ehsannarmani.compose_charts.extensions.addRoundRect
import ir.ehsannarmani.compose_charts.extensions.drawGridLines
import ir.ehsannarmani.compose_charts.extensions.pointerInteropFilter
import ir.ehsannarmani.compose_charts.extensions.spaceBetween
import ir.ehsannarmani.compose_charts.extensions.split
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.BarPopupData
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.DividerProperties
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.IndicatorPosition
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.PopupProperties
import ir.ehsannarmani.compose_charts.models.ScrollMode
import ir.ehsannarmani.compose_charts.models.SelectedBar
import ir.ehsannarmani.compose_charts.models.VerticalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.ZeroLineProperties
import ir.ehsannarmani.compose_charts.models.asRadiusPx
import ir.ehsannarmani.compose_charts.utils.ImplementRCAnimation
import ir.ehsannarmani.compose_charts.utils.VerticalLabels
import ir.ehsannarmani.compose_charts.utils.calculateOffset
import ir.ehsannarmani.compose_charts.utils.checkRCMaxValue
import ir.ehsannarmani.compose_charts.utils.checkRCMinValue
import ir.ehsannarmani.compose_charts.utils.computeScrollConfig
import ir.ehsannarmani.compose_charts.utils.rememberComputedChartMaxValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RowChart(
    modifier: Modifier = Modifier,
    data: List<Bars>,
    barProperties: BarProperties = BarProperties(),
    onBarClick: ((BarPopupData) -> Unit)? = null,
    onBarLongClick: ((BarPopupData) -> Unit)? = null,
    labelProperties: LabelProperties = LabelProperties(
        enabled = true,
        textStyle = TextStyle.Default
    ),
    indicatorProperties: VerticalIndicatorProperties = VerticalIndicatorProperties(textStyle = TextStyle.Default),
    labelHelperProperties: LabelHelperProperties = LabelHelperProperties(),
    dividerProperties: DividerProperties = DividerProperties(),
    zeroLineProperties: ZeroLineProperties = ZeroLineProperties(),
    gridProperties: GridProperties = GridProperties(),
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
    scrollMode: ScrollMode = ScrollMode.Disabled,
) {
    checkRCMinValue(minValue, data)
    checkRCMaxValue(maxValue, data)

    val onBarClick by rememberUpdatedState(onBarClick)
    val onBarLongClick by rememberUpdatedState(onBarLongClick)
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    val everyDataHeight = with(density) {
        data.maxOfOrNull { rowData ->
            rowData.values.map {
                (it.properties?.thickness
                    ?: barProperties.thickness).toPx() + (it.properties?.spacing
                    ?: barProperties.spacing).toPx()
            }.sum()
        } ?: 0f
    }
    val averageSpacingBetweenBars = with(density) {
        data.map { it.values }.flatten()
            .map { (it.properties?.spacing ?: barProperties.spacing).toPx() }.average()
    }

    val barWithRect = remember {
        mutableStateListOf<BarPopupData>()
    }

    val selectedBar = remember {
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

    val computedMaxValue = rememberComputedChartMaxValue(minValue, maxValue, indicatorProperties.count)
    val indicators = remember(minValue, computedMaxValue) {
        indicatorProperties.indicators.ifEmpty {
            split(
                count = indicatorProperties.count,
                minValue = minValue,
                maxValue = computedMaxValue
            )
        }
    }
    val indicatorAreaHeight = remember {
        if (indicatorProperties.enabled) {
            indicators.maxOf { textMeasurer.measure(indicatorProperties.contentBuilder(it)).size.height } + indicatorProperties.padding.value * density.density
        } else {
            0f
        }
    }

    val isScrollMode = scrollMode !is ScrollMode.Disabled
    val scrollState = rememberScrollState()
    val scrollEnabled = remember(scrollMode) { mutableStateOf(scrollMode is ScrollMode.Always) }

    // Pre-compute VerticalLabels width (used for fixed-indicator spacer alignment)
    val verticalLabelAreaWidthDp = remember(labelProperties, data) {
        if (labelProperties.enabled) {
            val labels = labelProperties.labels.ifEmpty { data.map { it.label } }
            if (labels.isNotEmpty()) {
                val maxTextWidth = labels.maxOf {
                    textMeasurer.measure(it, style = labelProperties.textStyle).size.width
                }
                (maxTextWidth / density.density).dp + labelProperties.padding
            } else 0.dp
        } else 0.dp
    }

    LaunchedEffect(selectedBar.value) {
        if (selectedBar.value != null) {
            delay(popupProperties.duration)
            popupAnimation.animateTo(0f, animationSpec = popupProperties.animationSpec)
            selectedBar.value = null
        }
    }

    ImplementRCAnimation(
        data = data,
        animationMode = animationMode,
        spec = { it.animationSpec ?: animationSpec },
        delay = animationDelay,
        before = {
        }
    )

    // Shared drawing logic — parameterised by barAreaHeight, yPadding, and whether to draw indicators/grid/bars
    fun DrawScope.drawRowContent(
        barAreaHeight: Float,
        barAreaWidth: Float,
        drawYPadding: Float,
        shouldDrawIndicators: Boolean,
        shouldDrawGrid: Boolean = true,
        shouldDrawBars: Boolean = true
    ) {
        val zeroX = barAreaWidth - calculateOffset(
            maxValue = computedMaxValue,
            minValue = minValue,
            total = barAreaWidth,
            value = 0.0f
        )

        if (shouldDrawGrid) {
            drawGridLines(
                size = size.copy(height = barAreaHeight, width = barAreaWidth),
                indicatorPosition = indicatorProperties.position,
                xAxisProperties = gridProperties.xAxisProperties,
                yAxisProperties = gridProperties.yAxisProperties,
                dividersProperties = dividerProperties,
                gridEnabled = gridProperties.enabled,
                yPadding = drawYPadding
            )
        }

        val drawZeroLine = {
            drawLine(
                brush = zeroLineProperties.color,
                start = Offset(x = zeroX.toFloat(), y = drawYPadding),
                end = Offset(
                    x = zeroX.toFloat(),
                    y = drawYPadding + barAreaHeight * zeroLineAnimation.value
                ),
                pathEffect = zeroLineProperties.style.pathEffect,
                strokeWidth = zeroLineProperties.thickness.toPx()
            )
        }
        if (shouldDrawGrid && zeroLineProperties.enabled && zeroLineProperties.zType == ZeroLineProperties.ZType.Under) {
            drawZeroLine()
        }

        if (shouldDrawBars) {
            data.forEachIndexed { dataIndex, bars ->
                bars.values.forEachIndexed { barIndex, bar ->
                    if (bar.value != 0.0) {
                        val stroke =
                            (bar.properties?.thickness ?: barProperties.thickness).toPx()
                        val spacing =
                            (bar.properties?.spacing ?: barProperties.spacing).toPx()
                        val width =
                            ((barAreaWidth * bar.value) / (computedMaxValue - minValue)) * bar.animator.value

                        val everyBarHeight = (stroke + spacing)

                        val barY =
                            (everyBarHeight * barIndex) + (barAreaHeight - everyDataHeight).spaceBetween(
                                itemCount = data.count(),
                                index = dataIndex
                            ) + (averageSpacingBetweenBars / 2).toFloat()
                        val barX =
                            (if (bar.value > 0) barAreaWidth - zeroX else (barAreaWidth - zeroX - width.absoluteValue.toFloat()).coerceAtLeast(
                                0.0
                            )).toFloat()
                        val y = barY + drawYPadding
                        val rect = Rect(
                            offset = Offset(x = barX, y = y),
                            size = Size(
                                height = stroke,
                                width = width.absoluteValue.toFloat()
                            )
                        )

                        val path = Path()

                        if (barWithRect.none { it.rect == rect }) {
                            barWithRect.add(BarPopupData(bar, rect, dataIndex, barIndex))
                        }
                        var radius =
                            (bar.properties?.cornerRadius ?: barProperties.cornerRadius)
                        if (bar.value < 0) {
                            radius = radius.reverse(horizontal = true)
                        }

                        path.addRoundRect(rect = rect, radius = radius.asRadiusPx(this))

                        val alpha = if (rect == selectedBar.value?.rect) {
                            1f - (barAlphaDecreaseOnPopup * popupAnimation.value)
                        } else {
                            1f
                        }
                        drawPath(
                            path = path,
                            brush = bar.color,
                            alpha = alpha,
                            style = (bar.properties?.style
                                ?: barProperties.style).getStyle(density.density)
                        )
                    }
                }
            }
            if (shouldDrawGrid && zeroLineProperties.enabled && zeroLineProperties.zType == ZeroLineProperties.ZType.Above) {
                drawZeroLine()
            }

            // Draw value indicators inside Canvas (Disabled mode only)
            if (shouldDrawIndicators && indicatorProperties.enabled) {
                indicators.reversed().forEachIndexed { index, indicator ->
                    val measureResult =
                        textMeasurer.measure(
                            indicatorProperties.contentBuilder(indicator),
                            style = indicatorProperties.textStyle,
                        )
                    val indicatorY = when (indicatorProperties.position) {
                        IndicatorPosition.Vertical.Top -> 0f
                        IndicatorPosition.Vertical.Bottom -> size.height - indicatorAreaHeight + indicatorProperties.padding.value * density.density
                    }
                    drawText(
                        textLayoutResult = measureResult,
                        topLeft = Offset(
                            x = (barAreaWidth - measureResult.size.width).spaceBetween(
                                itemCount = indicators.count(),
                                index = index
                            ), y = indicatorY
                        )
                    )
                }
            }

            selectedBar.value?.let { selectedValue ->
                drawPopUp(
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
        .pointerInput(scrollEnabled.value) {
            if (!popupProperties.enabled || scrollEnabled.value) return@pointerInput
            detectVerticalDragGestures { change, _ ->
                barWithRect
                    .lastOrNull { popupData ->
                        change.position.y in popupData.rect.top..popupData.rect.bottom
                    }
                    ?.let { popupData ->
                        selectedBar.value = SelectedBar(
                            bar = popupData.bar,
                            rect = popupData.rect,
                            offset = Offset(
                                x = if (popupData.bar.value > 0) popupData.rect.right else popupData.rect.left,
                                y = popupData.rect.top
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
                                selectedBar.value = SelectedBar(
                                    bar = popupData.bar,
                                    rect = popupData.rect,
                                    offset = Offset(
                                        x = if (popupData.bar.value > 0) popupData.rect.right else popupData.rect.left,
                                        y = popupData.rect.top
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
        .pointerInteropFilter { event ->
            if (event.action == MotionEvent.ACTION_DOWN && popupProperties.enabled) {

            }
            false
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

            if (isScrollMode) {
                // ── Scroll layout: indicators in separate fixed Canvases ──
                Column(modifier = Modifier.fillMaxSize()) {
                    // Fixed value indicators (Top position)
                    if (indicatorProperties.enabled && indicatorProperties.position == IndicatorPosition.Vertical.Top) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            if (labelProperties.enabled) {
                                Spacer(Modifier.width(verticalLabelAreaWidthDp))
                            }
                            Canvas(
                                modifier = Modifier
                                    .height(with(density) { indicatorAreaHeight.toDp() })
                                    .fillMaxWidth()
                            ) {
                                indicators.reversed().forEachIndexed { index, indicator ->
                                    val measureResult =
                                        textMeasurer.measure(
                                            indicatorProperties.contentBuilder(indicator),
                                            style = indicatorProperties.textStyle,
                                        )
                                    drawText(
                                        textLayoutResult = measureResult,
                                        topLeft = Offset(
                                            x = (size.width - measureResult.size.width).spaceBetween(
                                                itemCount = indicators.count(),
                                                index = index
                                            ), y = 0f
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Bar area with fixed grid lines layer and scrollable content layer (bars + labels)
                    BoxWithConstraints(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        val availableHeightPx = constraints.maxHeight.toFloat()
                        val scrollConfig = computeScrollConfig(
                            scrollMode = scrollMode,
                            availableSize = availableHeightPx,
                            everyDataSize = everyDataHeight,
                            dataCount = data.count(),
                            density = density
                        )
                        scrollEnabled.value = scrollConfig.scrollEnabled

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clipToBounds()
                        ) {
                            // Fixed grid lines layer (always measured to viewport size)
                            Row(modifier = Modifier.fillMaxSize()) {
                                if (labelProperties.enabled) {
                                    Spacer(Modifier.width(verticalLabelAreaWidthDp))
                                }
                                Canvas(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .weight(1f)
                                ) {
                                    drawRowContent(
                                        barAreaHeight = size.height,
                                        barAreaWidth = size.width,
                                        drawYPadding = 0f,
                                        shouldDrawIndicators = false,
                                        shouldDrawGrid = true,
                                        shouldDrawBars = false
                                    )
                                }
                            }

                            // Scrollable content area (bars + labels)
                            val rowContainerModifier = if (scrollEnabled.value) {
                                Modifier
                                    .fillMaxSize()
                                    .verticalScroll(scrollState)
                            } else {
                                Modifier.fillMaxSize()
                            }

                            Row(modifier = rowContainerModifier) {
                                VerticalLabels(
                                    labelProperties = labelProperties,
                                    labels = labelProperties.labels.ifEmpty {
                                        data.map { it.label }
                                    },
                                    dataCount = data.count(),
                                    indicatorAreaHeight = 0f,
                                    density = density,
                                    indicatorProperties = indicatorProperties,
                                    everyDataHeight = everyDataHeight
                                )
                                val canvasModifier = if (scrollEnabled.value) {
                                    Modifier
                                        .height(with(density) { scrollConfig.effectiveSize.toDp() })
                                        .fillMaxWidth()
                                } else {
                                    Modifier.fillMaxHeight().weight(1f)
                                }

                                Canvas(
                                    modifier = canvasModifier.then(chartPointerModifier)
                                ) {
                                    drawRowContent(
                                        barAreaHeight = size.height,
                                        barAreaWidth = size.width,
                                        drawYPadding = 0f,
                                        shouldDrawIndicators = false,
                                        shouldDrawGrid = false,
                                        shouldDrawBars = true
                                    )
                                }
                            }
                        }
                    }

                    // Fixed value indicators (Bottom position)
                    if (indicatorProperties.enabled && indicatorProperties.position == IndicatorPosition.Vertical.Bottom) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            if (labelProperties.enabled) {
                                Spacer(Modifier.width(verticalLabelAreaWidthDp))
                            }
                            Canvas(
                                modifier = Modifier
                                    .height(with(density) { indicatorAreaHeight.toDp() })
                                    .fillMaxWidth()
                            ) {
                                indicators.reversed().forEachIndexed { index, indicator ->
                                    val measureResult =
                                        textMeasurer.measure(
                                            indicatorProperties.contentBuilder(indicator),
                                            style = indicatorProperties.textStyle,
                                        )
                                    drawText(
                                        textLayoutResult = measureResult,
                                        topLeft = Offset(
                                            x = (size.width - measureResult.size.width).spaceBetween(
                                                itemCount = indicators.count(),
                                                index = index
                                            ), y = indicatorProperties.padding.value * density.density
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // ── Disabled layout: original single-Canvas Row ──
                Row(modifier = Modifier.fillMaxSize()) {
                    VerticalLabels(
                        labelProperties = labelProperties,
                        labels = labelProperties.labels.ifEmpty {
                            data.map { it.label }
                        },
                        dataCount = data.count(),
                        indicatorAreaHeight = indicatorAreaHeight,
                        density = density,
                        indicatorProperties = indicatorProperties,
                        everyDataHeight = everyDataHeight
                    )
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .then(chartPointerModifier)
                    ) {
                        val barAreaHeight = size.height - indicatorAreaHeight
                        val barAreaWidth = size.width
                        val yPadding =
                            if (indicatorProperties.position == IndicatorPosition.Vertical.Top) indicatorAreaHeight else 0f

                        drawRowContent(
                            barAreaHeight = barAreaHeight,
                            barAreaWidth = barAreaWidth,
                            drawYPadding = yPadding,
                            shouldDrawIndicators = true
                        )
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawPopUp(
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
    if (!properties.confirmDraw(popup))
        return

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
    val barHeight = barRect.bottom - barRect.top
    val barWidth = barRect.right - barRect.left
    var popupPosition = selectedBar.offset.copy(
        y = selectedBar.offset.y - popupSize.height + (barHeight / 2),
        x = selectedBar.offset.x - (barWidth / 10)
    )
    if (value < 0) {
        popupPosition = popupPosition.copy(
            x = selectedBar.offset.x - popupSize.width + barWidth / 10
        )
    }
    val outOfCanvas = popupPosition.x + popupSize.width > size.width
    if (outOfCanvas) {
        popupPosition = popupPosition.copy(
            x = (selectedBar.offset.x - popupSize.width) - 20f
        )
    }
    val cornerRadius = CornerRadius(properties.cornerRadius.toPx(), properties.cornerRadius.toPx())
    drawPath(
        path = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(
                        offset = popupPosition,
                        size = popupSize.copy(width = popupSize.width * progress),
                    ),
                    topRight = cornerRadius,
                    topLeft = cornerRadius,
                    bottomRight = if (outOfCanvas || value < 0) CornerRadius.Zero else cornerRadius,
                    bottomLeft = if (!outOfCanvas && value > 0) CornerRadius.Zero else cornerRadius,
                )
            )
        },
        color = properties.containerColor
    )
    drawText(
        textLayoutResult = measure,
        topLeft = popupPosition.copy(
            x = popupPosition.x + properties.contentHorizontalPadding.toPx(),
            y = popupPosition.y + properties.contentVerticalPadding.toPx()
        ),
    )
}
