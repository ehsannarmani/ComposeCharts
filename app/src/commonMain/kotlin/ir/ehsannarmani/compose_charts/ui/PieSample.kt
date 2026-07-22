package ir.ehsannarmani.compose_charts.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.ehsannarmani.compose_charts.LabelMode
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.PieInnerLabelContent
import ir.ehsannarmani.compose_charts.extensions.format
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.Pie
import ir.ehsannarmani.compose_charts.models.StrokeStyle

private const val unselectedPieAlpha = 1f
private val pieData = listOf(
    Pie(
        label = "Android",
        data = 20.0,
        color = Color(0xFFeb3b5a).copy(alpha = unselectedPieAlpha),
        selectedColor = Color(0xFFeb3b5a),
    ),
    Pie(
        label = "IOS",
        data = 14.0,
        color = Color(0xFFfa8231).copy(alpha = unselectedPieAlpha),
        selectedColor = Color(0xFFfa8231),
    ),
    Pie(
        label = "MacOS",
        data = 40.0,
        color = Color(0xFFf7b731).copy(alpha = unselectedPieAlpha),
        selectedColor = Color(0xFFf7b731),
    ),
    Pie(
        label = "Ubuntu",
        data = 10.0,
        color = Color(0xFF20bf6b).copy(alpha = unselectedPieAlpha),
        selectedColor = Color(0xFF20bf6b),
    ),
    Pie(
        label = "Manjaro",
        data = 9.0,
        color = Color(0xFF0fb9b1).copy(alpha = unselectedPieAlpha),
        selectedColor = Color(0xFF0fb9b1),
    ),
)

@Composable
fun PieSample(modifier: Modifier=Modifier) {
    var data by remember {
        mutableStateOf(pieData)
    }
    val selectedPie = data.find { it.selected }

    val floatSpec = spring<Float>(
        dampingRatio = .3f,
        stiffness = Spring.StiffnessLow
    )

    ChartParent(modifier=modifier){
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
            PieChart(
                modifier = Modifier
                    .fillMaxSize(),
                data = data,
                onPieClick = {
                    println("${it.label} Clicked")
                    val pieIndex = data.indexOf(it)
                    data =
                        data.mapIndexed { mapIndex, pie -> pie.copy(selected = pieIndex == mapIndex && !it.selected) }
                },
                selectedScale = 1.2f,
                spaceDegreeAnimEnterSpec = floatSpec,
                colorAnimEnterSpec = tween(300),
                scaleAnimEnterSpec = floatSpec,
                colorAnimExitSpec = tween(300),
                scaleAnimExitSpec = tween(300),
                spaceDegreeAnimExitSpec = tween(300),
                selectedPaddingDegree = 0f,
                style = Pie.Style.Fill,
                labelHelperProperties = labelHelperProperties,
                labelMode = LabelMode.OnPie(
                    show = { it.id == selectedPie?.id },
                    innerLabelContent = PieInnerLabelContent.Percentage {
                        it.format(1)+"%\nof total"
                    },
                )
            )
        }
    }
}
@Composable
fun PieSample2(modifier: Modifier=Modifier) {
    var data by remember {
        mutableStateOf(pieData)
    }

    val floatSpec = spring<Float>(
        dampingRatio = .3f,
        stiffness = Spring.StiffnessLow
    )

    ChartParent(modifier=modifier) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
            PieChart(
                modifier = Modifier
                    .fillMaxSize(),
                data = data,
                onPieClick = {
                    println("${it.label} Clicked")
                    val pieIndex = data.indexOf(it)
                    data =
                        data.mapIndexed { mapIndex, pie -> pie.copy(selected = pieIndex == mapIndex && !it.selected) }
                },
                selectedScale = 1.2f,
                spaceDegreeAnimEnterSpec = floatSpec,
                colorAnimEnterSpec = tween(300),
                scaleAnimEnterSpec = floatSpec,
                colorAnimExitSpec = tween(300),
                scaleAnimExitSpec = tween(300),
                spaceDegreeAnimExitSpec = tween(300),
                selectedPaddingDegree = 4f,
                style = Pie.Style.Stroke(width = 32.dp),
                labelHelperProperties = labelHelperProperties,
                labelMode = LabelMode.OnPie(
                    innerLabelStyle = TextStyle(
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                )
            )
        }
    }
}
@Composable
fun PieSample3(modifier: Modifier=Modifier) {
    var data by remember {
        mutableStateOf(pieData)
    }

    val floatSpec = spring<Float>(
        dampingRatio = .3f,
        stiffness = Spring.StiffnessLow
    )

    ChartParent(modifier=modifier) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
            PieChart(
                modifier = Modifier
                    .fillMaxSize(),
                data = data,
                onPieClick = {
                    println("${it.label} Clicked")
                    val pieIndex = data.indexOf(it)
                    data =
                        data.mapIndexed { mapIndex, pie -> pie.copy(selected = pieIndex == mapIndex && !it.selected) }
                },
                selectedScale = 1.2f,
                spaceDegreeAnimEnterSpec = floatSpec,
                colorAnimEnterSpec = tween(300),
                scaleAnimEnterSpec = floatSpec,
                colorAnimExitSpec = tween(300),
                scaleAnimExitSpec = tween(300),
                spaceDegreeAnimExitSpec = tween(300),
                selectedPaddingDegree = 4f,
                style = Pie.Style.Stroke(),
                spaceDegree = 7f,
                labelHelperProperties = labelHelperProperties,
                labelMode = LabelMode.OnPie(
                    innerLabelStyle = TextStyle(
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                )
            )
        }
    }
}