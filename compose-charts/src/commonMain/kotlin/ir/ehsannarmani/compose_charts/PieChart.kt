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
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ir.ehsannarmani.compose_charts.components.LabelHelper
import ir.ehsannarmani.compose_charts.extensions.getAngleInDegree
import ir.ehsannarmani.compose_charts.extensions.isDegreeBetween
import ir.ehsannarmani.compose_charts.extensions.isInsideCircle
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.Pie
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun PieChart(
    modifier: Modifier = Modifier,
    data: List<Pie>,
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
    style: Pie.Style = Pie.Style.Fill
) {

    require(data.none { it.data < 0 }) {
        "Data must be at least 0"
    }

    val onPieClick by rememberUpdatedState(onPieClick)
    val scope = rememberCoroutineScope()

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
                        id = details[mapIndex].id,
                        pie = chart,
                        scale = details[mapIndex].scale,
                        color = details[mapIndex].color,
                        space = details[mapIndex].space
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
        }
    }

    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (labelHelperProperties.enabled) {
            data.mapNotNull { pie -> pie.label?.let { pie.label to SolidColor(pie.color) } }
                .takeIf { it.isNotEmpty() }
                ?.let {
                    LabelHelper(data = it, properties = labelHelperProperties)
                    Spacer(modifier = Modifier.height(labelHelperPadding))
                }
        }
        Canvas(modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val angleInDegree = getAngleInDegree(
                        touchTapOffset = offset,
                        pieceOffset = pieChartCenter
                    )

                    pieces.firstOrNull { piece ->
                        isDegreeBetween(angleInDegree, piece.startFromDegree, piece.endToDegree)
                                && isInsideCircle(offset, pieChartCenter, piece.radius) }
                        ?.let {
                            val (id, _) = it
                            details.find { it.id == id }
                                ?.let {
                                    onPieClick(it.pie)
                                }
                        }
                }
            }
        ) {
            pieChartCenter = center

            val radius: Float = when (style) {
                is Pie.Style.Fill -> {
                    (minOf(size.width, size.height) / 2)
                }

                is Pie.Style.Stroke -> {
                    (minOf(size.width, size.height) / 2) - (style.width.toPx() / 2)
                }
            }
            val total = details.sumOf { it.pie.data } // 360 degree for total
            details.forEachIndexed { index, detail ->
                val degree = ((detail.pie.data * 360) / total)

                val drawStyle = if ((detail.pie.style ?: style) is Pie.Style.Stroke) {
                    Stroke(width = ((detail.pie.style ?: style) as Pie.Style.Stroke).width.toPx())
                } else {
                    Fill
                }
                val piecePath = if (degree >= 360.0) {
                    // draw circle instead of arc

                    pieces.add(
                        PiePiece(
                            id = detail.id,
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
                    val startFromDegree = beforeItems.sumOf { (it.data * 360) / total }

                    val arcRect = Rect(
                        center = center,
                        radius = radius * detail.scale.value
                    )

                    val arcStart = startFromDegree.toFloat() + detail.space.value
                    val arcSweep = degree.toFloat() - ((detail.space.value * 2) + spaceDegree)

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
                            id = detail.id,
                            radius = radius * detail.scale.value,
                            startFromDegree = arcStart,
                            endToDegree = if (arcStart + arcSweep >= 360f) 360f else arcStart + arcSweep,
                        )
                    )
                    piecePath
                }

                drawPath(
                    path = piecePath,
                    color = detail.color.value,
                    style = drawStyle,
                )
            }
        }
    }
}


private data class PieDetails(
    val id: String = Random.nextInt(0, 999999).toString(),
    val pie: Pie,
    val color: Animatable<Color, AnimationVector4D> = Animatable(pie.color),
    val scale: Animatable<Float, AnimationVector1D> = Animatable(1f),
    val space: Animatable<Float, AnimationVector1D> = Animatable(0f)
)

private data class PiePiece(
    val id: String,
    val radius: Float,
    val startFromDegree: Float,
    val endToDegree: Float,
)
