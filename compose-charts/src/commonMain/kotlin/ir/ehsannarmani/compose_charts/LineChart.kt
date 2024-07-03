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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
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
import ir.ehsannarmani.compose_charts.extensions.line_chart.drawLineGradient
import ir.ehsannarmani.compose_charts.extensions.line_chart.getLinePath
import ir.ehsannarmani.compose_charts.extensions.line_chart.getPopupValue
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class Popup(
    val properties: PopupProperties,
    val position: Offset,
    val value: Double
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
    if (data.isNotEmpty()) {
        require(minValue <= data.minOf { it.values.minOf { it } }) {
            "Chart data must be at least $minValue (Specified Min Value)"
        }
        require(maxValue >= data.maxOf { it.values.maxOf { it } }) {
            "Chart data must be at most $maxValue (Specified Max Value)"
        }
        require(data.none { it.values.isEmpty() }) {
            "Chart data should not contain empty values"
        }
    }

    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    val pathMeasure = remember {
        PathMeasure()
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
    val popups = remember {
        mutableStateListOf<Popup>()
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



    LaunchedEffect(Unit) {
        if (zeroLineProperties.enabled) {
            zeroLineAnimation.snapTo(0f)
            zeroLineAnimation.animateTo(1f, animationSpec = zeroLineProperties.animationSpec)
        }
    }

    LaunchedEffect(data) {
        dotAnimators.clear()
        launch {
            data.forEach {
                val animators = mutableListOf<Animatable<Float, AnimationVector1D>>()
                it.values.forEach {
                    animators.add(Animatable(0f))
                }
                dotAnimators.add(animators)
            }
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
            val paddingBottom = (labelAreaHeight / density.density).dp
            if (indicatorProperties.enabled) {
                if (indicatorProperties.position == IndicatorProperties.Position.Start) {
                    Indicator(
                        modifier = Modifier.padding(bottom = paddingBottom),
                        indicatorProperties = indicatorProperties,
                        minValue = minValue,
                        maxValue = maxValue
                    )
                    Spacer(modifier = Modifier.width(18.dp))
                }
            }
            Canvas(modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .pointerInput(data) {
                    detectDragGestures(
                        onDragEnd = {
                            scope.launch {
                                popupAnimation.animateTo(0f, animationSpec = tween(500))
                                popups.clear()
                                popupsOffsetAnimators.clear()
                            }
                        },
                        onDrag = { change, amount ->
                            val _size = size.toSize().copy(height = (size.height - labelAreaHeight).toFloat())
                            popups.clear()
                            data.forEach {
                                val properties = it.popupProperties ?: popupProperties

                                if (properties.enabled) {
                                    val positionX = (change.position.x).coerceIn(0f, size.width.toFloat())
                                    val fraction = (positionX / size.width)
                                    val popupValue = getPopupValue(
                                        points = it.values,
                                        fraction = fraction.toDouble(),
                                        rounded = it.curvedEdges ?: curvedEdges,
                                        size = _size,
                                        minValue = minValue,
                                        maxValue = maxValue
                                    )
                                    popups.add(
                                        Popup(
                                            position = popupValue.offset,
                                            value = popupValue.calculatedValue,
                                            properties = properties
                                        )
                                    )
                                    // add popup offset animators
                                    if (popupsOffsetAnimators.count() < popups.count()) {
                                        repeat(popups.count() - popupsOffsetAnimators.count()) {
                                            popupsOffsetAnimators.add(
                                                Animatable(0f) to Animatable(
                                                    0f
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                            scope.launch {
                                // animate popup (alpha)
                                if (popupAnimation.value != 1f && !popupAnimation.isRunning) {
                                    popupAnimation.animateTo(1f, animationSpec = tween(500))
                                }
                            }
                        }
                    )
                }
            ) {
                val chartAreaHeight = size.height - labelAreaHeight
                val drawZeroLine = {
                    val zeroY = chartAreaHeight - calculateOffset(
                        minValue = minValue,
                        maxValue = maxValue,
                        total = chartAreaHeight,
                        value = 0f
                    ).toFloat()
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
                    indicatorProperties = indicatorProperties,
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
                        drawLineGradient(
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
                        drawLineGradient(
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
                            minValue = minValue.toFloat(),
                            pathMeasure = pathMeasure,
                            scope = scope,
                            size = size.copy(height = chartAreaHeight)
                        )
                    }
                }
                if (zeroLineProperties.enabled && zeroLineProperties.zType == ZeroLineProperties.ZType.Above) {
                    drawZeroLine()
                }
                popups.forEachIndexed { index, popup ->
                    drawPopup(
                        popup = popup,
                        nextPopup = popups.getOrNull(index + 1),
                        textMeasurer = textMeasurer,
                        scope = scope,
                        progress = popupAnimation.value,
                        offsetAnimator = popupsOffsetAnimators.getOrNull(index)
                    )
                }
            }
            if (indicatorProperties.enabled) {
                if (indicatorProperties.position == IndicatorProperties.Position.End) {
                    Spacer(modifier = Modifier.width(18.dp))
                    Indicator(
                        modifier = Modifier.padding(bottom = paddingBottom),
                        indicatorProperties = indicatorProperties,
                        minValue = minValue,
                        maxValue = maxValue
                    )
                }
            }
        }
    }
}

@Composable
private fun Indicator(
    modifier: Modifier = Modifier,
    indicatorProperties: IndicatorProperties,
    minValue: Double,
    maxValue: Double
) {
    Column(
        modifier = modifier
            .fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        split(
            count = indicatorProperties.count,
            minValue = minValue,
            maxValue = maxValue
        ).forEach {
            BasicText(
                text = indicatorProperties.contentBuilder(it),
                style = indicatorProperties.textStyle
            )
        }
    }
}

private fun DrawScope.drawPopup(
    popup: Popup,
    nextPopup: Popup?,
    textMeasurer: TextMeasurer,
    scope: CoroutineScope,
    progress: Float,
    offsetAnimator: Pair<Animatable<Float, AnimationVector1D>, Animatable<Float, AnimationVector1D>>? = null,
) {
    val offset = popup.position
    val popupProperties = popup.properties
    val measureResult = textMeasurer.measure(
        popupProperties.contentBuilder(popup.value),
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
        ((nextPopup != null) && offset.y in nextPopup.position.y - rectSize.height..nextPopup.position.y + rectSize.height) ||
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
    minValue: Float,
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
            y = (_size.height - calculateOffset(
                maxValue = maxValue.toDouble(),
                minValue = minValue.toDouble(),
                total = _size.height,
                value = value.second
            )).toFloat()

        )
        if (lastPosition != Offset.Unspecified && lastPosition.x >= dotOffset.x - 20 || !properties.animationEnabled) {
            if (!value.first.isRunning && properties.animationEnabled && value.first.value != 1f) {
                scope.launch {
                    value.first.animateTo(1f, animationSpec = properties.animationSpec)
                }
            }

            val radius: Float
            val strokeRadius: Float
            if (properties.animationEnabled) {
                radius =
                    (properties.radius.toPx() + properties.strokeWidth.toPx() / 2) * value.first.value
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








