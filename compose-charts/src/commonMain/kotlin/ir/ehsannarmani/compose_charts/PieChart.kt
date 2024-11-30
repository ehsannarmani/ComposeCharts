package ir.ehsannarmani.compose_charts

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import ir.ehsannarmani.compose_charts.models.Pie
import kotlinx.coroutines.launch
import kotlin.math.atan2
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
    style: Pie.Style = Pie.Style.Fill
) {

    require(data.none { it.data < 0 }) {
        "Data must be at least 0"
    }

    val scope = rememberCoroutineScope()

    var details by remember {
        mutableStateOf(emptyList<PieDetails>())
    }
    val pieces = remember {
        mutableListOf<Pair<String, Rect>>()
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

    Canvas(modifier = modifier
        .pointerInput(Unit) {
            detectTapGestures { offset ->
                pieces
                    .firstOrNull { it.second.contains(offset) }
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
                piecePath
            }
            val rect = piecePath.getBounds()

            pieces.add(detail.id to rect)
            drawPath(
                path = piecePath,
                color = detail.color.value,
                style = drawStyle,
            )
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