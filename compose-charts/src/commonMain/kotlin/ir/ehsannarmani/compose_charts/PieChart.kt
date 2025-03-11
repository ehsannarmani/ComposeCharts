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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import ir.ehsannarmani.compose_charts.extensions.getAngleInDegree
import ir.ehsannarmani.compose_charts.extensions.isDegreeBetween
import ir.ehsannarmani.compose_charts.extensions.isInsideCircle
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
    appearanceAnimationSpec: AnimationSpec<Float> = tween(2000),
    style: Pie.Style = Pie.Style.Fill,
    centerTitle: String? = null, // 中心文本
    centerTextColor: Color = Color.Black, // 文本颜色
    centerTextStyle: TextStyle = TextStyle.Default // 文本样式
) {

    require(data.none { it.data < 0 }) {
        "Data must be at least 0"
    }

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

    val transitionProgress = remember(data) { Animatable(0f) }

    LaunchedEffect(Unit) {
        transitionProgress.snapTo(0f)
        transitionProgress.animateTo(
            targetValue = 1f,
            animationSpec = appearanceAnimationSpec
        )
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
        details.forEach { pieDetail ->
            if (pieDetail.pie.selected) {
                // 选中状态的动画
                pieDetail.color.animateTo(
                    pieDetail.pie.selectedColor,
                    animationSpec = pieDetail.pie.colorAnimEnterSpec ?: colorAnimEnterSpec
                )
                pieDetail.scale.animateTo(
                    pieDetail.pie.selectedScale ?: selectedScale,
                    animationSpec = pieDetail.pie.scaleAnimEnterSpec ?: scaleAnimEnterSpec
                )
                pieDetail.space.animateTo(
                    pieDetail.pie.selectedPaddingDegree ?: selectedPaddingDegree,
                    animationSpec = pieDetail.pie.spaceDegreeAnimEnterSpec ?: spaceDegreeAnimEnterSpec
                )
            } else {
                // 非选中状态的动画
                pieDetail.color.animateTo(
                    pieDetail.pie.color,
                    animationSpec = pieDetail.pie.colorAnimExitSpec ?: colorAnimExitSpec
                )
                pieDetail.scale.animateTo(
                    1f,
                    animationSpec = pieDetail.pie.scaleAnimExitSpec ?: scaleAnimExitSpec
                )
                pieDetail.space.animateTo(
                    0f,
                    animationSpec = pieDetail.pie.spaceDegreeAnimExitSpec ?: spaceDegreeAnimExitSpec
                )
            }
        }
    }

    val textMeasurer = rememberTextMeasurer()

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
        var currentAngle = 0f // Track the current angle for progressive animation
        
        details.forEachIndexed { index, detail ->
            val degree = ((detail.pie.data * 360) / total).toFloat()
            // Calculate the sweep angle based on both the progress and the actual degree
            val progress = transitionProgress.value
            val animatedDegree = degree * progress

            val drawStyle = if ((detail.pie.style ?: style) is Pie.Style.Stroke) {
                Stroke(width = ((detail.pie.style ?: style) as Pie.Style.Stroke).width.toPx())
            } else {
                Fill
            }

            val piecePath = if (animatedDegree >= 360f) {
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
                val startFromDegree = currentAngle // Use currentAngle instead of calculating from beforeItems

                val arcRect = Rect(
                    center = center,
                    radius = radius * detail.scale.value
                )

                val arcStart = startFromDegree + detail.space.value
                val arcSweep = (animatedDegree - ((detail.space.value * 2) + spaceDegree)).coerceAtLeast(0f)

                val piecePath = Path().apply {
                    // Move to center first for filled style
                    if ((detail.pie.style ?: style) is Pie.Style.Fill) {
                        moveTo(center.x, center.y)
                    }
                    // Draw the arc
                    arcTo(
                        rect = arcRect,
                        startAngleDegrees = arcStart,
                        sweepAngleDegrees = arcSweep,
                        forceMoveTo = true
                    )
                    // Complete the path for filled style
                    if ((detail.pie.style ?: style) is Pie.Style.Fill) {
                        lineTo(center.x, center.y)
                    }
                }

                pieces.add(
                    PiePiece(
                        id = detail.id,
                        radius = radius * detail.scale.value,
                        startFromDegree = arcStart,
                        endToDegree = if (arcStart + arcSweep >= 360f) 360f else arcStart + arcSweep
                    )
                )
                
                currentAngle += degree // Update the current angle for the next piece
                piecePath
            }

            // Animate both the path and the opacity
            val color = detail.color.value.copy(alpha = detail.color.value.alpha * progress)
            
            drawPath(
                path = piecePath,
                color = color,
                style = drawStyle,
            )
            // 绘制中心文本
            centerTitle?.let { title ->
                // 1. 截断字符长度
                val truncatedTitle = if (title.length > 8) title.take(8) else title

                // 2. 计算可用区域
                val availableDiameter = when (style) {
                    is Pie.Style.Stroke -> {
                        val strokeWidth = style.width.toPx()
                        minOf(size.width, size.height) - 2 * strokeWidth
                    }
                    else -> minOf(size.width, size.height) // Fill 样式按需调整
                }

                // 3. 动态调整字体大小
                // 3. 动态调整字体大小
                val maxTextWidth = availableDiameter * 0.9f
                var dynamicTextStyle = centerTextStyle.copy()

                // 确保 fontSize 的类型是明确的
                val initialFontSize = if (dynamicTextStyle.fontSize.type == TextUnitType.Unspecified) {
                    TextUnit(16f, TextUnitType.Sp) // 赋予默认值
                } else {
                    dynamicTextStyle.fontSize
                }
                dynamicTextStyle = dynamicTextStyle.copy(fontSize = initialFontSize)
                var textLayoutResult = textMeasurer.measure(
                    AnnotatedString(truncatedTitle),
                    dynamicTextStyle
                )

                if (textLayoutResult.size.width > maxTextWidth) {
                    val scale = maxTextWidth / textLayoutResult.size.width
                    dynamicTextStyle = dynamicTextStyle.copy(
                        fontSize = dynamicTextStyle.fontSize * scale
                    )
                    textLayoutResult = textMeasurer.measure(
                        AnnotatedString(truncatedTitle),
                        dynamicTextStyle
                    )
                }

                // 4. 绘制居中文本
                val textOffset = Offset(
                    x = center.x - textLayoutResult.size.width / 2,
                    y = center.y - textLayoutResult.size.height / 2
                )
                drawText(
                    textLayoutResult = textLayoutResult,
                    topLeft = textOffset,
                    color = centerTextColor
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
