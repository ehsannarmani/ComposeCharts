package ir.ehsannarmani.compose_charts

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
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
import ir.ehsannarmani.compose_charts.models.SelectedBar
import ir.ehsannarmani.compose_charts.models.asRadiusPx
import ir.ehsannarmani.compose_charts.utils.HorizontalLabels
import ir.ehsannarmani.compose_charts.utils.ImplementRCAnimation
import ir.ehsannarmani.compose_charts.utils.calculateOffset
import ir.ehsannarmani.compose_charts.utils.checkRCMaxValue
import ir.ehsannarmani.compose_charts.utils.checkRCMinValue
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
) {
    checkRCMinValue(minValue, data)
    checkRCMaxValue(maxValue, data)

    val density = LocalDensity.current

    val everyDataWidth = with(density) {
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

    val selectedValue = remember {
        mutableStateOf<SelectedBar?>(null)
    }

    val popupAnimation = remember {
        Animatable(0f)
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

    val chartWidth = remember {
        mutableFloatStateOf(0f)
    }

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
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Column(modifier = modifier) {
            if (labelHelperProperties.enabled) {
                RCChartLabelHelper(
                    data = data,
                    textStyle = labelHelperProperties.textStyle,
                    labelCountPerLine = labelHelperProperties.labelCountPerLine
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                val scope = rememberCoroutineScope()
                Canvas(
                    modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        if (popupProperties.enabled) {
                            detectHorizontalDragGestures { change, dragAmount ->
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
                ) {

                    val barsAreaWidth = size.width - (indicatorAreaWidth)
                    chartWidth.value = barsAreaWidth
                    val zeroY = size.height - calculateOffset(
                        maxValue = computedMaxValue,
                        minValue = minValue,
                        total = size.height,
                        value = 0.0f
                    ).toFloat()


                    if (indicatorProperties.enabled) {
                        indicators.forEachIndexed { index, indicator ->
                            val measureResult =
                                textMeasurer.measure(
                                    indicatorProperties.contentBuilder(indicator),
                                    style = indicatorProperties.textStyle
                                )
                            val x = when (indicatorProperties.position) {
                                IndicatorPosition.Horizontal.Start -> 0f
                                IndicatorPosition.Horizontal.End -> barsAreaWidth + indicatorProperties.padding.value * density.density
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

                    drawGridLines(
                        xPadding = xPadding,
                        size = size.copy(width = barsAreaWidth),
                        dividersProperties = dividerProperties,
                        indicatorPosition = indicatorProperties.position,
                        xAxisProperties = gridProperties.xAxisProperties,
                        yAxisProperties = gridProperties.yAxisProperties,
                        gridEnabled = gridProperties.enabled
                    )

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
                                    ) + xPadding + (averageSpacingBetweenBars / 2).toFloat()
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
            HorizontalLabels(
                labelProperties = labelProperties,
                labels = labelProperties.labels.ifEmpty {
                    data
                        .map { it.label }
                },
                indicatorProperties = indicatorProperties,
                chartWidth = chartWidth.value,
                density = density,
                textMeasurer = textMeasurer,
                xPadding = xPadding
            )
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
