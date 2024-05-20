package ir.ehsannarmani.compose_charts

import android.view.MotionEvent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import ir.ehsannarmani.compose_charts.components.RCChartLabelHelper
import ir.ehsannarmani.compose_charts.extensions.addRoundRect
import ir.ehsannarmani.compose_charts.extensions.drawGridLines
import ir.ehsannarmani.compose_charts.extensions.spaceBetween
import ir.ehsannarmani.compose_charts.extensions.split
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.SelectedBar
import ir.ehsannarmani.compose_charts.utils.ImplementRCAnimation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ColumnChart(
    modifier: Modifier = Modifier,
    data: List<Bars>,
    barsStroke: Dp = 15.dp,
    barsSpacing: Dp = 6.dp,
    barsRadius: Bars.Data.Radius = Bars.Data.Radius.None,
    indicatorStyle: TextStyle = LocalTextStyle.current,
    labelStyle: TextStyle = LocalTextStyle.current,
    indicatorBuilder: (Double) -> String = {
        "%.1f".format(it)
    },
    indicatorCount: Int = 4,
    dividerColor: Color = Color.Gray,
    drawGrid: Boolean = false,
    gridColor: Color = dividerColor,
    gridStroke: Dp = (.5).dp,
    animationMode: Bars.AnimationMode = Bars.AnimationMode.Together(),
    animationSpec: AnimationSpec<Float> = snap(),
    animationDelay: Long = 200,
    hideLabelHelper: Boolean = false,
    textMeasurer: TextMeasurer = rememberTextMeasurer(),
    popupEnabled: Boolean = true,
    popupAnimationSpec: AnimationSpec<Float> = tween(400),
    popupDuration: Long = 1500,
    popupTextStyle: TextStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = 12.sp),
    popupBackgroundColor: Color = Color(0xff313131),
    popupCornerRadius: Dp = 4.dp,
    popupContentHorizontalPadding: Dp = 4.dp,
    popupContentVerticalPadding: Dp = 2.dp,
    barAlphaDecreaseOnPopup: Float = .4f,
    popupContentBuilder:(value:Double)->String = {
        "%.1f".format(it)
    }
) {

    val density = LocalDensity.current

    val maxValue = data.maxOf { it.values.maxOf { it.value } }

    val everyDataWidth = with(density) {
        data.map { rowData ->
            rowData.values.map {
                (it.barStroke ?: barsStroke).toPx() + (it.barSpacing ?: barsSpacing).toPx()
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
        maxValue.split(maxValue / indicatorCount)
    }
    val indicatorAreaWidth = remember {
        indicators.maxOf { textMeasurer.measure(indicatorBuilder(it)).size.width } + (16*density.density)
    }

    LaunchedEffect(selectedValue.value) {
        if (selectedValue.value != null) {
            delay(popupDuration)
            popupAnimation.animateTo(0f, animationSpec = popupAnimationSpec)
            selectedValue.value = null
        }
    }

    ImplementRCAnimation(
        data = data,
        animationMode = animationMode,
        spec = {it.animationSpec ?: animationSpec},
        delay = animationDelay,
        before = {
            rectWithValue.clear()
        }
    )
    Column(modifier = modifier) {
        if (!hideLabelHelper) {
            RCChartLabelHelper(data = data)
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
                    if (!popupEnabled) return@pointerInput
                    detectDragGestures { change, dragAmount ->
                        rectWithValue
                            .lastOrNull { it.second.contains(change.position) }
                            ?.let {
                                println("found: ${it.first}")
                                selectedValue.value = SelectedBar(
                                    value = it.first,
                                    rect = it.second,
                                    offset = Offset(it.second.left, it.second.top)
                                )
                                scope.launch {
                                    if (popupAnimation.value != 1f) {
                                        popupAnimation.animateTo(
                                            1f,
                                            animationSpec = popupAnimationSpec
                                        )
                                    }
                                }
                            }
                    }
                }
                .pointerInteropFilter { event ->
                    if (event.action == MotionEvent.ACTION_DOWN && popupEnabled) {
                        val position = Offset(event.x, event.y)
                        rectWithValue
                            .lastOrNull { it.second.contains(position) }
                            ?.let {
                                selectedValue.value = SelectedBar(
                                    value = it.first,
                                    rect = it.second,
                                    offset = Offset(it.second.left, it.second.top)
                                )
                                scope.launch {
                                    popupAnimation.snapTo(0f)
                                    popupAnimation.animateTo(
                                        1f,
                                        animationSpec = popupAnimationSpec
                                    )
                                }
                            }
                    }
                    false
                }) {

                val barsAreaWidth = size.width-(indicatorAreaWidth)


                indicators.forEachIndexed { index, indicator ->
                    val measureResult = textMeasurer.measure(indicatorBuilder(indicator),style = indicatorStyle)
                    drawText(
                        textLayoutResult = measureResult,
                        topLeft = Offset(x = 0f,y = (size.height - measureResult.size.height).spaceBetween(itemCount = indicators.count(),index))
                    )
                }

                drawGridLines(count = indicatorCount,color = gridColor, strokeWidth = gridStroke, xPadding = indicatorAreaWidth.toFloat(),size = size.copy(width = barsAreaWidth), justDividers = !drawGrid)

                data.forEachIndexed { dataIndex, columnChart ->
                    columnChart.values.forEachIndexed { valueIndex, col ->
                        val stroke = (col.barStroke ?: barsStroke).toPx()
                        val spacing = (col.barSpacing ?: barsSpacing).toPx()

                        val barHeight = ((col.value * size.height) / maxValue) * col.animator.value
                        val everyBarWidth = (stroke + spacing)

                        val rect = Rect(
                            offset = Offset(
                                x = (valueIndex * everyBarWidth) + (barsAreaWidth - everyDataWidth).spaceBetween(
                                    itemCount = data.count(),
                                    index = dataIndex
                                )+indicatorAreaWidth,
                                y = (size.height - barHeight.toFloat())
                            ),
                            size = Size(width = stroke, height = barHeight.toFloat()),
                        )
                        if (rectWithValue.none { it.second == rect }) rectWithValue.add(col.value to rect)
                        val path = Path()
                        path.addRoundRect(rect = rect, radius = (col.barRadius ?: barsRadius))
                        val color = if (rect == selectedValue.value?.rect) {
                            col.color.copy(alpha = 1f - (barAlphaDecreaseOnPopup * popupAnimation.value))
                        } else {
                            col.color
                        }
                        drawPath(
                            path = path,
                            color = color
                        )
                    }
                }

                if (selectedValue.value != null) {
                    val measure = textMeasurer.measure(
                        popupContentBuilder(selectedValue.value!!.value),
                        style = popupTextStyle.copy(color = popupTextStyle.color.copy(alpha = popupAnimation.value * 1f))
                    )
                    val rectSize = measure.size.toSize()
                    val rectPosition = selectedValue.value!!.offset.copy(
                        y = selectedValue.value!!.offset.y - (rectSize.height),
                        x = selectedValue.value!!.offset.x + rectSize.width / 3
                    )
                    val cornerRadius =
                        CornerRadius(popupCornerRadius.toPx(), popupCornerRadius.toPx())
                    drawPath(
                        path = Path().apply {
                            addRoundRect(
                                RoundRect(
                                    rect = Rect(
                                        offset = rectPosition,
                                        size = rectSize.copy(
                                            width = (rectSize.width + (popupContentHorizontalPadding.toPx() * 2)) * popupAnimation.value,
                                            height = rectSize.height + popupContentVerticalPadding.toPx() * 2
                                        ),
                                    ),
                                    topRight = cornerRadius,
                                    topLeft = cornerRadius,
                                    bottomRight = cornerRadius,
                                )
                            )
                        },
                        color = popupBackgroundColor
                    )
                    drawText(
                        textLayoutResult = measure,
                        topLeft = rectPosition.copy(
                            x = rectPosition.x + popupContentHorizontalPadding.toPx(),
                            y = rectPosition.y + popupContentVerticalPadding.toPx()
                        ),
                    )
                }

            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = (indicatorAreaWidth/density.density).dp,
                ), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            data.forEach {
                val measureResult = textMeasurer.measure(it.label, style = labelStyle)
                Text(text = it.label, style = labelStyle,modifier=Modifier.padding(end = ((measureResult.size.width/2)/density.density).dp))
            }
        }

    }
}
