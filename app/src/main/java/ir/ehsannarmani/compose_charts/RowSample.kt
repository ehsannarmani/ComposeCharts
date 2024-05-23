package ir.ehsannarmani.compose_charts

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.IndicatorProperties
import ir.ehsannarmani.compose_charts.models.StrokeStyle

@Composable
fun RowSample() {

    val data = remember {
        mutableStateOf(generateRowBarData())
    }

    Column(modifier=Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        RowChart(
            modifier= Modifier
                .fillMaxWidth()
                .height(270.dp)
                .padding(horizontal = 22.dp)
            ,
            data = data.value,
            barProperties = BarProperties(
                radius = Bars.Data.Radius.Rectangle(topRight = 6.dp, bottomRight = 6.dp),
                spacing = 4.dp,
                strokeWidth = 20.dp,
            ),
            indicatorProperties = IndicatorProperties(
                textStyle = TextStyle(fontSize = 12.sp),
                count = 4
            ),
            gridProperties = GridProperties(
                enabled = true,
                strokeWidth = .2.dp
            ),
            labelStyle = TextStyle(fontSize = 12.sp),
            animationSpec = spring(
                dampingRatio = .6f,
                stiffness = Spring.StiffnessLow
            ),
            animationMode = AnimationMode.Together(delayBuilder = {it*100L}),
            animationDelay = 300,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            data.value = generateRowBarData()
        }, shape = RoundedCornerShape(8.dp)) {
            Text(text = "Show Animation")
        }
    }
}