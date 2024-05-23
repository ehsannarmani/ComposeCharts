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
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
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
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.IndicatorProperties
import ir.ehsannarmani.compose_charts.models.PopupProperties
import ir.ehsannarmani.compose_charts.models.SelectedBar
import ir.ehsannarmani.compose_charts.utils.ImplementRCAnimation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RowChart(
    modifier: Modifier = Modifier,
    data: List<Bars>,
    barProperties: BarProperties = BarProperties(),
    labelStyle: TextStyle = LocalTextStyle.current,
    indicatorProperties: IndicatorProperties = IndicatorProperties(textStyle = LocalTextStyle.current),
    gridProperties: GridProperties = GridProperties(),
    animationMode: AnimationMode = AnimationMode.Together(),
    animationSpec: AnimationSpec<Float> = snap(),
    animationDelay: Long = 200,
    hideLabelHelper: Boolean = false,
    textMeasurer: TextMeasurer = rememberTextMeasurer(),
    popupProperties: PopupProperties = PopupProperties(textStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = 12.sp)),
    barAlphaDecreaseOnPopup: Float = .4f,
) {

    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    val maxValue = data.maxOf { it.values.maxOf { it.value } }

    val everyDataHeight = with(density) {
        data.map { rowData ->
            rowData.values.map {
                (it.properties?.strokeWidth ?: barProperties.strokeWidth).toPx() + (it.properties?.spacing ?: barProperties.spacing).toPx()
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
        maxValue.split(maxValue / indicatorProperties.count)
    }
    val indicatorAreaHeight = remember {
        if (indicatorProperties.enabled){
            indicators.maxOf { textMeasurer.measure(indicatorProperties.contentBuilder(it)).size.height }
        }else{
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

    Column(modifier=modifier) {
        if (!hideLabelHelper){
            RCChartLabelHelper(data = data)
            Spacer(modifier = Modifier.height(24.dp))
        }
        Row(modifier=Modifier.fillMaxSize()) {
            if (indicatorProperties.enabled){
                Column(modifier= Modifier
                    .fillMaxHeight()
                    .padding(bottom = (indicatorAreaHeight/density.density).dp)
                    .padding(vertical = (((everyDataHeight) / data.count()) / density.density).dp)
                    , verticalArrangement = Arrangement.SpaceBetween) {
                    data.forEach {
                        Text(text = it.label,style = labelStyle)
                    }
                }
                Spacer(modifier = Modifier.width(22.dp))
            }
            Canvas(modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    if (!popupProperties.enabled) return@pointerInput
                    detectDragGestures { change, dragAmount ->
                        rectWithValue
                            .lastOrNull { it.second.contains(change.position) }
                            ?.let {
                                selectedValue.value = SelectedBar(
                                    value = it.first,
                                    rect = it.second,
                                    offset = Offset(
                                        x = it.second.right,
                                        y = it.second.top
                                    )
                                )
                                scope.launch {
                                    if (popupAnimation.value != 1f) {
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
                            ?.let {
                                selectedValue.value = SelectedBar(
                                    value = it.first,
                                    rect = it.second,
                                    offset = Offset(
                                        x = it.second.right,
                                        y = it.second.top
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

                drawGridLines(
                    count = indicatorProperties.count,
                    color = gridProperties.color,
                    strokeWidth = gridProperties.strokeWidth,
                    size = size.copy(height = barAreaHeight, width = barAreaWidth),
                    justDividers = !gridProperties.enabled,
                    style = gridProperties.style
                )
                data.forEachIndexed { dataIndex, bars ->
                    bars.values.forEachIndexed { barIndex, bar ->

                        val stroke = (bar.properties?.strokeWidth ?: barProperties.strokeWidth).toPx()
                        val spacing = (bar.properties?.spacing ?: barProperties.spacing).toPx()
                        val width = ((barAreaWidth * bar.value) / maxValue) * bar.animator.value

                        val everyBarHeight = (stroke + spacing)

                        val rect = Rect(
                            offset = Offset(
                                x = 0f,
                                y = (everyBarHeight * barIndex) + (barAreaHeight - everyDataHeight).spaceBetween(
                                    itemCount = data.count(),
                                    index = dataIndex
                                )
                            ),
                            size = Size(height = stroke, width = width.toFloat())
                        )

                        val path = Path()

                        if (rectWithValue.none { it.second == rect }) rectWithValue.add(bar.value to rect)
                        path.addRoundRect(rect = rect, radius = (bar.properties?.radius ?: barProperties.radius))

                        val alpha = if (rect == selectedValue.value?.rect) {
                            1f - (barAlphaDecreaseOnPopup * popupAnimation.value)
                        } else {
                            1f
                        }
                        drawPath(
                            path = path,
                            brush = bar.color,
                            alpha = alpha,
                            style = (bar.properties?.style ?: barProperties.style).getStyle(density.density)
                        )
                    }
                }
                indicators.reversed().forEachIndexed { index, indicator ->
                    val measureResult =
                        textMeasurer.measure(indicatorProperties.contentBuilder(indicator), style = indicatorProperties.textStyle)
                    drawText(
                        textLayoutResult = measureResult,
                        topLeft = Offset(
                            x = (barAreaWidth - measureResult.size.width).spaceBetween(
                                itemCount = indicators.count(),
                                index = index
                            ), y = size.height - indicatorAreaHeight/2
                        )
                    )
                }


                if (selectedValue.value != null) {
                    val measure = textMeasurer.measure(
                        popupProperties.contentBuilder(selectedValue.value!!.value),
                        style = popupProperties.textStyle.copy(color = popupProperties.textStyle.color.copy(alpha = popupAnimation.value * 1f))
                    )
                    val rectSize = measure.size.toSize()
                    val rectPosition = selectedValue.value!!.offset.copy(
                        y = selectedValue.value!!.offset.y - (rectSize.height/1.5f),
                        x = selectedValue.value!!.offset.x - rectSize.width
                    )
                    val cornerRadius =
                        CornerRadius(popupProperties.cornerRadius.toPx(), popupProperties.cornerRadius.toPx())
                    drawPath(
                        path = Path().apply {
                            addRoundRect(
                                RoundRect(
                                    rect = Rect(
                                        offset = rectPosition,
                                        size = rectSize.copy(
                                            width = (rectSize.width + (popupProperties.contentHorizontalPadding.toPx() * 2)) * popupAnimation.value,
                                            height = rectSize.height + popupProperties.contentVerticalPadding.toPx() * 2
                                        ),
                                    ),
                                    topRight = cornerRadius,
                                    topLeft = cornerRadius,
                                    bottomRight = cornerRadius,
                                )
                            )
                        },
                        color = popupProperties.containerColor
                    )
                    drawText(
                        textLayoutResult = measure,
                        topLeft = rectPosition.copy(
                            x = rectPosition.x + popupProperties.contentHorizontalPadding.toPx(),
                            y = rectPosition.y + popupProperties.contentVerticalPadding.toPx()
                        ),
                    )
                }
            }
        }
    }
}

