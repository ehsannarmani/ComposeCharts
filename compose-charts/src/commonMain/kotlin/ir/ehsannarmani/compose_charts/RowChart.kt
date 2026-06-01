package ir.ehsannarmani.compose_charts

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
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
import ir.ehsannarmani.compose_charts.models.*
import ir.ehsannarmani.compose_charts.utils.*
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
    minValue: Double = if (data.any { it.values.any { it.value < 0 } }) -maxValue else 0.0
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
        data.flatMap { it.values }
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

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Column(modifier = modifier) {
            if (labelHelperProperties.enabled) {
                RCChartLabelHelper(
                    data = data,
                    properties = labelHelperProperties
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
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
                        .pointerInput(Unit) {
                            if (!popupProperties.enabled) return@pointerInput
                            detectDragGestures { change, _ ->
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
                ) {
                    val barAreaHeight = size.height - indicatorAreaHeight
                    val barAreaWidth = size.width

                    val zeroX = size.width - calculateOffset(
                        maxValue = computedMaxValue,
                        minValue = minValue,
                        total = size.width,
                        value = 0.0f
                    )

                    val yPadding =
                        if (indicatorProperties.position == IndicatorPosition.Vertical.Top) indicatorAreaHeight else 0f

                    drawGridLines(
                        size = size.copy(height = barAreaHeight, width = barAreaWidth),
                        indicatorPosition = indicatorProperties.position,
                        xAxisProperties = gridProperties.xAxisProperties,
                        yAxisProperties = gridProperties.yAxisProperties,
                        dividersProperties = dividerProperties,
                        gridEnabled = gridProperties.enabled,
                        yPadding = yPadding
                    )
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
                                    (if (bar.value > 0) size.width - zeroX else (size.width - zeroX - width.absoluteValue.toFloat()).coerceAtLeast(
                                        0.0
                                    )).toFloat()
                                val y =
                                    if (indicatorProperties.position == IndicatorPosition.Vertical.Top) barY + indicatorAreaHeight else barY
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
                    if (indicatorProperties.enabled) {
                        indicators.reversed().forEachIndexed { index, indicator ->
                            val measureResult =
                                textMeasurer.measure(
                                    indicatorProperties.contentBuilder(indicator),
                                    style = indicatorProperties.textStyle,
                                )
                            val y = when (indicatorProperties.position) {
                                IndicatorPosition.Vertical.Top -> 0f
                                IndicatorPosition.Vertical.Bottom -> size.height - indicatorAreaHeight + indicatorProperties.padding.value * density.density
                            }
                            drawText(
                                textLayoutResult = measureResult,
                                topLeft = Offset(
                                    x = (barAreaWidth - measureResult.size.width).spaceBetween(
                                        itemCount = indicators.count(),
                                        index = index
                                    ), y = y
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

