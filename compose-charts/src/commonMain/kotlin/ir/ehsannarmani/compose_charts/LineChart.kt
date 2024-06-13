package ir.ehsannarmani.compose_charts

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
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
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
import ir.ehsannarmani.compose_charts.utils.calculateOffset
import ir.ehsannarmani.compose_charts.utils.calculateValue
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
    require(minValue <= data.minOf { it.values.minOf { it } }) {
        "Chart data must be at least $minValue (Specified Min Value)"
    }
    require(maxValue >= data.maxOf { it.values.maxOf { it } }) {
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

    val dotAnimators = remember {
        mutableStateListOf<List<Animatable<Float, AnimationVector1D>>>()
    }
    val popupPositions = remember {
        mutableStateListOf<Offset>()
    }

    val popupsOffsetAnimators = remember {
        mutableStateListOf<Pair<Animatable<Float, AnimationVector1D>, Animatable<Float, AnimationVector1D>>>()
    }
    val labelAreaHeight = remember {
        if (labelProperties.enabled) {
            if (labelProperties.labels.isNotEmpty()) {
                labelProperties.labels.maxOf {
                    textMeasurer.measure(
                        it,
                        style = labelProperties.textStyle
                    ).size.height
                } + (labelProperties.padding.value * density.density).toInt()
            } else {
                error("Labels enabled, but there is no label provided to show, disable labels or fill 'labels' parameter in LabelProperties")
            }
        } else {
            0
        }
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
                                popupsOffsetAnimators.clear()
                            }
                        },
                        onDrag = { change, amount ->
                            popupPositions.clear()
                            pathsOffsets
                                .map {
                                    it.offset.firstOrNull { (_, offset) ->
                                        offset.x >= change.position.x
                                    } ?: it.offset.lastOrNull { (_, offset) ->
                                        offset.x <= change.position.x
                                    }
                                }
                                .also {
                                    val popups = it.filterNotNull()
                                    if (popupsOffsetAnimators.count() < popups.count()) {
                                        repeat(popups.count()) {
                                            popupsOffsetAnimators.add(
                                                Animatable(0f) to Animatable(
                                                    0f
                                                )
                                            )
                                        }
                                    }
                                    popupPositions.addAll(popups.map { it.second })
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
                    val zeroY = chartAreaHeight - calculateOffset(
                        minValue = minValue.toFloat(),
                        maxValue = maxValue.toFloat(),
                        total = chartAreaHeight,
                        value = 0.0f
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
                    drawPopup(
                        maxValue = maxValue,
                        minValue = minValue,
                        canvasHeight = size.height,
                        offset = offset,
                        popupProperties = popupProperties,
                        textMeasurer = textMeasurer,
                        scope = scope,
                        progress = popupAnimation.value,
                        nextPopup = popupPositions.getOrNull(index + 1),
                        offsetAnimator = popupsOffsetAnimators.getOrNull(index)
                    )
                }
            }
        }
    }
}


fun DrawScope.drawPopup(
    maxValue: Double,
    minValue: Double,
    canvasHeight: Float,
    offset: Offset,
    popupProperties: PopupProperties,
    textMeasurer: TextMeasurer,
    scope: CoroutineScope,
    progress: Float,
    nextPopup: Offset?,
    offsetAnimator: Pair<Animatable<Float, AnimationVector1D>, Animatable<Float, AnimationVector1D>>? = null,
) {
    val value = (maxValue + minValue) - calculateValue(
        minValue = minValue,
        maxValue = maxValue,
        total = canvasHeight,
        offset = offset.y
    )
    val measureResult = textMeasurer.measure(
        popupProperties.contentBuilder(value),
        style = popupProperties.textStyle.copy(
            color = popupProperties.textStyle.color.copy(
                alpha = 1f * progress
            )
        )
    )
    var rectSize = measureResult.size.toSize()
    rectSize = rectSize.copy(
        width = (rectSize.width + (popupProperties.contentHorizontalPadding.toPx() * 2)),
        height = (rectSize.height + (popupProperties.contentVerticalPadding.toPx() * 2))
    )

    val conflictDetected =
        ((nextPopup != null) && offset.y in nextPopup.y - rectSize.height..nextPopup.y + rectSize.height) ||
                (offset.x + rectSize.width) > size.width


    val rectOffset = if (conflictDetected) {
        offset.copy(x = offset.x - rectSize.width)
    } else {
        offset
    }
    offsetAnimator?.also { (x, y) ->
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
    if (offsetAnimator != null) {
        val animatedOffset = Offset(
            x = offsetAnimator.first.value,
            y = offsetAnimator.second.value
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
            alpha = 1f * progress
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

fun DrawScope.drawDots(
    dataPoints: List<Pair<Animatable<Float, AnimationVector1D>, Float>>,
    properties: DotProperties,
    linePath: Path,
    maxValue: Float,
    pathMeasure: PathMeasure,
    scope: CoroutineScope,
    size: Size? = null,
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
                radius = (properties.radius.toPx() + properties.strokeWidth.toPx() / 2) * value.first.value
                strokeRadius = properties.radius.toPx() * value.first.value
            } else {
                radius = properties.radius.toPx() + properties.strokeWidth.toPx() / 2
                strokeRadius = properties.radius.toPx()
            }
            drawCircle(
                brush = properties.strokeColor,
                radius = radius,
                center = dotOffset,
                style = Stroke(width = properties.strokeWidth.toPx(), pathEffect = pathEffect),
            )
            drawCircle(
                brush = properties.color,
                radius = strokeRadius,
                center = dotOffset,
            )
        }
    }
}

private fun DrawScope.getLinePath(
    dataPoints: List<Float>,
    maxValue: Float,
    minValue: Float,
    rounded: Boolean = true,
    size: Size? = null
): Path {

    val _size = size ?: this.size
    val path = Path()

    val calculateHeight = { value: Float ->
        calculateOffset(
            maxValue = maxValue,
            minValue = minValue,
            total = _size.height,
            value = value
        )
    }

    path.moveTo(0f, _size.height - calculateHeight(dataPoints[0]))

    for (i in 0 until dataPoints.size - 1) {
        val x1 = (i * (_size.width / (dataPoints.size - 1)))
        val y1 = _size.height - calculateHeight(dataPoints[i])
        val x2 = ((i + 1) * (_size.width / (dataPoints.size - 1)))
        val y2 = _size.height - calculateHeight(dataPoints[i + 1])

        if (rounded) {
            val cx = (x1 + x2) / 2
            path.cubicTo(cx, y1, cx, y2, x2, y2)
        } else {
            path.cubicTo(x1, y1, x1, y1, (x1 + x2) / 2, (y1 + y2) / 2)
            path.cubicTo((x1 + x2) / 2, (y1 + y2) / 2, x2, y2, x2, y2)
        }
    }
    return path
}

private fun DrawScope.drawGradient(
    path: Path,
    color1: Color,
    color2: Color,
    progress: Float,
    size: Size? = null
) {
    val _size = size ?: this.size
    drawIntoCanvas {
        val p = Path()
        p.addPath(path)
        p.lineTo(_size.width, _size.height)
        p.lineTo(0f, _size.height)
        p.close()
        val paint = Paint()
        paint.shader = LinearGradientShader(
            Offset(0f, 0f),
            Offset(0f, _size.height),
            listOf(
                color1.copy(alpha = color1.alpha * progress),
                color2,
            ),
            tileMode = TileMode.Mirror
        )
        it.drawPath(p, paint)
    }
}

