package ir.ehsannarmani.compose_charts

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
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
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import ir.ehsannarmani.compose_charts.components.LabelHelper
import ir.ehsannarmani.compose_charts.extensions.line_chart.PathData
import ir.ehsannarmani.compose_charts.extensions.line_chart.Value
import ir.ehsannarmani.compose_charts.extensions.line_chart.drawLineGradient
import ir.ehsannarmani.compose_charts.extensions.line_chart.getLinePath
import ir.ehsannarmani.compose_charts.extensions.line_chart.getPopupValue
import ir.ehsannarmani.compose_charts.extensions.spaceBetween
import ir.ehsannarmani.compose_charts.extensions.split
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.IndicatorPosition
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.PopupProperties
import ir.ehsannarmani.compose_charts.models.ZeroLineProperties
import ir.ehsannarmani.compose_charts.utils.calculateOffset
import ir.ehsannarmani.compose_charts.utils.rememberComputedChartMaxValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max

private data class Popup(
    val properties: PopupProperties,
    val position: Offset,
    val value: Double,
    val dataIndex: Int,
    val valueIndex: Int
)

@Composable
fun LineChart(
    modifier: Modifier = Modifier,
    data: List<Line>,
    curvedEdges: Boolean = true,
    animationDelay: Long = 300,
    animationMode: AnimationMode = AnimationMode.Together(),
    gridProperties: GridProperties = GridProperties(),
    zeroLineProperties: ZeroLineProperties = ZeroLineProperties(),
    indicatorProperties: HorizontalIndicatorProperties = HorizontalIndicatorProperties(
        textStyle = TextStyle.Default,
        padding = 16.dp
    ),
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
    maxValue: Double = data.maxOfOrNull { line -> line.values.maxOfOrNull { it } ?: 0.0 } ?: 0.0,
    minValue: Double = if (data.any { line -> line.values.any { it < 0.0 } }) data.minOfOrNull { line ->
        line.values.minOfOrNull { it } ?: 0.0
    } ?: 0.0 else 0.0
) {
    if (data.isNotEmpty()) {
        require(minValue <= (data.minOfOrNull { line -> line.values.minOfOrNull { it } ?: 0.0 }
            ?: 0.0)) {
            "Chart data must be at least $minValue (Specified Min Value)"
        }
        require(maxValue >= (data.maxOfOrNull { line -> line.values.maxOfOrNull { it } ?: 0.0 }
            ?: 0.0)) {
            "Chart data must be at most $maxValue (Specified Max Value)"
        }
    }

    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    var chartSize by remember(density) { mutableStateOf(Size(0f, 0f)) }

    val pathMeasure = remember(chartSize) {
        PathMeasure()
    }

    val popupAnimation = remember(data) {
        Animatable(0f)
    }

    val zeroLineAnimation = remember(data) {
        Animatable(0f)
    }

    val dotAnimators = remember(data) {
        mutableStateListOf<List<Animatable<Float, AnimationVector1D>>>()
    }

    val popups = remember(data) {
        mutableStateListOf<Popup>()
    }
    val popupsOffsetAnimators = remember(chartSize, data) {
        mutableStateListOf<Pair<Animatable<Float, AnimationVector1D>, Animatable<Float, AnimationVector1D>>>()
    }
    val linesPathData = remember(chartSize, data) {
        mutableStateListOf<PathData>()
    }

    val computedMaxValue =
        rememberComputedChartMaxValue(minValue, maxValue, indicatorProperties.count)
    val indicators = remember(indicatorProperties.indicators, minValue, maxValue) {
        indicatorProperties.indicators.ifEmpty {
            split(
                count = indicatorProperties.count,
                minValue = minValue,
                maxValue = computedMaxValue
            )
        }
    }

    LaunchedEffect(Unit) {
        if (zeroLineProperties.enabled) {
            zeroLineAnimation.snapTo(0f)
            zeroLineAnimation.animateTo(1f, animationSpec = zeroLineProperties.animationSpec)
        }
    }

    // make animators
    LaunchedEffect(data) {
        dotAnimators.clear()
        data.forEach {
            val animators = mutableListOf<Animatable<Float, AnimationVector1D>>()
            repeat(it.values.size) {
                animators.add(Animatable(0f))
            }
            dotAnimators.add(animators)
        }
    }

    // animate
    LaunchedEffect(data) {
        if (animationMode != AnimationMode.None) delay(animationDelay)

        val animateStroke: suspend (Line) -> Unit = { line ->
            line.strokeProgress.animateTo(1f, animationSpec = line.strokeAnimationSpec)
        }
        val animateGradient: suspend (Line) -> Unit = { line ->
            delay(line.gradientAnimationDelay)
            line.gradientProgress.animateTo(1f, animationSpec = line.gradientAnimationSpec)
        }
        data.forEachIndexed { index, line ->
            when (animationMode) {
                is AnimationMode.OneByOne -> {
                    launch {
                        animateGradient(line)
                    }
                    animateStroke(line)
                }

                is AnimationMode.Together -> {
                    launch {
                        delay(animationMode.delayBuilder(index))
                        animateStroke(line)
                    }
                    launch {
                        delay(animationMode.delayBuilder(index))
                        animateGradient(line)
                    }
                }

                is AnimationMode.None -> {
                    line.gradientProgress.snapTo(1f)
                    line.strokeProgress.snapTo(1f)
                }
            }
        }
    }

    LaunchedEffect(data, minValue, computedMaxValue) {
        linesPathData.clear()
    }

    suspend fun hidePopup() {
        popupAnimation.animateTo(0f, animationSpec = tween(300))
        popups.clear()
        popupsOffsetAnimators.clear()
    }

    fun PointerInputScope.showPopup(
        data: List<Line>,
        size: IntSize,
        position: Offset,
        insetPad: InsetPad,
    ) {
        popups.clear()

        data.forEachIndexed { dataIndex, line ->
            val properties = line.popupProperties ?: popupProperties
            if (!properties.enabled) return@forEachIndexed

            val bounds = insetPad.toBounds(size)
            val positionX = position.x.coerceIn(bounds.xMin, bounds.xMax)
            val pathData = linesPathData[dataIndex]

            val isSingleValue = line.values.count() == 1

            if (
                positionX >= (pathData.xPositions[pathData.startIndex] + insetPad.left) &&
                positionX <= (pathData.xPositions[pathData.endIndex] + insetPad.left) ||
                isSingleValue
            ) {
                val showOnPointsThreshold =
                    ((properties.mode as? PopupProperties.Mode.PointMode)?.threshold
                        ?: 0.dp).toPx()
                val pointX = pathData.xPositions.find {
                    (it + insetPad.left) in positionX - showOnPointsThreshold..positionX + showOnPointsThreshold
                }

                if (properties.mode !is PopupProperties.Mode.PointMode || pointX != null || isSingleValue) {
                    val relevantX =
                        if (properties.mode is PopupProperties.Mode.PointMode) (pointX?.toFloat()
                            ?: 0f) else positionX - insetPad.left
                    val fraction = ((relevantX) / insetPad.width(size))

                    val valueIndex = if (isSingleValue) {
                        0
                    } else {
                        calculateValueIndex(
                            fraction = fraction.toDouble(),
                            values = line.values,
                            pathData = pathData
                        )
                    }

                    val popupValue = if (isSingleValue) {
                        Value(
                            calculatedValue = line.values.first(),
                            offset = Offset(
                                x = 0f,
                                y = insetPad.height(size) - calculateOffset(
                                    maxValue = maxValue,
                                    minValue = minValue,
                                    value = line.values.first().toFloat(),
                                    total = insetPad.height(size)
                                ).toFloat()
                            )
                        )
                    } else {
                        getPopupValue(
                            points = line.values,
                            fraction = fraction.toDouble(),
                            rounded = line.curvedEdges ?: curvedEdges,
                            size = Size(insetPad.width(size), insetPad.height(size)),
                            minValue = minValue,
                            maxValue = computedMaxValue
                        )
                    }

                    popups.add(
                        Popup(
                            position = popupValue.offset,
                            value = popupValue.calculatedValue,
                            properties = properties,
                            dataIndex = dataIndex,
                            valueIndex = valueIndex
                        )
                    )

                    if (popupsOffsetAnimators.count() < popups.count()) {
                        repeat(popups.count() - popupsOffsetAnimators.count()) {
                            popupsOffsetAnimators.add(
                                if (properties.mode is PopupProperties.Mode.PointMode) {
                                    Animatable(popupValue.offset.x) to Animatable(popupValue.offset.y)
                                } else {
                                    Animatable(0f) to Animatable(0f)
                                }
                            )
                        }
                    }
                }
            }
        }

        scope.launch {
            if (popupAnimation.value != 1f && !popupAnimation.isRunning) {
                popupAnimation.animateTo(1f, animationSpec = popupProperties.animationSpec)
            }
        }
    }

    var onPressJob: Job? = null

    Column(modifier = modifier) {
        if (labelHelperProperties.enabled) {
            data.mapNotNull { line -> line.label?.let { line.label to line.color } }
                .takeIf { it.isNotEmpty() }
                ?.let {
                    LabelHelper(
                        data = it,
                        properties = labelHelperProperties
                    )
                    Spacer(modifier = Modifier.height(labelHelperPadding))
                }
        }
        Row(modifier = Modifier.fillMaxSize().weight(1f)) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                LineChartCanvas(
                    data = data,
                    maxValue = computedMaxValue,
                    minValue = minValue,
                    indicators = indicators,
                    indicatorProperties = indicatorProperties,
                    labelProperties = labelProperties,
                    pathData = linesPathData,
                    popupProperties = popupProperties,
                    scope = scope,
                    hidePopup = { hidePopup() },
                    showPopup = { data, size, position, insetPad ->
                        showPopup(
                            data,
                            size,
                            position,
                            insetPad
                        )
                    },
                    onPressJob = onPressJob,
                    onPressJobChange = { job -> onPressJob = job }
                ) { xTicks, yTicks ->
                    val drawZeroLine = {
                        val zeroY = size.height - calculateOffset(
                            minValue = minValue,
                            maxValue = computedMaxValue,
                            total = size.height,
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
                    if (linesPathData.isEmpty() || linesPathData.count() != data.count()) {
                        data.map {
                            getLinePath(
                                dataPoints = it.values.mapIndexed { index, v -> index.toFloat() to v.toFloat() },
                                maxValue = computedMaxValue.toFloat(),
                                minValue = minValue.toFloat(),
                                rounded = it.curvedEdges ?: curvedEdges,
                                size = size
                            )
                        }.also {
                            linesPathData.addAll(it)
                        }
                    }

                    drawTicks(
                        xTicks,
                        TickDirection.Vertical,
                        gridProperties.enabled,
                        gridProperties.xAxisProperties
                    )
                    drawTicks(
                        yTicks,
                        TickDirection.Horizontal,
                        gridProperties.enabled,
                        gridProperties.yAxisProperties
                    )

                    if (zeroLineProperties.enabled && zeroLineProperties.zType == ZeroLineProperties.ZType.Under) {
                        drawZeroLine()
                    }
                    data.forEachIndexed { index, line ->
                        val pathData = linesPathData.getOrNull(index) ?: return@LineChartCanvas
                        val segmentedPath = Path()
                        pathMeasure.setPath(pathData.path, false)
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

                        var startOffset = 0f
                        var endOffset = size.width
                        if (pathData.startIndex > 0) {
                            startOffset = pathData.xPositions[pathData.startIndex].toFloat()
                        }

                        if (pathData.endIndex < line.values.size - 1) {
                            endOffset = pathData.xPositions[pathData.endIndex].toFloat()
                        }

                        if (line.firstGradientFillColor.isSpecified) {
                            drawLineGradient(
                                path = pathData.path,
                                color1 = line.firstGradientFillColor,
                                color2 = line.secondGradientFillColor.takeOrElse { line.firstGradientFillColor },
                                progress = line.gradientProgress.value,
                                size = size,
                                startOffset,
                                endOffset
                            )
                        } else if (line.drawStyle is DrawStyle.Fill) {
                            var fillColor = Color.Unspecified
                            if (line.color is SolidColor) {
                                fillColor = line.color.value
                            }
                            drawLineGradient(
                                path = pathData.path,
                                color1 = fillColor,
                                color2 = fillColor,
                                progress = 1f,
                                size = size,
                                startOffset,
                                endOffset
                            )
                        }

                        if ((line.dotProperties?.enabled ?: dotsProperties.enabled)) {
                            drawDots(
                                dataPoints = line.values.mapIndexed { mapIndex, value ->
                                    DotInfo(
                                        animator = dotAnimators
                                            .getOrNull(index)
                                            ?.getOrNull(mapIndex)
                                            ?: Animatable(0f),
                                        dataIndex = index,
                                        value = value.toFloat()
                                    )
                                },
                                properties = line.dotProperties ?: dotsProperties,
                                linePath = segmentedPath,
                                maxValue = computedMaxValue.toFloat(),
                                minValue = minValue.toFloat(),
                                pathMeasure = pathMeasure,
                                scope = scope,
                                startIndex = pathData.startIndex,
                                endIndex = pathData.endIndex
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

            }
        }
    }
}

private fun calculateValueIndex(
    fraction: Double,
    values: List<Double>,
    pathData: PathData
): Int {
    val xPosition = (fraction * pathData.path.getBounds().width).toFloat()
    val closestXIndex = pathData.xPositions.indexOfFirst { x ->
        x >= xPosition
    }
    return if (closestXIndex >= 0) closestXIndex else values.size - 1
}

private fun DrawScope.drawPopup(
    popup: Popup,
    nextPopup: Popup?,
    textMeasurer: TextMeasurer,
    scope: CoroutineScope,
    progress: Float,
    offsetAnimator: Pair<Animatable<Float, AnimationVector1D>, Animatable<Float, AnimationVector1D>>? = null
) {
    val popupProperties = popup.properties
    val popupData = PopupProperties.Popup(
        dataIndex = popup.dataIndex,
        valueIndex = popup.valueIndex,
        value = popup.value
    )
    if (!popupProperties.confirmDraw(popupData)) return

    val offset = popup.position
    val measureResult = textMeasurer.measure(
        popupProperties.contentBuilder(popupData),
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
        if (x.value == 0f || y.value == 0f || popupProperties.mode is PopupProperties.Mode.PointMode) {
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
        val animatedOffset = if (popup.properties.mode is PopupProperties.Mode.PointMode) {
            rectOffset
        } else {
            Offset(
                x = offsetAnimator.first.value,
                y = offsetAnimator.second.value
            )
        }
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

private sealed class TickDirection {
    data object Horizontal : TickDirection()
    data object Vertical : TickDirection()
}

private fun DrawScope.drawTicks(
    ticks: List<Float>,
    tickDirection: TickDirection,
    gridEnabled: Boolean,
    axisProperties: GridProperties.AxisProperties,
) {
    if (gridEnabled && axisProperties.enabled) {
        val getOffsets = { tick: Float ->
            if (tickDirection == TickDirection.Horizontal) {
                Offset(0f, tick) to Offset(size.width, tick)
            } else {
                Offset(tick, 0f) to Offset(tick, size.height)
            }
        }
        ticks
            .ifEmpty {
                (0 until axisProperties.lineCount)
                    .map { it.toFloat() * size.width / (axisProperties.lineCount - 1) }
            }
            .forEach { tick ->
                val (start, end) = getOffsets(tick)
                drawLine(
                    brush = axisProperties.color,
                    start = start,
                    end = end,
                    strokeWidth = axisProperties.thickness.toPx(),
                    pathEffect = axisProperties.style.pathEffect,
                )
            }
    }
}

private fun DrawScope.drawDots(
    dataPoints: List<DotInfo>,
    properties: DotProperties,
    linePath: Path,
    maxValue: Float,
    minValue: Float,
    pathMeasure: PathMeasure,
    scope: CoroutineScope,
    startIndex: Int,
    endIndex: Int,
) {

    val pathEffect = properties.strokeStyle.pathEffect

    pathMeasure.setPath(linePath, false)
    val lastPosition = pathMeasure.getPosition(pathMeasure.length)
    dataPoints.forEachIndexed { valueIndex, value ->
        if (
            properties.confirmDraw(
                DotProperties.Dot(
                    value.dataIndex,
                    valueIndex,
                    value.value.toDouble()
                )
            ) &&
            valueIndex in startIndex..endIndex
        ) {
            val dotOffset = Offset(
                x = size.width.spaceBetween(
                    itemCount = dataPoints.count(),
                    index = valueIndex
                ),
                y = (size.height - calculateOffset(
                    maxValue = maxValue.toDouble(),
                    minValue = minValue.toDouble(),
                    total = size.height,
                    value = value.value
                )).toFloat()

            )
            if (lastPosition != Offset.Unspecified && lastPosition.x >= dotOffset.x - 20 || !properties.animationEnabled || dataPoints.count() == 1) {
                if (!value.animator.isRunning && properties.animationEnabled && value.animator.value != 1f) {
                    scope.launch {
                        value.animator.animateTo(1f, animationSpec = properties.animationSpec)
                    }
                }

                val radius: Float
                val strokeRadius: Float
                if (properties.animationEnabled) {
                    radius =
                        (properties.radius.toPx() + properties.strokeWidth.toPx() / 2) * value.animator.value
                    strokeRadius = properties.radius.toPx() * value.animator.value
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
}

data class DotInfo(
    val animator: Animatable<Float, AnimationVector1D>,
    val dataIndex: Int,
    val value: Float,
)

data class InsetBounds(
    val xMin: Float,
    val xMax: Float,
    val yMin: Float,
    val yMax: Float,
) {
    fun xWithin(x: Float): Boolean {
        return x in xMin..xMax
    }

    fun yWithin(y: Float): Boolean {
        return y in yMin..yMax
    }

    fun pointWithin(x: Float, y: Float): Boolean {
        return xWithin(x) && yWithin(y)
    }
}

data class InsetPad(
    var left: Float,
    var right: Float,
    var top: Float,
    var bottom: Float,
) {
    fun width(size: IntSize): Float {
        return size.width - left - right
    }

    fun height(size: IntSize): Float {
        return size.height - top - bottom
    }

    fun toBounds(size: IntSize) = InsetBounds(
        left, size.width - right, top, size.height - bottom
    )
}

fun DrawScope.inset(insetPad: InsetPad, block: DrawScope.() -> Unit) =
    inset(
        left = insetPad.left,
        right = insetPad.right,
        top = insetPad.top,
        bottom = insetPad.bottom
    ) {
        block()
    }

fun getInsetPad(
    textMeasurer: TextMeasurer,
    indicators: List<Double>,
    indicatorProperties: HorizontalIndicatorProperties,
    labelProperties: LabelProperties
): InsetPad {
    val measureLabel =
        { text: String -> textMeasurer.measure(text, style = labelProperties.textStyle) }
    val measureIndicator =
        { text: String -> textMeasurer.measure(text, style = indicatorProperties.textStyle) }
    val labels = labelProperties.labels
    val insetPad = InsetPad(0f, 0f, 0f, 0f)
    if (indicators.isNotEmpty() && indicatorProperties.enabled) {
        val indicatorPad = measureIndicator("M").size.width
        val textIndicators = indicators.map(indicatorProperties.contentBuilder)
        val maxIndicatorWidth = textIndicators.maxOf { measureIndicator(it).size.width }
        val maxIndicatorHeight = textIndicators.maxOf { measureIndicator(it).size.height }
        insetPad.top = maxIndicatorHeight / 2f
        if (labels.isNotEmpty()) {
            val labelPad = measureLabel("M").size.width
            insetPad.bottom = labels.maxOf { measureLabel(it).size.height }.toFloat() + labelPad
            if (indicatorProperties.position == IndicatorPosition.Horizontal.Start) {
                insetPad.left =
                    max(measureLabel(labels[0]).size.width / 2f, maxIndicatorWidth.toFloat()) +
                            indicatorPad.toFloat()
                insetPad.right = measureLabel(labels.last()).size.width / 2f
            } else {
                insetPad.left = measureLabel(labels[0]).size.width / 2f
                insetPad.right =
                    max(measureLabel(labels.last()).size.width / 2f, maxIndicatorWidth.toFloat()) +
                            indicatorPad.toFloat()
            }
        } else {
            insetPad.bottom = (maxIndicatorHeight) / 2f
            if (indicatorProperties.position == IndicatorPosition.Horizontal.Start) {
                insetPad.left = (maxIndicatorWidth + indicatorPad).toFloat()
            } else {
                insetPad.right = (maxIndicatorWidth + indicatorPad).toFloat()
            }
        }
    } else if (labels.isNotEmpty()) {
        insetPad.left = measureLabel(labels[0]).size.width / 2f
        insetPad.right = measureLabel(labels.last()).size.width / 2f
    }
    return insetPad
}

@Composable
private fun RowScope.calculateModifier(
    data: List<Line>,
    minValue: Double,
    maxValue: Double,
    pathData: SnapshotStateList<PathData>,
    popupProperties: PopupProperties,
    scope: CoroutineScope,
    hidePopup: suspend () -> Unit,
    showPopup: PointerInputScope.(
        data: List<Line>,
        size: IntSize,
        position: Offset
    ) -> Unit,
    onPressJob: Job?,
    onPressJobChange: (Job?) -> Unit
): Modifier {
    return Modifier
        .weight(1f)
        .fillMaxSize()
        .pointerInput(data, minValue, maxValue, pathData) {
            if (!popupProperties.enabled || data.all { it.popupProperties?.enabled == false })
                return@pointerInput

            detectHorizontalDragGestures(
                onDragEnd = {
                    scope.launch {
                        hidePopup()
                    }
                },
                onHorizontalDrag = { change, _ ->
                    showPopup(data, size, change.position)
                }
            )
        }
        .pointerInput(Unit) {
            if (!popupProperties.enabled || data.all { it.popupProperties?.enabled == false })
                return@pointerInput

            detectTapGestures(
                onPress = {
                    if (onPressJob?.isActive == true) {
                        onPressJob.cancel()
                        onPressJobChange(null)
                    }

                    onPressJobChange(scope.launch {
                        showPopup(data, size, it)

                        tryAwaitRelease()
                        delay(timeMillis = popupProperties.duration)

                        hidePopup()
                    })
                },
            )
        }
}

@Composable
private fun RowScope.LineChartCanvas(
    data: List<Line>,
    maxValue: Double,
    minValue: Double,
    indicators: List<Double>,
    indicatorProperties: HorizontalIndicatorProperties,
    labelProperties: LabelProperties,
    pathData: SnapshotStateList<PathData>,
    popupProperties: PopupProperties,
    scope: CoroutineScope,
    hidePopup: suspend () -> Unit,
    showPopup: PointerInputScope.(
        data: List<Line>,
        size: IntSize,
        position: Offset,
        insetPad: InsetPad
    ) -> Unit,
    onPressJob: Job?,
    onPressJobChange: (Job?) -> Unit,
    insetDrawScope: DrawScope.(xTicks: List<Float>, yTicks: List<Float>) -> Unit,
) {
    val textMeasurer = rememberTextMeasurer()
    val insetPad = getInsetPad(textMeasurer, indicators, indicatorProperties, labelProperties)
    val modifier = calculateModifier(
        data = data,
        minValue = minValue,
        maxValue = maxValue,
        pathData = pathData,
        popupProperties = popupProperties,
        scope = scope,
        hidePopup = hidePopup,
        showPopup = { data, size, position -> showPopup(data, size, position, insetPad) },
        onPressJob = onPressJob,
        onPressJobChange = onPressJobChange
    )
    Canvas(modifier = modifier) {
        val yTicks = if (indicators.isNotEmpty() && indicatorProperties.enabled) {
            val measureIndicator =
                { text: String ->
                    textMeasurer.measure(
                        text,
                        style = indicatorProperties.textStyle
                    )
                }
            val sortedIndicators = indicators.sortedBy { -it }
            val maxIndicatorWidth = sortedIndicators.maxOf {
                measureIndicator(indicatorProperties.contentBuilder(it)).size.width
            }
            val drawingHeight = size.height - insetPad.top - insetPad.bottom
            val getOffset = { value: Double ->
                drawingHeight + insetPad.top - calculateOffset(
                    maxValue,
                    minValue,
                    drawingHeight,
                    value.toFloat()
                )
            }
            sortedIndicators.map {
                val offset = getOffset(it)
                val text = indicatorProperties.contentBuilder(it)
                val textSize = measureIndicator(text)
                if (indicatorProperties.position == IndicatorPosition.Horizontal.Start) {
                    drawText(
                        textMeasurer = textMeasurer,
                        text = text,
                        style = indicatorProperties.textStyle,
                        topLeft = Offset(
                            (maxIndicatorWidth - textSize.size.width).toFloat(),
                            offset.toFloat() - textSize.size.height / 2f
                        )
                    )
                } else {
                    drawText(
                        textMeasurer = textMeasurer,
                        text = text,
                        style = indicatorProperties.textStyle,
                        topLeft = Offset(
                            (size.width - maxIndicatorWidth),
                            offset.toFloat() - textSize.size.height / 2f
                        )
                    )
                }
                (offset - insetPad.top).toFloat()
            }
        } else emptyList()
        val xTicks = if (labelProperties.labels.isNotEmpty() && labelProperties.enabled) {
            val measureLabel =
                { text: String -> textMeasurer.measure(text, style = labelProperties.textStyle) }
            val labels = labelProperties.labels
            val maxLabelHeight = labels.maxOf {
                measureLabel(it).size.height
            }
            val start = insetPad.left
            val end = size.width - insetPad.right
            val range = end - start
            labels.mapIndexed { index, label ->
                val offset = start + range * index / (labels.size - 1)
                val textSize = measureLabel(label)
                drawText(
                    textMeasurer = textMeasurer,
                    text = label,
                    style = labelProperties.textStyle,
                    topLeft = Offset(
                        offset - textSize.size.width / 2f,
                        size.height - maxLabelHeight
                    )
                )
                offset - start
            }
        } else emptyList()
        inset(insetPad) {
            insetDrawScope(xTicks, yTicks)
        }
    }
}


