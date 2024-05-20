package ir.ehsannarmani.compose_charts

import android.graphics.LinearGradient
import android.graphics.Shader
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import ir.ehsannarmani.compose_charts.components.LabelHelper
import ir.ehsannarmani.compose_charts.extensions.drawGridLines
import ir.ehsannarmani.compose_charts.extensions.split
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


data class Line(
    val label: String,
    val values: List<Double>,
    val color: Color,
    val firstGradientFillColor: Color? = null,
    val secondGradientFillColor: Color? = null,
    val drawStyle: DrawStyle = DrawStyle.Stroke(2.dp),
    val strokeAnimationSpec: AnimationSpec<Float> = tween(2000),
    val gradientAnimationSpec: AnimationSpec<Float> = tween(2000),
    val gradientAnimationDelay: Long = 1000,
    val strokeProgress: Animatable<Float, AnimationVector1D> = Animatable(0f),
    val gradientProgress: Animatable<Float, AnimationVector1D> = Animatable(0f),
) {
    sealed class DrawStyle() {
        data class Stroke(val width: Dp = 2.dp) : DrawStyle()
        data object Fill : DrawStyle()
    }

    sealed class AnimationMode {
        data class Together(val delayBuilder: (index: Int) -> Long = { 0 }) : AnimationMode()
        data object OneByOne : AnimationMode()
    }
}

