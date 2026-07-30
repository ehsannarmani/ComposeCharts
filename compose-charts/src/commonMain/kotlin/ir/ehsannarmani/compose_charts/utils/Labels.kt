package ir.ehsannarmani.compose_charts.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import ir.ehsannarmani.compose_charts.extensions.spaceBetween
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.IndicatorPosition
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.VerticalIndicatorProperties

@Composable
fun HorizontalLabels(
    labelProperties: LabelProperties,
    labels: List<String>,
    indicatorProperties: HorizontalIndicatorProperties,
    chartWidth: Float,
    density: Density,
    textMeasurer: TextMeasurer,
    xPadding: Float,
    everyDataWidth: Float = 0f,
    averageSpacingBetweenBars: Double = 0.0
) {
    if (labelProperties.enabled && labels.isNotEmpty()) {
        Column {
            Spacer(modifier = Modifier.height(labelProperties.padding))

            val widthModifier =
                if (indicatorProperties.position == IndicatorPosition.Horizontal.End) {
                    Modifier.width((chartWidth / density.density).dp)
                } else {
                    Modifier.fillMaxWidth()
                }

            val labelMeasures =
                labels.map {
                    textMeasurer.measure(
                        it,
                        style = labelProperties.textStyle,
                        maxLines = 1
                    )
                }
            val labelWidths = labelMeasures.map { it.size.width }
            val maxLabelWidth = labelWidths.max()
            val minLabelWidth = labelWidths.min()

            var textModifier: Modifier = Modifier

            val shouldCompact = (maxLabelWidth / minLabelWidth.toDouble()) >= 1.5 && labelProperties.rotation.degree != 0f
            val shouldRotate = when(labelProperties.rotation.mode){
                LabelProperties.Rotation.Mode.Force -> true
                LabelProperties.Rotation.Mode.Never -> false
                LabelProperties.Rotation.Mode.IfNecessary -> shouldCompact
            }
            if (shouldCompact) {
                textModifier = textModifier.width((minLabelWidth / density.density).dp)
            }

            if (everyDataWidth > 0f && labels.isNotEmpty()) {
                Box(
                    modifier = widthModifier
                        .padding(
                            start = (xPadding / density.density).dp,
                        )
                ) {
                    labels.forEachIndexed { index, label ->
                        val labelWidth = labelMeasures[index].size.width.toFloat()
                        val groupX = (chartWidth - everyDataWidth).spaceBetween(labels.count(), index) + (averageSpacingBetweenBars / 2).toFloat()
                        val groupCenterX = groupX + (everyDataWidth / 2f)
                        val labelLeftPx = groupCenterX - (labelWidth / 2f)
                        val labelOffsetDp = (labelLeftPx / density.density).dp

                        val baseModifier = textModifier.offset(x = labelOffsetDp)
                        val modifier = if (shouldRotate) baseModifier.graphicsLayer {
                            rotationZ = labelProperties.rotation.degree
                            transformOrigin =
                                TransformOrigin(
                                    (labelMeasures[index].size.width / minLabelWidth.toFloat()),
                                    .5f
                                )
                            translationX =
                                (-(labelMeasures[index].size.width - minLabelWidth.toFloat())) - (labelProperties.rotation.padding?.toPx()
                                    ?: (minLabelWidth / 2f))
                        } else baseModifier

                        if (labelProperties.builder != null) {
                            labelProperties.builder.invoke(modifier, label, shouldRotate, index)
                        } else {
                            BasicText(
                                modifier = modifier,
                                text = label,
                                style = labelProperties.textStyle,
                                overflow = if (shouldRotate) TextOverflow.Visible else TextOverflow.Clip,
                                softWrap = !shouldRotate,
                            )
                        }
                    }
                }
            } else {
                Row(
                    modifier = widthModifier
                        .padding(
                            start = (xPadding / density.density).dp,
                        ), horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    labels.forEachIndexed { index, label ->
                        val modifier = if (shouldRotate) textModifier.graphicsLayer {
                            rotationZ = labelProperties.rotation.degree
                            transformOrigin =
                                TransformOrigin(
                                    (labelMeasures[index].size.width / minLabelWidth.toFloat()),
                                    .5f
                                )
                            translationX =
                                (-(labelMeasures[index].size.width - minLabelWidth.toFloat())) - (labelProperties.rotation.padding?.toPx()
                                    ?: (minLabelWidth / 2f))
                        } else textModifier
                        if (labelProperties.builder != null) {
                            labelProperties.builder.invoke(modifier, label, shouldRotate, index)
                        } else {
                            BasicText(
                                modifier = modifier,
                                text = label,
                                style = labelProperties.textStyle,
                                overflow = if (shouldRotate) TextOverflow.Visible else TextOverflow.Clip,
                                softWrap = !shouldRotate,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VerticalLabels(
    labelProperties: LabelProperties,
    dataCount: Int,
    labels: List<String>,
    indicatorAreaHeight: Float,
    density: Density,
    indicatorProperties: VerticalIndicatorProperties,
    everyDataHeight: Float,
    chartHeight: Float = 0f,
    averageSpacingBetweenBars: Double = 0.0,
    textMeasurer: TextMeasurer? = null
) {
    if (labelProperties.enabled && labels.isNotEmpty()) {
        val paddingY = (indicatorAreaHeight / density.density).dp

        if (chartHeight > 0f && everyDataHeight > 0f) {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(IntrinsicSize.Max)
            ) {
                Box(
                    modifier = Modifier
                        .height((chartHeight / density.density).dp)
                        .width(IntrinsicSize.Max)
                ) {
                    labels.forEachIndexed { index, label ->
                        val labelHeight = textMeasurer?.measure(
                            label,
                            style = labelProperties.textStyle,
                            maxLines = 1
                        )?.size?.height?.toFloat()
                            ?: 0f

                        val drawYPadding = if (indicatorProperties.position == IndicatorPosition.Vertical.Top) indicatorAreaHeight else 0f
                        val groupY = (chartHeight - everyDataHeight).spaceBetween(labels.count(), index) + (averageSpacingBetweenBars / 2).toFloat() + drawYPadding
                        val groupCenterY = groupY + (everyDataHeight / 2f)
                        val labelTopPx = groupCenterY - (labelHeight / 2f)
                        val labelTopDp = (labelTopPx / density.density).dp

                        val modifier = Modifier
                            .offset(y = labelTopDp)
                            .fillMaxWidth()

                        if (labelProperties.builder != null) {
                            labelProperties.builder.invoke(modifier, label, false, index)
                        } else {
                            BasicText(
                                text = label,
                                style = labelProperties.textStyle,
                                modifier = modifier
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(labelProperties.padding))
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(IntrinsicSize.Max)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(IntrinsicSize.Max)
                        .padding(
                            top = if (indicatorProperties.position == IndicatorPosition.Vertical.Top) paddingY else 0.dp,
                            bottom = if (indicatorProperties.position == IndicatorPosition.Vertical.Bottom) paddingY else 0.dp
                        )
                        .padding(vertical = (((everyDataHeight) / dataCount) / density.density).dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    labels.forEachIndexed { index, label ->
                        if (labelProperties.builder != null) {
                            labelProperties.builder.invoke(Modifier, label, false, index)
                        } else {
                            BasicText(
                                text = label,
                                style = labelProperties.textStyle,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(labelProperties.padding))
            }
        }
    }
}