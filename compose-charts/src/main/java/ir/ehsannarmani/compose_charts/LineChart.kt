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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
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
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.DividerProperties
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.IndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.PopupProperties
import ir.ehsannarmani.compose_charts.models.ZeroLineProperties
import kotlinx.coroutines.CoroutineScope
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
    curvedEdges: Boolean = true,
    animationDelay: Long = 300,
    animationMode: AnimationMode = AnimationMode.Together(),
    dividerProperties: DividerProperties = DividerProperties(),
    gridProperties: GridProperties = GridProperties(),
    zeroLineProperties: ZeroLineProperties = ZeroLineProperties(),
    indicatorProperties: IndicatorProperties = IndicatorProperties(textStyle = TextStyle.Default),
    labelHelperProperties: LabelHelperProperties = LabelHelperProperties(),
    labelHelperPadding: Dp = 26.dp,
    textMeasurer: TextMeasurer = rememberTextMeasurer(),
    popupProperties: PopupProperties = PopupProperties(
        textStyle = TextStyle.Default.copy(
            color = Color.White,
            fontSize = 12.sp
        )
    ),
    dotsProperties: DotProperties = DotProperties(),
    labelProperties: LabelProperties = LabelProperties(enabled = false),
    maxValue: Double = data.maxOfOrNull { it.values.maxOfOrNull { it } ?: 0.0 } ?: 0.0,
    minValue: Double = if (data.any { it.values.any { it < 0.0 } }) data.minOfOrNull {
        it.values.minOfOrNull { it } ?: 0.0
    } ?: 0.0 else 0.0,
) {

    require(data.isNotEmpty()) {
        "Chart data is empty"
    }
    require(minValue <= data.minOf { it.values.minOf { it } }){
        "Chart data must be at least $minValue (Specified Min Value)"
    }
    require(maxValue >= data.maxOf { it.values.maxOf { it } }){
        "Chart data must be at most $maxValue (Specified Max Value)"
    }

    val density = LocalDensity.current
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

    val zeroLineAnimation = remember {
        Animatable(0f)
    }

    val rectOffsets = remember {
        mutableStateListOf<Pair<Animatable<Float, AnimationVector1D>, Animatable<Float, AnimationVector1D>>>()
    }
    val labelAreaHeight = remember {
        if (labelProperties.enabled) {
            labelProperties.labels.maxOf {
                textMeasurer.measure(
                    it,
                    style = labelProperties.textStyle
                ).size.height
            } + (labelProperties.padding.value * density.density).toInt()
        } else {
            0
        }
    }

    val dotAnimators = remember {
        mutableStateListOf<List<Animatable<Float, AnimationVector1D>>>()
    }

    LaunchedEffect(data) {
        pathsOffsets.clear()
        dotAnimators.clear()
        if (zeroLineProperties.enabled) {
            zeroLineAnimation.snapTo(0f)
        }
        launch {
            data.forEach {
                val animators = mutableListOf<Animatable<Float, AnimationVector1D>>()
                it.values.forEach {
                    animators.add(Animatable(0f))
                }
                dotAnimators.add(animators)
            }
        }
        if (zeroLineProperties.enabled) {
            zeroLineAnimation.animateTo(1f, animationSpec = zeroLineProperties.animationSpec)
        }
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
                    is AnimationMode.OneByOne -> {
                        animateStroke(line)
                    }

                    is AnimationMode.Together -> {
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
                    is AnimationMode.OneByOne -> {
                        animateGradient(line)
                    }

                    is AnimationMode.Together -> {
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
        if (labelHelperProperties.enabled) {
            LabelHelper(
                data = data.map { it.label to it.color },
                textStyle = labelHelperProperties.textStyle
            )
            Spacer(modifier = Modifier.height(labelHelperPadding))
        }
        Row(modifier = Modifier.fillMaxSize()) {
            if (indicatorProperties.enabled) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(bottom = (labelAreaHeight / density.density).dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    (maxValue).split(
                        step = (maxValue - minValue) / indicatorProperties.count,
                        minValue = minValue
                    ).forEach {
                        BasicText(
                            text = indicatorProperties.contentBuilder(it),
                            style = indicatorProperties.textStyle
                        )
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
                val chartAreaHeight = size.height - labelAreaHeight

                val drawZeroLine = {
                    val zeroY = chartAreaHeight - (0.0f).heightFor(
                        minValue = minValue.toFloat(),
                        maxValue = maxValue.toFloat(),
                        totalHeight = chartAreaHeight
                    )
                    drawLine(
                        brush = zeroLineProperties.color,
                        start = Offset(x = 0f, y = zeroY),
                        end = Offset(x = size.width * zeroLineAnimation.value, y = zeroY),
                        pathEffect = zeroLineProperties.style.pathEffect,
                        strokeWidth = zeroLineProperties.thickness.toPx()
                    )
                }

                if (labelProperties.enabled) {
                    labelProperties.labels.forEachIndexed { index, label ->
                        val measureResult =
                            textMeasurer.measure(label, style = labelProperties.textStyle)
                        drawText(
                            textLayoutResult = measureResult,
                            topLeft = Offset(
                                (size.width - measureResult.size.width).spaceBetween(
                                    itemCount = labelProperties.labels.count(),
                                    index = index
                                ),
                                size.height - labelAreaHeight + labelProperties.padding.toPx()
                            )
                        )
                    }
                }

                drawGridLines(
                    dividersProperties = dividerProperties,
                    xAxisProperties = gridProperties.xAxisProperties,
                    yAxisProperties = gridProperties.yAxisProperties,
                    size = size.copy(height = chartAreaHeight),
                    gridEnabled = gridProperties.enabled
                )
                if (zeroLineProperties.enabled && zeroLineProperties.zType == ZeroLineProperties.ZType.Under) {
                    drawZeroLine()
                }
                data.forEachIndexed { index, line ->
                    val path = getLinePath(
                        dataPoints = line.values.map { it.toFloat() },
                        maxValue = maxValue.toFloat(),
                        minValue = minValue.toFloat(),
                        rounded = line.curvedEdges ?: curvedEdges,
                        size = size.copy(height = chartAreaHeight)
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

                    var pathEffect: PathEffect? = null
                    val stroke: Float = when (val drawStyle = line.drawStyle) {
                        is DrawStyle.Fill -> {
                            0f
                        }

                        is DrawStyle.Stroke -> {
                            pathEffect = drawStyle.strokeStyle.pathEffect
                            drawStyle.width.toPx()
                        }
                    }
                    drawPath(
                        path = segmentedPath,
                        brush = line.color,
                        style = Stroke(width = stroke, pathEffect = pathEffect)
                    )
                    if (line.firstGradientFillColor != null && line.secondGradientFillColor != null) {
                        drawGradient(
                            path = path,
                            color1 = line.firstGradientFillColor,
                            color2 = line.secondGradientFillColor,
                            progress = line.gradientProgress.value,
                            size = size.copy(height = chartAreaHeight)
                        )
                    } else if (line.drawStyle is DrawStyle.Fill) {
                        var fillColor = Color.Unspecified
                        if (line.color is SolidColor) {
                            fillColor = line.color.value
                        }
                        drawGradient(
                            path = path,
                            color1 = fillColor,
                            color2 = fillColor,
                            progress = 1f,
                            size = size.copy(height = chartAreaHeight)
                        )
                    }

                    if ((line.dotProperties?.enabled ?: dotsProperties.enabled)) {
                        drawDots(
                            dataPoints = line.values.mapIndexed { mapIndex, value ->
                                (dotAnimators.getOrNull(
                                    index
                                )?.getOrNull(mapIndex) ?: Animatable(0f)) to value.toFloat()
                            },
                            properties = line.dotProperties ?: dotsProperties,
                            linePath = segmentedPath,
                            maxValue = maxValue.toFloat(),
                            pathMeasure = pathMeasure,
                            scope = scope,
                            size = size.copy(height = chartAreaHeight)
                        )
                    }
                }
                if (zeroLineProperties.enabled && zeroLineProperties.zType == ZeroLineProperties.ZType.Above) {
                    drawZeroLine()
                }
                popupPositions.forEachIndexed { index, offset ->
                    val value = (maxValue + minValue) - offset.y.valueFor(
                        minValue = minValue,
                        maxValue = maxValue,
                        totalHeight = chartAreaHeight
                    )
                    val measureResult = textMeasurer.measure(
                        popupProperties.contentBuilder(value),
                        style = popupProperties.textStyle.copy(
                            color = popupProperties.textStyle.color.copy(
                                alpha = 1f * popupAnimation.value
                            )
                        )
                    )
                    var rectSize = measureResult.size.toSize()
                    rectSize = rectSize.copy(
                        width = (rectSize.width + (popupProperties.contentHorizontalPadding.toPx() * 2)),
                        height = (rectSize.height + (popupProperties.contentVerticalPadding.toPx() * 2))
                    )

                    val nextItem = popupPositions.getOrNull(index + 1)
                    val conflictDetected =
                        ((nextItem != null) && offset.y in nextItem.y - rectSize.height..nextItem.y + rectSize.height) ||
                                (offset.x + rectSize.width) > size.width


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

fun DrawScope.drawDots(
    dataPoints: List<Pair<Animatable<Float, AnimationVector1D>, Float>>,
    properties: DotProperties,
    linePath: Path,
    maxValue: Float,
    pathMeasure: PathMeasure,
    scope: CoroutineScope,
    size: Size? = null
) {
    val _size = size ?: this.size

    val pathEffect = properties.strokeStyle.pathEffect

    pathMeasure.setPath(linePath, false)
    val lastPosition = pathMeasure.getPosition(pathMeasure.length)
    dataPoints.forEachIndexed { valueIndex, value ->
        val dotOffset = Offset(
            x = _size.width.spaceBetween(
                itemCount = dataPoints.count(),
                index = valueIndex
            ),
            y = _size.height - (_size.height * value.second / maxValue)
        )
        if (lastPosition != Offset.Unspecified && lastPosition.x >= dotOffset.x - 20 || !properties.animationEnabled) {
            if (!value.first.isRunning && properties.animationEnabled) {
                scope.launch {
                    value.first.animateTo(1f, animationSpec = properties.animationSpec)
                }
            }

            val radius: Float
            val strokeRadius: Float
            if (properties.animationEnabled) {
                radius = (properties.radius + properties.strokeWidth / 2) * value.first.value
                strokeRadius = properties.radius * value.first.value
            } else {
                radius = properties.radius + properties.strokeWidth / 2
                strokeRadius = properties.radius
            }
            drawCircle(
                brush = properties.strokeColor,
                radius = radius,
                center = dotOffset,
                style = Stroke(width = properties.strokeWidth, pathEffect = pathEffect),
            )
            drawCircle(
                brush = properties.color,
                radius = strokeRadius,
                center = dotOffset,
            )
        }
    }
}

fun DrawScope.getLinePath(
    dataPoints: List<Float>,
    maxValue: Float,
    minValue: Float,
    rounded: Boolean = true,
    size: Size? = null
): Path {

    val _size = size ?: this.size
    val path = Path()

    val calculateHeight = { value: Float ->
        value.heightFor(
            maxValue = maxValue,
            minValue = minValue,
            totalHeight = _size.height
        )
    }

    path.moveTo(0f, _size.height - calculateHeight(dataPoints[0]))

    if (rounded) {
        for (i in 0 until dataPoints.size - 1) {
            val x1 = (i * (_size.width / (dataPoints.size - 1)))
            val y1 = _size.height - calculateHeight(dataPoints[i])
            val x2 = ((i + 1) * (_size.width / (dataPoints.size - 1)))
            val y2 = _size.height - calculateHeight(dataPoints[i + 1])

            val cx = (x1 + x2) / 2
            path.cubicTo(cx, y1, cx, y2, x2, y2)
        }
    } else {
        dataPoints.forEachIndexed { index, value ->
            path.lineTo(
                _size.width.spaceBetween(itemCount = dataPoints.count(), index = index),
                y = _size.height - calculateHeight(value)
            )
        }
    }
    return path
}

fun DrawScope.drawGradient(
    path: Path,
    color1: Color,
    color2: Color,
    progress: Float,
    size: Size? = null
) {
    val _size = size ?: this.size
    drawIntoCanvas {
        it.nativeCanvas.drawPath(android.graphics.Path().apply {
            addPath(path.asAndroidPath())
            lineTo(_size.width, _size.height)
            lineTo(0f, _size.height)
            close()
        }, android.graphics.Paint().apply {
            setShader(
                LinearGradient(
                    0f,
                    0f,
                    0f,
                    _size.height,
                    color1.copy(alpha = color1.alpha * progress).toArgb(),
                    color2.toArgb(),
                    Shader.TileMode.MIRROR
                )
            )
        })
    }
}


/**
 * This function calculates height from total height for a specific value
 */
private fun Float.heightFor(
    maxValue: Float,
    minValue: Float,
    totalHeight: Float
): Float {
    val range = maxValue - minValue
    val percentage = (this - minValue) / range
    val height = totalHeight * percentage
    return height
}

/**
 * This function is reverse of Float.heightFor, calculates value from total value for a specific height
 */
private fun Float.valueFor(minValue: Double, maxValue: Double, totalHeight: Float): Double {
    val percentage = this / totalHeight
    val range = maxValue - minValue
    val value = minValue + percentage * range
    return value
}