private data class PathOffset(
    val offset:List<Pair<Int,Offset>>,
    val line: Line,
)
@Composable
fun LineChart(
    modifier: Modifier = Modifier,
    data: List<Line>,
    animationDelay: Long = 300,
    animationMode: Line.AnimationMode = Line.AnimationMode.Together(),
    drawGrid: Boolean = false,
    drawDividers:Boolean = true,
    gridColor: Color = Color.Gray,
    gridStroke: Dp = (.5).dp,
    gridLineCount: Int = 5,
    indicatorStyle: TextStyle = LocalTextStyle.current,
    showIndicators:Boolean = true,
    hideLabelHelper: Boolean = false,
    labelHelperPadding:Dp = 26.dp,
    indicatorBuilder: (Double) -> String = {
        "%.1f".format(it)
    },
    indicatorCount: Int = 4,
    textMeasurer: TextMeasurer = rememberTextMeasurer(),
    popupEnabled:Boolean = true,
    popupTextStyle: TextStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = 12.sp),
    popupBackgroundColor: Color = Color(0xff313131),
    popupCornerRadius: Dp = 7.dp,
    popupContentHorizontalPadding: Dp = 4.dp,
    popupContentVerticalPadding: Dp = 2.dp,
    popupContentBuilder:(value:Double)->String = {
        "%.1f".format(it)
    }
) {

    val scope = rememberCoroutineScope()

    val pathMeasure = remember {
        PathMeasure()
    }

    val pathsOffsets = remember {
        mutableStateListOf<PathOffset>()
    }

    val popupAnimation = remember{
        Animatable(0f)
    }

    val rectOffsets = remember {
        mutableStateListOf<Pair<Animatable<Float,AnimationVector1D>,Animatable<Float,AnimationVector1D>>>()
    }

    val maxValue = data.maxOf { it.values.maxOf { it } }

    LaunchedEffect(data) {
        pathsOffsets.clear()
    }

    LaunchedEffect(data) {
        delay(animationDelay)

        val animateStroke: suspend (Line) -> Unit = { line ->
            line.strokeProgress.animateTo(1f, animationSpec = line.strokeAnimationSpec)
        }
        val animateGradient: suspend (Line) -> Unit = { line ->
            delay(line.gradientAnimationDelay)
            line.gradientProgress.animateTo(1f, animationSpec = line.gradientAnimationSpec)
        }
        launch {
            data.forEachIndexed { index, line ->
                when (animationMode) {
                    is Line.AnimationMode.OneByOne -> {
                        animateStroke(line)
                    }

                    is Line.AnimationMode.Together -> {
                        launch {
                            delay(animationMode.delayBuilder(index))
                            animateStroke(line)
                        }
                    }
                }
            }
        }
        launch {
            data.forEachIndexed { index, line ->
                when (animationMode) {
                    is Line.AnimationMode.OneByOne -> {
                        animateGradient(line)
                    }

                    is Line.AnimationMode.Together -> {
                        launch {
                            delay(animationMode.delayBuilder(index))
                            animateGradient(line)
                        }
                    }
                }
            }
        }
    }

    val ballPositions = remember {
        mutableStateListOf<Offset>()
    }

    Column(modifier=modifier) {
        if(!hideLabelHelper){
            LabelHelper(data = data.map { it.label to it.color })
            Spacer(modifier = Modifier.height(labelHelperPadding))
        }
        Row(modifier=Modifier.fillMaxSize()) {
            if (showIndicators){
                Column(modifier=Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween) {
                    (maxValue).split(maxValue/indicatorCount).forEach {
                        Text(text = indicatorBuilder(it), style = indicatorStyle)
                    }
                }
                Spacer(modifier = Modifier.width(18.dp))
            }
            Canvas(modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    if (!popupEnabled) return@pointerInput
                    detectDragGestures(
                        onDragEnd = {
                            scope.launch {
                                popupAnimation.animateTo(0f, animationSpec = tween(500))
                                ballPositions.clear()
                                rectOffsets.clear()
                            }
                        },
                        onDrag = { change, amount ->
                            ballPositions.clear()
                            pathsOffsets
                                .map {
                                    it.offset.find {
                                        it.second.x in change.position.x - 5..change.position.x + 5
                                    }
                                }
                                .also {
                                    if (rectOffsets.count() < it.count()) {
                                        repeat(it.count()) {
                                            rectOffsets.add(Animatable(0f) to Animatable(0f))
                                        }
                                    }
                                    ballPositions.addAll(
                                        it
                                            .filter { it?.second != null }
                                            .map { it!!.second })
                                }

                            scope.launch {
                                if (popupAnimation.value != 1f && !popupAnimation.isRunning) {
                                    popupAnimation.animateTo(1f, animationSpec = tween(500))
                                }
                            }
                        }
                    )
                }) {

                if (drawDividers){
                    drawGridLines(
                        count = gridLineCount,
                        color = gridColor,
                        strokeWidth = gridStroke,
                        justDividers = !drawGrid,
                    )
                }
                data.forEachIndexed { index, line ->
                    val path = getLinePath(
                        dataPoints = line.values.map { it.toFloat() },
                        maxValue = maxValue.toFloat(),
                    )
                    if (pathsOffsets.none { it.line == line }){
                        pathsOffsets.add(
                            PathOffset(offset = path.flatten(),line = line)
                        )
                    }
                    val segmentedPath = Path()
                    pathMeasure.setPath(path, false)
                    pathMeasure.getSegment(
                        0f,
                        pathMeasure.length * line.strokeProgress.value,
                        segmentedPath
                    )

                    val stroke: Float = when (val drawStyle = line.drawStyle) {
                        is Line.DrawStyle.Fill -> {
                            0f
                        }

                        is Line.DrawStyle.Stroke -> {
                            drawStyle.width.toPx()
                        }
                    }
                    drawPath(
                        path = segmentedPath,
                        color = line.color,
                        style = Stroke(width = stroke)
                    )
                    if (line.firstGradientFillColor != null && line.secondGradientFillColor != null) {
                        drawGradient(
                            path = path,
                            color1 = line.firstGradientFillColor,
                            color2 = line.secondGradientFillColor,
                            progress = line.gradientProgress.value
                        )
                    } else if (line.drawStyle is Line.DrawStyle.Fill) {
                        drawGradient(
                            path = path,
                            color1 = line.color,
                            color2 = line.color,
                            progress = 1f
                        )
                    }
                    ballPositions.forEachIndexed { index, offset ->
                        val value = maxValue - ((offset.y*maxValue)/size.height)
                        val measureResult = textMeasurer.measure(popupContentBuilder(value), style = popupTextStyle.copy(color = popupTextStyle.color.copy(alpha = 1f*popupAnimation.value)))
                        var rectSize = measureResult.size.toSize()
                        rectSize = rectSize.copy(
                            width = (rectSize.width+(popupContentHorizontalPadding.toPx()*2)),
                            height = (rectSize.height+(popupContentVerticalPadding.toPx()*2))
                        )
                        val nextItem = ballPositions.getOrNull(index+1)
                        val conflictDetected = (nextItem != null )&& offset.y in nextItem.y-rectSize.height..nextItem.y+rectSize.height
                        val rectOffset = if (conflictDetected){
                            offset.copy(x = offset.x-rectSize.width)
                        }else{
                            offset
                        }
                        val animatedOffsetPair = rectOffsets.getOrNull(index)
                        animatedOffsetPair?.also { (x,y)->
                            if (x.value == 0f || y.value == 0f){
                                scope.launch {
                                    x.snapTo(rectOffset.x)
                                    y.snapTo(rectOffset.y)
                                }
                            }else{
                                scope.launch {
                                    x.animateTo(rectOffset.x)
                                }
                                scope.launch {
                                    y.animateTo(rectOffset.y)
                                }
                            }

                        }
                        if (animatedOffsetPair != null){
                            val animatedOffset = Offset(x = rectOffsets[index].first.value,y = rectOffsets[index].second.value)
                            val rect = Rect(
                                offset = animatedOffset,
                                size = rectSize
                            )
                            drawPath(
                                path = Path().apply {
                                    addRoundRect(RoundRect(
                                        rect = rect.copy(
                                            top = rect.top+stroke,
                                            left = rect.left+stroke,
                                        ),
                                        topLeft = CornerRadius(if (conflictDetected) popupCornerRadius.toPx() else 0f,if (conflictDetected) popupCornerRadius.toPx() else 0f),
                                        topRight = CornerRadius(if (!conflictDetected) popupCornerRadius.toPx() else 0f,if (!conflictDetected) popupCornerRadius.toPx() else 0f),
                                        bottomRight = CornerRadius(popupCornerRadius.toPx(),popupCornerRadius.toPx()),
                                        bottomLeft = CornerRadius(popupCornerRadius.toPx(),popupCornerRadius.toPx()),
                                    ))
                                },
                                color = popupBackgroundColor,
                                alpha = 1f*popupAnimation.value
                            )
                            drawText(
                                textLayoutResult = measureResult,
                                topLeft = animatedOffset.copy(
                                    x = animatedOffset.x + popupContentHorizontalPadding.toPx(),
                                    y = animatedOffset.y + popupContentVerticalPadding.toPx()
                                )
                            )
                        }


                    }
                }
            }

        }
    }
}

