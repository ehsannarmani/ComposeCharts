package ir.ehsannarmani.compose_charts

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.ehsannarmani.compose_charts.components.LabelHelper
import ir.ehsannarmani.compose_charts.extensions.degreeToRadians
import ir.ehsannarmani.compose_charts.extensions.format
import ir.ehsannarmani.compose_charts.extensions.getAngleInDegree
import ir.ehsannarmani.compose_charts.extensions.isInsidePie
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.Pie
import ir.ehsannarmani.compose_charts.models.StrokeStyle
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PieChart(
    modifier: Modifier = Modifier,
    data: List<Pie>,
    startDegree: Float = 0f,
    spaceDegree: Float = 0f,
    onPieClick: (Pie) -> Unit = {},
    selectedScale: Float = 1.1f,
    selectedPaddingDegree: Float = 5f,
    colorAnimEnterSpec: AnimationSpec<Color> = tween(500),
    scaleAnimEnterSpec: AnimationSpec<Float> = tween(500),
    spaceDegreeAnimEnterSpec: AnimationSpec<Float> = tween(500),
    colorAnimExitSpec: AnimationSpec<Color> = colorAnimEnterSpec,
    scaleAnimExitSpec: AnimationSpec<Float> = scaleAnimEnterSpec,
    spaceDegreeAnimExitSpec: AnimationSpec<Float> = spaceDegreeAnimEnterSpec,
    labelHelperProperties: LabelHelperProperties = LabelHelperProperties(),
    labelHelperPadding: Dp = 26.dp,
    labelMode: LabelMode = LabelMode.Heading,
    style: Pie.Style = Pie.Style.Fill
) {

    require(data.none { it.data < 0 }) {
        "Data must be at least 0"
    }

    val onPieClick by rememberUpdatedState(onPieClick)
    val scope = rememberCoroutineScope()
    val textMeasurer = rememberTextMeasurer()

    var pieChartCenter by remember {
        mutableStateOf(Offset.Zero)
    }

    var details by remember {
        mutableStateOf(emptyList<PieDetails>())
    }
    val pieces = remember {
        mutableListOf<PiePiece>()
    }

    val pathMeasure = remember {
        PathMeasure()
    }

    LaunchedEffect(data) {
        val currDetailsSize = details.size
        details = if (details.isNotEmpty()) {
            data.mapIndexed { mapIndex, chart ->
                if (mapIndex < currDetailsSize) {
                    PieDetails(
                        pie = chart,
                        scale = details[mapIndex].scale,
                        color = details[mapIndex].color,
                        space = details[mapIndex].space,
                        pieLabelAlpha = details[mapIndex].pieLabelAlpha,
                        outerLabelLineProgress = details[mapIndex].outerLabelLineProgress,
                    )
                } else {
                    PieDetails(pie = chart)
                }
            }
        } else {
            data.map { PieDetails(pie = it) }
        }
        pieces.clear()
    }

    LaunchedEffect(details) {
        details.forEach {
            if (it.pie.selected) {
                scope.launch {
                    it.color.animateTo(
                        it.pie.selectedColor,
                        animationSpec = it.pie.colorAnimEnterSpec ?: colorAnimEnterSpec
                    )
                }
                scope.launch {
                    it.scale.animateTo(
                        it.pie.selectedScale ?: selectedScale,
                        animationSpec = it.pie.scaleAnimEnterSpec ?: scaleAnimEnterSpec
                    )
                }
                scope.launch {
                    it.space.animateTo(
                        it.pie.selectedPaddingDegree ?: selectedPaddingDegree,
                        animationSpec = it.pie.spaceDegreeAnimEnterSpec ?: spaceDegreeAnimEnterSpec
                    )
                }
            } else {
                scope.launch {
                    it.color.animateTo(
                        it.pie.color,
                        animationSpec = it.pie.colorAnimExitSpec ?: colorAnimExitSpec
                    )
                }
                scope.launch {
                    it.scale.animateTo(
                        1f,
                        animationSpec = it.pie.scaleAnimExitSpec ?: scaleAnimExitSpec
                    )
                }
                scope.launch {
                    it.space.animateTo(
                        0f,
                        animationSpec = it.pie.spaceDegreeAnimExitSpec ?: spaceDegreeAnimExitSpec
                    )
                }
            }
            if (labelHelperProperties.enabled && labelMode is LabelMode.OnPie){
                val showLabel = labelMode.show(it.pie)
                if (showLabel){
                    scope.launch {
                        it.outerLabelLineProgress.animateTo(1f, animationSpec = labelMode.animationSpec)
                    }
                    scope.launch {
                        it.pieLabelAlpha.animateTo(1f, animationSpec = labelMode.animationSpec)
                    }
                }else{
                    scope.launch {
                        it.outerLabelLineProgress.animateTo(0f, animationSpec = labelMode.animationSpec)
                    }
                    scope.launch {
                        it.pieLabelAlpha.animateTo(0f, animationSpec = labelMode.animationSpec)
                    }
                }
            }
        }
    }

    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (labelHelperProperties.enabled && labelMode is LabelMode.Heading) {
            data.mapNotNull { pie -> pie.label?.let { pie.label to SolidColor(pie.color) } }
                .takeIf { it.isNotEmpty() }
                ?.let {
                    LabelHelper(data = it, properties = labelHelperProperties)
                    Spacer(modifier = Modifier.height(labelHelperPadding))
                }
        }
        Canvas(modifier = modifier
            .pointerInput(pieces.size, startDegree, data) {
                detectTapGestures { offset ->
                    val angleInDegree = getAngleInDegree(
                        touchTapOffset = offset,
                        pieceOffset = pieChartCenter
                    )
                    val normalizedAngle =
                        ((angleInDegree - startDegree) % 360f + 360f) % 360f

                    pieces.firstOrNull { piece ->
                        normalizedAngle in piece.startFromDegree..piece.endToDegree &&
                                isInsidePie(
                                    touchTapOffset = offset,
                                    pieceOffset = pieChartCenter,
                                    radius = piece.radius,
                                    style = style
                                )
                    }
                        ?.let {
                            val (id, _) = it
                            details.find { it.pie.id == id }
                                ?.let {
                                    onPieClick(it.pie)
                                }
                        }
                }
            }
        ) {
            pieChartCenter = center

            // outer space for label
            val labelSpace = if (labelMode is LabelMode.OnPie) {
                (labelMode.outerLineSize.first + labelMode.outerLineSize.second + 24.dp).toPx()
            } else {
                0f
            }

            val dataContainsDifferentPieStyles = data.map { it.style ?: style }.toSet().count() > 1

            val total = details.sumOf { it.pie.data } // 360 degree for total
            details.forEachIndexed { index, detail ->
                val degree = ((detail.pie.data * 360) / total)

                val drawStyle = if ((detail.pie.style ?: style) is Pie.Style.Stroke) {
                    Stroke(width = ((detail.pie.style ?: style) as Pie.Style.Stroke).width.toPx())
                } else {
                    Fill
                }
                var radius: Float = when (drawStyle) {
                    is Fill -> {
                        (minOf(size.width, size.height) / 2) - labelSpace
                    }

                    is Stroke -> {
                        (minOf(size.width, size.height) / 2) - (drawStyle.width / 2) - labelSpace
                    }
                }

                // Equalize radius with highest radius
                if(dataContainsDifferentPieStyles){
                    val highestStroke = data
                        .map { it.style ?: style }
                        .filterIsInstance<Pie.Style.Stroke>()
                        .maxByOrNull { it.width.toPx() }
                    highestStroke?.let { stroke->
                        if (drawStyle is Fill){
                            radius += stroke.width.toPx()/2
                        }else if (drawStyle is Stroke){
                            val strokeDiff = stroke.width.toPx()-drawStyle.width
                            radius += strokeDiff/2
                        }
                    }
                }
                val piecePath = if (degree >= 360.0) {
                    // draw circle instead of arc

                    pieces.add(
                        PiePiece(
                            id = detail.pie.id,
                            radius = radius * detail.scale.value,
                            startFromDegree = 0f,
                            endToDegree = 360f
                        )
                    )

                    Path().apply {
                        addOval(
                            oval = Rect(
                                center = center,
                                radius = radius * detail.scale.value
                            )
                        )
                    }
                } else {
                    val beforeItems = data.filterIndexed { filterIndex, chart -> filterIndex < index }
                    val sliceStart = beforeItems.sumOf { (it.data * 360) / total }.toFloat()

                    val arcRect = Rect(
                        center = center,
                        radius = radius * detail.scale.value
                    )

                    val arcStart = sliceStart + detail.space.value + startDegree
                    val arcSweep = degree.toFloat() - ((detail.space.value * 2) + spaceDegree)
                    val sliceEnd = sliceStart + detail.space.value + arcSweep

                    val piecePath = Path().apply {
                        arcTo(arcRect, arcStart, arcSweep, true)
                    }

                    if ((detail.pie.style ?: style) is Pie.Style.Fill) {
                        pathMeasure.setPath(piecePath, false)
                        piecePath.reset()
                        val start = pathMeasure.getPosition(0f)
                        if (!start.isUnspecified) {
                            piecePath.moveTo(start.x, start.y)
                        }
                        piecePath.lineTo(
                            (size.width / 2),
                            ((size.height / 2))
                        )
                        piecePath.arcTo(arcRect, arcStart, arcSweep, true)
                        piecePath.lineTo(
                            (size.width / 2),
                            (size.height / 2)
                        )
                    }

                    pieces.add(
                        PiePiece(
                            id = detail.pie.id,
                            radius = radius * detail.scale.value,
                            startFromDegree = sliceStart + detail.space.value,
                            endToDegree = if (sliceEnd >= 360f) 360f else sliceEnd,
                        )
                    )
                    piecePath
                }

                drawPath(
                    path = piecePath,
                    color = detail.color.value,
                    style = drawStyle,
                )
                if (labelHelperProperties.enabled && labelMode is LabelMode.OnPie){
                    drawOnPieLabel(
                        data = data,
                        index = index,
                        degree = degree,
                        total = total,
                        detail = detail,
                        startDegree = startDegree,
                        spaceDegree = spaceDegree,
                        radius = radius,
                        labelMode = labelMode,
                        textMeasurer = textMeasurer,
                        pathMeasure = pathMeasure,
                        labelHelperProperties = labelHelperProperties,
                        style = detail.pie.style ?: style
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawOnPieLabel(
    data: List<Pie>,
    index: Int,
    degree: Double,
    total: Double,
    detail: PieDetails,
    startDegree: Float,
    spaceDegree: Float,
    radius: Float,
    labelMode: LabelMode.OnPie,
    textMeasurer: TextMeasurer,
    pathMeasure: PathMeasure,
    style: Pie.Style,
    labelHelperProperties: LabelHelperProperties
) {
    val beforeItems = data.filterIndexed { filterIndex, chart -> filterIndex < index }
    // Slice center angle (canvas: 0 = 3 o'clock, clockwise)
    val midDegree = if (degree >= 360.0) {
        -90f // Any angle is suitable for a perfect circle.
    } else {
        val arcStart = beforeItems.sumOf { (it.data * 360) / total }.toFloat() +
                detail.space.value + startDegree
        val arcSweep = degree.toFloat() - ((detail.space.value * 2) + spaceDegree)
        arcStart + arcSweep / 2f
    }

    val rad = midDegree.degreeToRadians()
    val cosA = cos(rad).toFloat()
    val sinA = sin(rad).toFloat()
    val effectiveRadius = when(style){
        Pie.Style.Fill -> (radius*detail.scale.value)
        is Pie.Style.Stroke -> (radius*detail.scale.value) + style.width.toPx()/2f
    }

    // Pie Inner Label
    val innerText = when (val content = labelMode.innerLabelContent) {
        is PieInnerLabelContent.Custom -> {
            content.transform(detail.pie)
        }

        is PieInnerLabelContent.Percentage -> {
            val percent = (detail.pie.data.toFloat() / total.toFloat()) * 100f
            content.transform(percent)
        }
    }
    innerText?.let {
        val innerRadius = when(style){
            Pie.Style.Fill -> effectiveRadius * 0.6f
            is Pie.Style.Stroke -> effectiveRadius - style.width.toPx()/2
        }
        val innerLayout = textMeasurer.measure(
            text = AnnotatedString(innerText),
            style = labelMode.innerLabelStyle
        )
        drawText(
            textLayoutResult = innerLayout,
            topLeft = Offset(
                x = center.x + cosA * innerRadius - innerLayout.size.width / 2f,
                y = center.y + sinA * innerRadius - innerLayout.size.height / 2f
            ),
            alpha = detail.pieLabelAlpha.value
        )
    }
    // Pie Outer Label With Line
    detail.pie.label?.let { labelText ->
        val lineStart = Offset(
            x = center.x + cosA * effectiveRadius,
            y = center.y + sinA * effectiveRadius
        )
        // The knee point of the line, slightly outside the edge
        val elbowRadius = effectiveRadius + labelMode.outerLineSize.first.toPx()
        val elbow = Offset(
            x = center.x + cosA * elbowRadius,
            y = center.y + sinA * elbowRadius
        )
        val onRight = cosA >= 0f
        val horizontalLen = labelMode.outerLineSize.second.toPx()
        val lineEnd = Offset(
            x = elbow.x + if (onRight) horizontalLen else -horizontalLen,
            y = elbow.y
        )
        val linePath = Path().apply {
            moveTo(lineStart.x, lineStart.y)
            lineTo(elbow.x, elbow.y)
            lineTo(lineEnd.x, lineEnd.y)
        }
        pathMeasure.setPath(linePath, false)
        val animatedLinePath = Path()
        pathMeasure.getSegment(
            0f,
            pathMeasure.length * detail.outerLabelLineProgress.value,
            animatedLinePath
        )

        drawPath(
            path = animatedLinePath,
            color = detail.color.value,
            style = Stroke(
                width = labelMode.outerLineStyle.width.toPx(),
                pathEffect = labelMode.outerLineStyle.strokeStyle.pathEffect
            )
        )

        val outerLayout = textMeasurer.measure(
            text = AnnotatedString(labelText),
            style = labelHelperProperties.textStyle
        )
        drawText(
            textLayoutResult = outerLayout,
            topLeft = Offset(
                x = if (onRight) lineEnd.x + 4.dp.toPx() else lineEnd.x - outerLayout.size.width - 4.dp.toPx(),
                y = lineEnd.y - outerLayout.size.height / 2f
            ),
            alpha = detail.pieLabelAlpha.value
        )
    }
}


sealed interface LabelMode {
    data object Heading: LabelMode
    data class OnPie(
        val outerLineSize: Pair<Dp,Dp> = 12.dp to 16.dp,
        val outerLineStyle: DrawStyle.Stroke = DrawStyle.Stroke(
            width = 1.5.dp,
            strokeStyle = StrokeStyle.Normal
        ),
        val innerLabelStyle: TextStyle = TextStyle(
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        ),
        val show: (Pie)-> Boolean = { true },
        val innerLabelContent: PieInnerLabelContent = PieInnerLabelContent.Percentage(),
        val animationSpec: AnimationSpec<Float> = tween(300, delayMillis = 100)
    ): LabelMode
}
sealed interface PieInnerLabelContent{
    data class Percentage(val transform:(Float)->String = { it.format(1)+"%" }): PieInnerLabelContent
    data class Custom(val transform: (Pie) -> String?): PieInnerLabelContent
}

private data class PieDetails(
    val pie: Pie,
    val color: Animatable<Color, AnimationVector4D> = Animatable(pie.color),
    val scale: Animatable<Float, AnimationVector1D> = Animatable(1f),
    val space: Animatable<Float, AnimationVector1D> = Animatable(0f),
    val outerLabelLineProgress:Animatable<Float, AnimationVector1D> = Animatable(0f),
    val pieLabelAlpha:Animatable<Float, AnimationVector1D> = Animatable(0f),
)

private data class PiePiece(
    val id: String,
    val radius: Float,
    val startFromDegree: Float,
    val endToDegree: Float,
)
