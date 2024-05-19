package ir.ehsannarmani.compose_charts

import android.view.MotionEvent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.graphics.BlendMode
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

data class ColumnChart(
    val label: String,
    val values: List<Data>
) {
    data class Data(
        val label: String? = null,
        val value: Double,
        val color: Color,
        val barWidth: Dp? = null,
        val barSpacing: Dp? = null,
        val barRadius: Radius? = null,
        val animationSpec:AnimationSpec<Float>? = null,
        val animator:Animatable<Float,AnimationVector1D> = Animatable(0f)
    ) {
        sealed class Radius() {
            data object None : Radius()
            data class Circular(val radius: Dp) : Radius()
            data class Rectangle(
                val topLeft: Dp = 0.dp,
                val topRight: Dp = 0.dp,
                val bottomLeft: Dp = 0.dp,
                val bottomRight: Dp = 0.dp
            ) : Radius()
        }
    }
    sealed class AnimationMode{
        data class Together(val delayBuilder:(index:Int)->Long = {0}):AnimationMode()
        data object OneByOne:AnimationMode()
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ColumnChart(
    modifier: Modifier = Modifier,
    data: List<ColumnChart>,
    barsWidth: Dp = 15.dp,
    barsSpacing: Dp = 6.dp,
    barsRadius: ColumnChart.Data.Radius = ColumnChart.Data.Radius.None,
    indicatorStyle: TextStyle = LocalTextStyle.current,
    labelStyle: TextStyle = LocalTextStyle.current,
    indicatorBuilder:(Double)->String = {
        "%.1f".format(it)
    },
    indicatorCount:Int = 4,
    dividerColor:Color= Color.Gray,
    drawGrid:Boolean = false,
    gridColor:Color = dividerColor,
    gridStroke:Dp = (.5).dp,
    animationMode:ColumnChart.AnimationMode = ColumnChart.AnimationMode.Together(),
    animationSpec:AnimationSpec<Float> = snap(),
    animationDelay:Long = 200,
    hideLabelHelper:Boolean = false,
    textMeasurer: TextMeasurer = rememberTextMeasurer(),
    popupTextStyle: TextStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = 12.sp),
    popupBackgroundColor:Color = Color(0xff313131),
    popupCornerRadius: Dp = 4.dp,
    popupContentHorizontalPadding:Dp = 4.dp,
    popupContentVerticalPadding:Dp = 2.dp,
) {

    val density = LocalDensity.current

    val maxValue = data.maxOf { it.values.maxOf { it.value } }

    val indicatorPanelSize = remember{
        mutableStateOf(Size.Zero)
    }
    val everyDataWidth = with(density){
        data.map { rowData ->
            rowData.values.map {
                (it.barWidth ?: barsWidth).toPx() + (it.barSpacing ?: barsSpacing).toPx()
            }.sum()
        }.average().toFloat()
    }

    val rectWithValue = remember{
        mutableStateListOf<Pair<Double,Rect>>()
    }

    val selectedValue = remember {
        mutableStateOf<Pair<Offset,Double>?>(null)
    }

    val popupAnimation = remember{
        Animatable(0f)
    }

    LaunchedEffect(selectedValue.value) {
        if (selectedValue.value != null){
            delay(1000)
            popupAnimation.animateTo(0f,animationSpec = tween(500))
            selectedValue.value = null
        }
    }

    LaunchedEffect(data) {
        rectWithValue.clear()
        delay(animationDelay)
        data.forEachIndexed { colIndex, columnChart ->
            columnChart.values.forEachIndexed { dataIndex, data ->
                val animate :suspend ()->Unit = {data.animator.animateTo(1f, animationSpec = data.animationSpec ?: animationSpec)}
                when(animationMode){
                    is ColumnChart.AnimationMode.OneByOne->{
                        animate()
                    }
                    is ColumnChart.AnimationMode.Together->{
                        launch {
                            delay(animationMode.delayBuilder((colIndex*columnChart.values.count())+dataIndex))
                            animate()
                        }
                    }
                }
            }
        }
    }

    Column(modifier=modifier) {
       if (!hideLabelHelper){
           LazyVerticalGrid(columns = GridCells.Fixed(3),modifier=Modifier) {
               items(data.map { it.values.map { it.label } }.flatten().toSet().toList()){label->
                   val color = data.map { it.values.find { it.label == label }?.color }.firstOrNull()
                   color?.let {
                       Row(modifier= Modifier
                           .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                           Box(modifier = Modifier
                               .size(10.dp)
                               .clip(CircleShape)
                               .background(color))
                           Text(text = label.orEmpty(), fontSize = 13.sp)
                       }
                   }
               }
           }
           Spacer(modifier = Modifier.height(28.dp))
       }
        Row(modifier= Modifier
            .fillMaxSize()
            .weight(1f)) {
            Column(modifier= Modifier
                .fillMaxHeight()
                .onSizeChanged { indicatorPanelSize.value = it.toSize() }, verticalArrangement = Arrangement.SpaceBetween) {
                maxValue.split(maxValue/indicatorCount).forEach {
                    Text(text = indicatorBuilder(it), style = indicatorStyle)
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
            val scope = rememberCoroutineScope()
            Canvas(modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        rectWithValue
                            .find { it.second.contains(change.position) }
                            ?.let {
                                println("found: ${it.first}")
                                selectedValue.value = change.position to it.first
                                scope.launch {
                                    if (!popupAnimation.isRunning) {
                                        popupAnimation.animateTo(1f, animationSpec = tween(500))
                                    }
                                }
                            }
                    }
                }
                .pointerInteropFilter { event ->
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        val position = Offset(event.x, event.y)
                        rectWithValue
                            .find { it.second.contains(position) }
                            ?.let {
                                selectedValue.value = position to it.first
                                scope.launch {
                                    if (!popupAnimation.isRunning) {
                                        popupAnimation.animateTo(1f, animationSpec = tween(500))
                                    }
                                }
                            }
                    }
                    false
                }) {
                if (drawGrid){
                    for (i in 0 until indicatorCount){
                        val everyHeight = size.height/indicatorCount
                        val everyWidth = (size.width+everyDataWidth)/indicatorCount
                        drawLine(
                            color = gridColor,
                            start = Offset(0f,everyHeight*i),
                            end = Offset(size.width,everyHeight*i),
                            strokeWidth = gridStroke.toPx()
                        )
                        drawLine(
                            color = gridColor,
                            start = Offset(everyWidth*i,0f),
                            end = Offset(everyWidth*i,size.height),
                            strokeWidth = gridStroke.toPx()
                        )

                    }
                }
                data.forEachIndexed { dataIndex, columnChart ->
                    columnChart.values.forEachIndexed { valueIndex, col ->
                        val width = (col.barWidth ?: barsWidth).toPx()
                        val spacing = (col.barSpacing ?: barsSpacing).toPx()

                        val height = ((col.value * size.height) / maxValue)*col.animator.value
                        val rect = Rect(
                            offset = Offset(
                                x = (valueIndex * (width + spacing))+ calculateX(size.width-everyDataWidth,data.count(),dataIndex),
                                y = (size.height - height.toFloat())
                            ),
                            size = Size(width = width, height = height.toFloat()),
                        )
                        if (rectWithValue.none { it.second == rect }) rectWithValue.add(col.value to rect)
                        val path = Path()
                        when(val radius = (col.barRadius ?: barsRadius)){
                            is ColumnChart.Data.Radius.None->{
                                path.addRect(rect)
                            }
                            is ColumnChart.Data.Radius.Circular->{
                                path.addRoundRect(
                                    roundRect = RoundRect(
                                        rect = rect,
                                        cornerRadius = CornerRadius(x = radius.radius.toPx(),y = radius.radius.toPx())
                                    )
                                )
                            }
                            is ColumnChart.Data.Radius.Rectangle->{
                                path.addRoundRect(
                                    roundRect = RoundRect(
                                        rect = rect,
                                        topLeft = CornerRadius(x = radius.topLeft.toPx(),y = radius.topLeft.toPx()),
                                        topRight = CornerRadius(x = radius.topRight.toPx(),y = radius.topRight.toPx()),
                                        bottomLeft = CornerRadius(x = radius.bottomLeft.toPx(),y = radius.bottomLeft.toPx()),
                                        bottomRight = CornerRadius(x = radius.bottomRight.toPx(),y = radius.bottomRight.toPx()),
                                    )
                                )
                            }
                        }
                        drawPath(
                            path = path,
                            color = col.color
                        )
                    }
                }
                drawLine(
                    color = dividerColor,
                    start = Offset(0f,size.height),
                    end = Offset(size.width,size.height),
                    strokeWidth = 1f
                )

                if (selectedValue.value != null){
                    val measure = textMeasurer.measure(selectedValue.value!!.second.toString(), style = popupTextStyle.copy(color = popupTextStyle.color.copy(alpha = popupAnimation.value*1f)))
                    val rectSize = measure.size.toSize()
                    val rectPosition = selectedValue.value!!.first.copy(y = selectedValue.value!!.first.y-(rectSize.height*2))
                    val cornerRadius = CornerRadius(popupCornerRadius.toPx(),popupCornerRadius.toPx())
                    drawPath(
                        path = Path().apply {
                            addRoundRect(RoundRect(
                                rect = Rect(
                                    offset = rectPosition,
                                    size = rectSize.copy(width = (rectSize.width+(popupContentHorizontalPadding.toPx()*2))*popupAnimation.value, height = rectSize.height+popupContentVerticalPadding.toPx()*2),
                                ),
                                topRight = cornerRadius,
                                bottomLeft = cornerRadius,
                                bottomRight = cornerRadius,
                            ))
                        },
                        color = popupBackgroundColor
                    )
                    drawText(
                        textLayoutResult = measure,
                        topLeft = rectPosition.copy(x=rectPosition.x+popupContentHorizontalPadding.toPx(),y = rectPosition.y+popupContentVerticalPadding.toPx()),
                    )
                }

            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier= Modifier
            .fillMaxWidth()
            .padding(
                start = (indicatorPanelSize.value.width / density.density).dp + 22.dp,
                end = 8.dp
            ), horizontalArrangement = Arrangement.SpaceBetween) {
            data.forEach { 
                Text(text = it.label, style = labelStyle)
            }
        }

    }
}

fun Double.split(step:Double):List<Double>{
    var current = this
    val result = mutableListOf<Double>()
    while (current >= 0){
        result.add(current)
        current -= step
    }
    return result
}
fun calculateX(size:Float,itemCount:Int,index:Int): Float {
    val itemSize = size / (itemCount - 1)
    val positions = (0 until itemCount).map { it * itemSize }
    val result = positions[index]
    return result
}