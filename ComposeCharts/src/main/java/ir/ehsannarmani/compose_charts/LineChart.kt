package ir.ehsannarmani.compose_charts

import android.graphics.LinearGradient
import android.graphics.Shader
import androidx.compose.animation.core.Animatable
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
import ir.ehsannarmani.compose_charts.extensions.flatten
import ir.ehsannarmani.compose_charts.extensions.spaceBetween
import ir.ehsannarmani.compose_charts.extensions.split
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.IndicatorProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.PopupProperties
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class PathOffset(
    val offset: List<Pair<Int, Offset>>,
    val line: Line,
)

@Composable
fun LineChart(
    modifier: Modifier = Modifier,
    data: List<Line>,
    curvedEdges:Boolean = true,
    animationDelay: Long = 300,
    animationMode: Line.AnimationMode = Line.AnimationMode.Together(),
    gridProperties: GridProperties = GridProperties(lineCount = 5),
    indicatorProperties: IndicatorProperties = IndicatorProperties(textStyle = LocalTextStyle.current),
    drawDividers: Boolean = true,
    hideLabelHelper: Boolean = false,
    labelHelperPadding: Dp = 26.dp,
    textMeasurer: TextMeasurer = rememberTextMeasurer(),
    popupProperties: PopupProperties = PopupProperties(textStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = 12.sp)),
    showDots: Boolean = false,
    dotsRadius: Float = 10f,
    dotsBorderWidth: Float = 3f,
    dotsColor: Color = Color.Unspecified,
    dotsBorderColor: Color = Color.Unspecified,
) {

    val scope = rememberCoroutineScope()

    val pathMeasure = remember {
        PathMeasure()
    }

    val pathsOffsets = remember {
        mutableStateListOf<PathOffset>()
    }

    val popupAnimation = remember {
        Animatable(0f)
    }

    val rectOffsets = remember {
        mutableStateListOf<Pair<Animatable<Float, AnimationVector1D>, Animatable<Float, AnimationVector1D>>>()
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

    val popupPositions = remember {
        mutableStateListOf<Offset>()
    }


    Column(modifier = modifier) {
        if (!hideLabelHelper) {
            LabelHelper(data = data.map { it.label to it.color })
            Spacer(modifier = Modifier.height(labelHelperPadding))
        }
        Row(modifier = Modifier.fillMaxSize()) {
            if (indicatorProperties.enabled) {
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    (maxValue).split(maxValue / indicatorProperties.count).forEach {
                        Text(text = indicatorProperties.contentBuilder(it), style = indicatorProperties.textStyle)
                    }
                }
                Spacer(modifier = Modifier.width(18.dp))
            }
            Canvas(modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    if (!popupProperties.enabled) return@pointerInput

                    detectDragGestures(
                        onDragEnd = {
                            scope.launch {
                                popupAnimation.animateTo(0f, animationSpec = tween(500))
                                popupPositions.clear()
                                rectOffsets.clear()
                            }
                        },
                        onDrag = { change, amount ->
                            popupPositions.clear()
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
                                    popupPositions.addAll(
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

                if (drawDividers) {
                    drawGridLines(
                        count = gridProperties.lineCount,
                        color = gridProperties.color,
                        strokeWidth = gridProperties.strokeWidth,
                        justDividers = !gridProperties.enabled,
                        style = gridProperties.style
                    )
                }
                data.forEachIndexed { index, line ->
                    val path = getLinePath(
                        dataPoints = line.values.map { it.toFloat() },
                        maxValue = maxValue.toFloat(),
                        rounded =line.curvedEdges ?: curvedEdges
                    )
                    if (pathsOffsets.none { it.line == line }) {
                        pathsOffsets.add(
                            PathOffset(offset = path.flatten(), line = line)
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

                    if ((line.showDots ?: showDots)) {
                        line.values.forEachIndexed { valueIndex, value ->
                            drawCircle(
                                color = line.dotsBorderColor ?: dotsBorderColor,
                                radius = ((line.dotsRadius ?: dotsRadius) + (line.dotsBorderWidth
                                    ?: dotsBorderWidth)) ,
                                center = Offset(
                                    x = size.width.spaceBetween(
                                        itemCount = line.values.count(),
                                        index = valueIndex
                                    ),
                                    y = size.height - (size.height * value.toFloat() / maxValue).toFloat()
                                )
                            )
                            drawCircle(
                                color = line.dotsColor ?: dotsColor,
                                radius = (line.dotsRadius ?: dotsRadius),
                                center = Offset(
                                    x = size.width.spaceBetween(
                                        itemCount = line.values.count(),
                                        index = valueIndex
                                    ),
                                    y = size.height - (size.height * value.toFloat() / maxValue).toFloat()
                                )
                            )
                        }
                    }


                }
                popupPositions.forEachIndexed { index, offset ->
                    val value = maxValue - ((offset.y * maxValue) / size.height)
                    val measureResult = textMeasurer.measure(
                        popupProperties.contentBuilder(value),
                        style = popupProperties.textStyle.copy(color = popupProperties.textStyle.color.copy(alpha = 1f * popupAnimation.value))
                    )
                    var rectSize = measureResult.size.toSize()
                    rectSize = rectSize.copy(
                        width = (rectSize.width + (popupProperties.contentHorizontalPadding.toPx() * 2)),
                        height = (rectSize.height + (popupProperties.contentVerticalPadding.toPx() * 2))
                    )

                    val nextItem = popupPositions.getOrNull(index + 1)
                    val conflictDetected =
                        ((nextItem != null) && offset.y in nextItem.y - rectSize.height..nextItem.y + rectSize.height) ||
                                (offset.x+rectSize.width) > size.width


                    val rectOffset = if (conflictDetected) {
                        offset.copy(x = offset.x - rectSize.width)
                    } else {
                        offset
                    }
                    val animatedOffsetPair = rectOffsets.getOrNull(index)
                    animatedOffsetPair?.also { (x, y) ->
                        if (x.value == 0f || y.value == 0f) {
                            scope.launch {
                                x.snapTo(rectOffset.x)
                                y.snapTo(rectOffset.y)
                            }
                        } else {
                            scope.launch {
                                x.animateTo(rectOffset.x)
                            }
                            scope.launch {
                                y.animateTo(rectOffset.y)
                            }
                        }

                    }
                    if (animatedOffsetPair != null) {
                        val animatedOffset = Offset(
                            x = rectOffsets[index].first.value,
                            y = rectOffsets[index].second.value
                        )
                        val rect = Rect(
                            offset = animatedOffset,
                            size = rectSize
                        )
                        drawPath(
                            path = Path().apply {
                                addRoundRect(
                                    RoundRect(
                                        rect = rect.copy(
                                            top = rect.top,
                                            left = rect.left,
                                        ),
                                        topLeft = CornerRadius(
                                            if (conflictDetected) popupProperties.cornerRadius.toPx() else 0f,
                                            if (conflictDetected) popupProperties.cornerRadius.toPx() else 0f
                                        ),
                                        topRight = CornerRadius(
                                            if (!conflictDetected) popupProperties.cornerRadius.toPx() else 0f,
                                            if (!conflictDetected) popupProperties.cornerRadius.toPx() else 0f
                                        ),
                                        bottomRight = CornerRadius(
                                            popupProperties.cornerRadius.toPx(),
                                            popupProperties.cornerRadius.toPx()
                                        ),
                                        bottomLeft = CornerRadius(
                                            popupProperties.cornerRadius.toPx(),
                                            popupProperties.cornerRadius.toPx()
                                        ),
                                    )
                                )
                            },
                            color = popupProperties.containerColor,
                            alpha = 1f * popupAnimation.value
                        )
                        drawText(
                            textLayoutResult = measureResult,
                            topLeft = animatedOffset.copy(
                                x = animatedOffset.x + popupProperties.contentHorizontalPadding.toPx(),
                                y = animatedOffset.y + popupProperties.contentVerticalPadding.toPx()
                            )
                        )
                    }
                }

            }
        }
    }
}

fun DrawScope.getLinePath(dataPoints: List<Float>, maxValue: Float,rounded:Boolean = true): Path {

    val path = Path()

    val calculateHeight = { value: Float ->
        (size.height * value) / maxValue
    }

    path.moveTo(0f, size.height - calculateHeight(dataPoints[0]))

    if (rounded){
        for (i in 0 until dataPoints.size - 1) {
            val x1 = (i * (size.width / (dataPoints.size - 1)))
            val y1 = size.height - calculateHeight(dataPoints[i])
            val x2 = ((i + 1) * (size.width / (dataPoints.size - 1)))
            val y2 = size.height - calculateHeight(dataPoints[i + 1])

            val cx = (x1 + x2) / 2
            path.cubicTo(cx, y1, cx, y2, x2, y2)
        }
    }else{
        dataPoints.forEachIndexed { index, value ->
            path.lineTo(size.width.spaceBetween(itemCount = dataPoints.count(),index = index),y = size.height-calculateHeight(value))
        }
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

