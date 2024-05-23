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
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.IndicatorProperties

fun generateColumnBarData():List<Bars>{
    return MutableList(3){
        Bars(
            label = "Jan",
            values = listOf(
                Bars.Data(
                    label = "Linux",
                    value = (0..100).random().toDouble(),
                    color = Brush.verticalGradient(
                        listOf(
                            Color(0xFFE65100),
                            Color(0xFFFFD54F),
                        )
                    )
                ),
                Bars.Data(
                    label = "Windows",
                    value = (0..100).random().toDouble(),
                    color = Brush.verticalGradient(
                        listOf(
                            Color(0xFF7E57C2),
                            Color(0xFF7986CB),
                        )
                    )
                ),
            )
        )
    }
}
fun generateRowBarData():List<Bars>{
    return MutableList(3){
        Bars(
            label = "Jan",
            values = listOf(
                Bars.Data(
                    label = "Linux",
                    value = (0..100).random().toDouble(),
                    color = Brush.horizontalGradient(
                        listOf(
                            Color(0xFFFFD54F),
                            Color(0xFFE65100),
                        )
                    )
                ),
                Bars.Data(
                    label = "Windows",
                    value = (0..100).random().toDouble(),
                    color = Brush.horizontalGradient(
                        listOf(
                            Color(0xFF7986CB),
                            Color(0xFF7E57C2),
                        )
                    )
                ),
            )
        )
    }
}

@Composable
fun ColumnSample() {

    val data = remember {
        mutableStateOf(generateColumnBarData())
    }

    Column(modifier=Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        ColumnChart(
            modifier= Modifier
                .fillMaxWidth()
                .height(270.dp)
                .padding(horizontal = 22.dp)
            ,
            data = data.value,
            barProperties = BarProperties(
                radius = Bars.Data.Radius.Rectangle(topRight = 6.dp, topLeft = 6.dp),
                spacing = 3.dp,
                strokeWidth = 20.dp
            ),
            indicatorProperties = IndicatorProperties(
                textStyle = TextStyle(fontSize = 12.sp),
                count = 4
            ),
            gridProperties = GridProperties(enabled = true, strokeWidth = (.2).dp),
            labelStyle = TextStyle(fontSize = 12.sp),
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            animationMode = AnimationMode.Together(delayBuilder = {it*100L}),
            animationDelay = 300,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            data.value = generateColumnBarData()
        }, shape = RoundedCornerShape(8.dp)) {
            Text(text = "Show Animation")
        }
    }
}