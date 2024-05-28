package ir.ehsannarmani.compose_charts

import android.view.MotionEvent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.snap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
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
import ir.ehsannarmani.compose_charts.models.LineProperties
import ir.ehsannarmani.compose_charts.models.PopupProperties
import ir.ehsannarmani.compose_charts.models.SelectedBar
import ir.ehsannarmani.compose_charts.utils.ImplementRCAnimation
import ir.ehsannarmani.compose_charts.utils.calculateOffset
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RowChart(
    modifier: Modifier = Modifier,
    data: List<Bars>,
    barProperties: BarProperties = BarProperties(),
    labelProperties: LabelProperties = LabelProperties(
        enabled = true,
        textStyle = TextStyle.Default
    ),
    indicatorProperties: IndicatorProperties = IndicatorProperties(textStyle = TextStyle.Default),
    labelHelperProperties: LabelHelperProperties = LabelHelperProperties(),
    dividerProperties: DividerProperties = DividerProperties(),
    gridProperties: GridProperties = GridProperties(),
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

    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    val everyDataHeight = with(density) {
        data.map { rowData ->
            rowData.values.map {
                (it.properties?.thickness
                    ?: barProperties.thickness).toPx() + (it.properties?.spacing
                    ?: barProperties.spacing).toPx()
            }.sum()
        }.average().toFloat()
    }

    val rectWithValue = remember {
        mutableStateListOf<Pair<Double, Rect>>()
    }

    val selectedValue = remember {
        mutableStateOf<SelectedBar?>(null)
    }

    val popupAnimation = remember {
        Animatable(0f)
    }

    val indicators = remember {
        maxValue.split(
            step = (maxValue - minValue) / indicatorProperties.count,
            minValue = minValue
        )
    }
    val indicatorAreaHeight = remember {
        if (indicatorProperties.enabled) {
            indicators.maxOf { textMeasurer.measure(indicatorProperties.contentBuilder(it)).size.height }
        } else {
            0
        }
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
        }
    )

    Column(modifier = modifier) {
        if (labelHelperProperties.enabled) {
            RCChartLabelHelper(data = data, textStyle = labelHelperProperties.textStyle)
            Spacer(modifier = Modifier.height(24.dp))
        }
        Row(modifier = Modifier.fillMaxSize()) {
            if (labelProperties.enabled) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(bottom = (indicatorAreaHeight / density.density).dp)
                        .padding(vertical = (((everyDataHeight) / data.count()) / density.density).dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    data.forEach {
                        BasicText(text = it.label, style = labelProperties.textStyle)
                    }
                }
                Spacer(modifier = Modifier.width(labelProperties.padding))
            }
            Canvas(modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    if (!popupProperties.enabled) return@pointerInput
                    detectDragGestures { change, dragAmount ->
                        rectWithValue
                            .lastOrNull { (value,rect)->
                                change.position.y in rect.top .. rect.bottom
                            }
                            ?.let { (value,rect)->
                                selectedValue.value = SelectedBar(
                                    value = value,
                                    rect = rect,
                                    offset = Offset(
                                        x = if (value > 0) rect.right else rect.left,
                                        y = rect.top
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
                .pointerInteropFilter { event ->
                    if (event.action == MotionEvent.ACTION_DOWN && popupProperties.enabled) {
                        val position = Offset(event.x, event.y)
                        rectWithValue
                            .lastOrNull { it.second.contains(position) }
                            ?.let {(value,rect)->
                                selectedValue.value = SelectedBar(
                                    value = value,
                                    rect = rect,
                                    offset = Offset(
                                        x = if (value > 0) rect.right else rect.left,
                                        y = rect.top
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
                    }
                    false
                }) {
                val barAreaHeight = size.height - indicatorAreaHeight
                val barAreaWidth = size.width

                val zeroX = size.width - calculateOffset(
                    maxValue = maxValue.toFloat(),
                    minValue = minValue.toFloat(),
                    total = size.width,
                    value = 0.0f
                )

                drawGridLines(
                    size = size.copy(height = barAreaHeight, width = barAreaWidth),
                    xAxisProperties = gridProperties.xAxisProperties,
                    yAxisProperties = gridProperties.yAxisProperties,
                    dividersProperties = dividerProperties,
                    gridEnabled = gridProperties.enabled
                )
                data.forEachIndexed { dataIndex, bars ->
                    bars.values.forEachIndexed { barIndex, bar ->

                        val stroke = (bar.properties?.thickness ?: barProperties.thickness).toPx()
                        val spacing = (bar.properties?.spacing ?: barProperties.spacing).toPx()
                        var width = ((barAreaWidth * bar.value) / (maxValue-minValue)) * bar.animator.value

                        val everyBarHeight = (stroke + spacing)

                        val barY = (everyBarHeight * barIndex) + (barAreaHeight - everyDataHeight).spaceBetween(
                            itemCount = data.count(),
                            index = dataIndex
                        )
                        val barX = if (bar.value > 0) size.width-zeroX else (size.width-zeroX-width.absoluteValue.toFloat()).coerceAtLeast(0f)
                        val rect = Rect(
                            offset = Offset(x = barX, y = barY),
                            size = Size(height = stroke, width = width.absoluteValue.toFloat())
                        )

                        val path = Path()

                        if (rectWithValue.none { it.second == rect }) rectWithValue.add(bar.value to rect)

                        var radius = (bar.properties?.cornerRadius ?: barProperties.cornerRadius)
                        if (bar.value < 0){
                            radius = radius.reverse(horizontal = true)
                        }

                        path.addRoundRect(rect = rect, radius = radius)

                        val alpha = if (rect == selectedValue.value?.rect) {
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
                if (indicatorProperties.enabled) {
                    indicators.reversed().forEachIndexed { index, indicator ->
                        val measureResult =
                            textMeasurer.measure(
                                indicatorProperties.contentBuilder(indicator),
                                style = indicatorProperties.textStyle
                            )
                        drawText(
                            textLayoutResult = measureResult,
                            topLeft = Offset(
                                x = (barAreaWidth - measureResult.size.width).spaceBetween(
                                    itemCount = indicators.count(),
                                    index = index
                                ), y = size.height - indicatorAreaHeight / 2
                            )
                        )
                    }
                }


                if (selectedValue.value != null) {
                    val measure = textMeasurer.measure(
                        popupProperties.contentBuilder(selectedValue.value!!.value),
                        style = popupProperties.textStyle.copy(
                            color = popupProperties.textStyle.color.copy(
                                alpha = popupAnimation.value * 1f
                            )
                        )
                    )
                    val textSize = measure.size.toSize()
                    val popupSize = Size(
                        width = (textSize.width + (popupProperties.contentHorizontalPadding.toPx() * 2)),
                        height = textSize.height + popupProperties.contentVerticalPadding.toPx() * 2
                    )
                    val value = selectedValue.value!!.value
                    val barRect = selectedValue.value!!.rect
                    val barHeight = barRect.bottom-barRect.top
                    val barWidth = barRect.right-barRect.left
                    var popupPosition = selectedValue.value!!.offset.copy(
                        y = selectedValue.value!!.offset.y-popupSize.height+(barHeight/2),
                        x = selectedValue.value!!.offset.x - (barWidth/10)
                    )
                    if (value < 0){
                        popupPosition = popupPosition.copy(
                            x = selectedValue.value!!.offset.x - popupSize.width + barWidth/10
                        )
                    }
                    val outOfCanvas = popupPosition.x+popupSize.width > size.width
                    if (outOfCanvas){
                        popupPosition = popupPosition.copy(
                            x = (selectedValue.value!!.offset.x - popupSize.width)-20f
                        )
                    }
                    val cornerRadius = CornerRadius(popupProperties.cornerRadius.toPx(), popupProperties.cornerRadius.toPx())
                    drawPath(
                        path = Path().apply {
                            addRoundRect(
                                RoundRect(
                                    rect = Rect(
                                        offset = popupPosition,
                                        size = popupSize.copy(width = popupSize.width*popupAnimation.value,),
                                    ),
                                    topRight = cornerRadius,
                                    topLeft = cornerRadius,
                                    bottomRight = if (outOfCanvas || value < 0) CornerRadius.Zero else cornerRadius,
                                    bottomLeft = if (!outOfCanvas && value > 0) CornerRadius.Zero else cornerRadius,
                                )
                            )
                        },
                        color = popupProperties.containerColor
                    )
                    drawText(
                        textLayoutResult = measure,
                        topLeft = popupPosition.copy(
                            x = popupPosition.x + popupProperties.contentHorizontalPadding.toPx(),
                            y = popupPosition.y + popupProperties.contentVerticalPadding.toPx()
                        ),
                    )
                }
            }
        }
    }
}

