package ir.ehsannarmani.compose_charts.ui.theme

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.ehsannarmani.compose_charts.ColumnChart

private fun generateColumnData():List<ColumnChart>{
    return MutableList(3){
        ColumnChart(
            label = "Jan",
            values = listOf(
                ColumnChart.Data(
                    label = "Linux",
                    value = (0..100).random().toDouble(),
                    color = Color(0xFFFFCA28)
                ),
                ColumnChart.Data(
                    label = "Windows",
                    value = (0..100).random().toDouble(),
                    color = Color(0xFF5C6BC0)
                ),
            )
        )
    }
}

@Composable
fun ColumnSample() {

    val data = remember {
        mutableStateOf(generateColumnData())
    }

    Column(modifier=Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        ColumnChart(
            modifier= Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(22.dp)
            ,
            data = data.value,
            barsRadius = ColumnChart.Data.Radius.Rectangle(topRight = 6.dp, topLeft = 6.dp),
            indicatorStyle = TextStyle(fontSize = 12.sp),
            labelStyle = TextStyle(fontSize = 12.sp),
            drawGrid = true,
            gridStroke = (.2).dp,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            animationMode = ColumnChart.AnimationMode.Together(delayBuilder = {it*100L}),
            animationDelay = 300,
            indicatorCount = 4,
            barsSpacing = 4.dp,
            barsWidth = 20.dp,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            data.value = generateColumnData()
        }, shape = RoundedCornerShape(8.dp)) {
            Text(text = "Show Animation")
        }
    }
}