fun DrawScope.getLinePath(dataPoints: List<Float>,maxValue:Float): Path {

    val path = Path()

    val calculateHeight = { value: Float ->
        (size.height * value) / maxValue
    }

    path.moveTo(0f, size.height - calculateHeight(dataPoints[0]))

    for (i in 0 until dataPoints.size - 1) {
        val x1 = (i * (size.width / (dataPoints.size - 1)))
        val y1 = size.height - calculateHeight(dataPoints[i])
        val x2 = ((i + 1) * (size.width / (dataPoints.size - 1)))
        val y2 = size.height - calculateHeight(dataPoints[i + 1])

        val cx = (x1 + x2) / 2
        path.cubicTo(cx, y1, cx, y2, x2, y2)
    }

    return path
}

fun DrawScope.drawGradient(
    path: Path,
    color1: Color,
    color2: Color,
    progress: Float,
) {
    drawIntoCanvas {
        it.nativeCanvas.drawPath(android.graphics.Path().apply {
            addPath(path.asAndroidPath())
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }, android.graphics.Paint().apply {
            setShader(
                LinearGradient(
                    0f,
                    0f,
                    0f,
                    size.height,
                    color1.copy(alpha = color1.alpha * progress).toArgb(),
                    color2.toArgb(),
                    Shader.TileMode.MIRROR
                )
            )
        })
    }
}

fun Path.flatten(): MutableList<Pair<Int, Offset>> {
    val measure = PathMeasure()
    measure.setPath(this,false)
    val result = mutableListOf<Pair<Int,Offset>>()
    for (i in 0 until measure.length.toInt()){
        result.add(i to measure.getPosition(i.toFloat()))
    }
    return result
}