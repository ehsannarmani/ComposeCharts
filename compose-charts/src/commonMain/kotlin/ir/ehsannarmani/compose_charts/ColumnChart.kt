package ir.ehsannarmani.compose_charts

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.snap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
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
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
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
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.DividerProperties
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.IndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.PopupProperties
import ir.ehsannarmani.compose_charts.models.SelectedBar
import ir.ehsannarmani.compose_charts.utils.ImplementRCAnimation
import ir.ehsannarmani.compose_charts.utils.calculateOffset
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ColumnChart(
    modifier: Modifier = Modifier,
    data: List<Bars>,
    barProperties: BarProperties = BarProperties(),
    onBarClick: ((Bars.Data) -> Unit)? = null,
    onBarLongClick: ((Bars.Data) -> Unit)? = null,
    labelProperties: LabelProperties = LabelProperties(
        textStyle = TextStyle.Default,
        enabled = true
    ),
    indicatorProperties: IndicatorProperties = IndicatorProperties(textStyle = TextStyle.Default),
    dividerProperties: DividerProperties = DividerProperties(),
    gridProperties: GridProperties = GridProperties(),
    labelHelperProperties: LabelHelperProperties = LabelHelperProperties(),
    animationMode: AnimationMode = AnimationMode.Together(),
    animationSpec: AnimationSpec<Float> = snap(),
    animationDelay: Long = 200,
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
    require(data.isNotEmpty()) {
        "Chart data is empty"
    }
    require(maxValue >= data.maxOf { it.values.maxOf { it.value } }) {
        "Chart data must be at most $maxValue (Specified Max Value)"
    }
    require(minValue <= 0) {
        "Min value in column chart must be 0 or lower."
    }
    require(minValue <= data.minOf { it.values.minOf { it.value } }) {
        "Chart data must be at least $minValue (Specified Min Value)"
    }

    val density = LocalDensity.current

    val everyDataWidth = with(density) {
        data.map { rowData ->
            rowData.values.map {
                (it.properties?.thickness
                    ?: barProperties.thickness).toPx() + (it.properties?.spacing
                    ?: barProperties.spacing).toPx()
            }.sum()
        }.average().toFloat()
    }

    val barWithRect = remember {
        mutableStateListOf<Pair<Bars.Data, Rect>>()
    }

    val selectedValue = remember {
        mutableStateOf<SelectedBar?>(null)
    }

    val popupAnimation = remember {
        Animatable(0f)
    }

    val indicators = remember {
        split(
            count = indicatorProperties.count,
            minValue = minValue,
            maxValue = maxValue
        )
    }
    val indicatorAreaWidth = remember {
        if (indicatorProperties.enabled) {
            indicators.maxOf { textMeasurer.measure(indicatorProperties.contentBuilder(it)).size.width } + (16 * density.density)
        } else {
            0f
        }
    }

    val xPadding = remember {
        if (indicatorProperties.enabled && indicatorProperties.position == IndicatorProperties.Position.Start) {
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
    Column(modifier = modifier) {
        if (labelHelperProperties.enabled) {
            RCChartLabelHelper(data = data, textStyle = labelHelperProperties.textStyle)
            Spacer(modifier = Modifier.height(24.dp))
        }
        Row(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            val scope = rememberCoroutineScope()
            Canvas(modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    if (popupProperties.enabled) {
                        detectDragGestures { change, dragAmount ->
                            barWithRect
                                .lastOrNull { (value, rect) ->
                                    change.position.x in rect.left..rect.right
                                }
                                ?.let { (bar, rect) ->
                                    selectedValue.value = SelectedBar(
                                        bar = bar,
                                        rect = rect,
                                        offset = Offset(
                                            rect.left,
                                            if (bar.value < 0) rect.bottom else rect.top
                                        )
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
                                .lastOrNull { (_, rect) ->
                                    rect.contains(position)
                                }
                                ?.let { (bar, rect) ->
                                    if (popupProperties.enabled) {
                                        selectedValue.value = SelectedBar(
                                            bar = bar,
                                            rect = rect,
                                            offset = Offset(
                                                rect.left,
                                                if (bar.value < 0) rect.bottom else rect.top
                                            )
                                        )
                                        scope.launch {
                                            popupAnimation.snapTo(0f)
                                            popupAnimation.animateTo(
                                                1f,
                                                animationSpec = popupProperties.animationSpec
                                            )
                                        }
                                    }
                                    onBarClick?.invoke(bar)
                                }
                        },
                        onLongPress = {
                            val position = Offset(it.x, it.y)
                            barWithRect
                                .lastOrNull { (_, rect) ->
                                    rect.contains(position)
                                }
                                ?.let { (bar, _) ->
                                    onBarLongClick?.invoke(bar)
                                }
                        }
                    )
                }
            ) {

                val barsAreaWidth = size.width - (indicatorAreaWidth)
                chartWidth.value = barsAreaWidth
                val zeroY = size.height - calculateOffset(
                    maxValue = maxValue,
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
                        val textWidth = measureResult.size.width
                        val x = when (indicatorProperties.position) {
                            IndicatorProperties.Position.Start -> 0f
                            IndicatorProperties.Position.End -> size.width - textWidth
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
                    indicatorProperties = indicatorProperties,
                    xAxisProperties = gridProperties.xAxisProperties,
                    yAxisProperties = gridProperties.yAxisProperties,
                    gridEnabled = gridProperties.enabled
                )

                data.forEachIndexed { dataIndex, columnChart ->
                    columnChart.values.forEachIndexed { valueIndex, col ->
                        val stroke = (col.properties?.thickness ?: barProperties.thickness).toPx()
                        val spacing = (col.properties?.spacing ?: barProperties.spacing).toPx()

                        val barHeight =
                            ((col.value * size.height) / (maxValue - minValue)) * col.animator.value
                        val everyBarWidth = (stroke + spacing)

                        val barX =
                            (valueIndex * everyBarWidth) + (barsAreaWidth - everyDataWidth).spaceBetween(
                                itemCount = data.count(),
                                index = dataIndex
                            ) + xPadding
                        val rect = Rect(
                            offset = Offset(
                                x = barX,
                                y = (zeroY - barHeight.toFloat().coerceAtLeast(0f))
                            ),
                            size = Size(width = stroke, height = barHeight.absoluteValue.toFloat()),
                        )
                        if (barWithRect.none { it.second == rect }) barWithRect.add(col to rect)
                        val path = Path()

                        var radius = (col.properties?.cornerRadius ?: barProperties.cornerRadius)
                        if (col.value < 0) {
                            radius = radius.reverse()
                        }

                        path.addRoundRect(rect = rect, radius = radius)
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
        if (labelProperties.enabled) {
            Spacer(modifier = Modifier.height(labelProperties.padding))

            val widthModifier =
                if (indicatorProperties.position == IndicatorProperties.Position.End) {
                    Modifier.width((chartWidth.value / density.density).dp)
                } else {
                    Modifier.fillMaxWidth()
                }

            Row(
                modifier = widthModifier
                    .padding(
                        start = (xPadding / density.density).dp,
                    ), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                data.forEach {
                    BasicText(
                        text = it.label,
                        style = labelProperties.textStyle,
                    )
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
    val measure = textMeasurer.measure(
        properties.contentBuilder(selectedBar.bar.value),
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
