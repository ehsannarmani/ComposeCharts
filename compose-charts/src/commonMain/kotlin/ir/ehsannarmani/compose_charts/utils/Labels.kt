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
    xPadding: Float
) {
    if (labelProperties.enabled && labels.isNotEmpty()) {
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
        var shouldRotate = labelProperties.rotation.mode == LabelProperties.Rotation.Mode.Force
        if ((maxLabelWidth / minLabelWidth.toDouble()) >= 1.5 && labelProperties.rotation.degree != 0f) {
            textModifier = textModifier.width((minLabelWidth / density.density).dp)
            shouldRotate = true
        }
        Row(
            modifier = widthModifier
                .padding(
                    start = (xPadding / density.density).dp,
                ), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            labels.forEachIndexed { index, label ->
                val modifier= if (shouldRotate) textModifier.graphicsLayer {
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
                    labelProperties.builder.invoke(modifier,label,shouldRotate,index)
                }else{
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

@Composable
fun VerticalLabels(
    labelProperties: LabelProperties,
    dataCount: Int,
    labels: List<String>,
    indicatorAreaHeight: Float,
    density: Density,
    indicatorProperties: VerticalIndicatorProperties,
    everyDataHeight: Float
) {
    if (labelProperties.enabled && labels.isNotEmpty()) {
        val paddingY = (indicatorAreaHeight / density.density).dp
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
            labels.forEachIndexed { index,label->
                if (labelProperties.builder != null){
                    labelProperties.builder.invoke(Modifier,label,false,index)
                }else{
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