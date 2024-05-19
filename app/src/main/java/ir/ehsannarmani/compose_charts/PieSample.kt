package ir.ehsannarmani.compose_charts

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ir.ehsannarmani.compose_charts.models.Pie

private const val unselectedPieAlpha = .6f
private val pieData = listOf(
    Pie(
        label = "Android",
        data = 20.0,
        color = Color(0xFFFFA726).copy(alpha = unselectedPieAlpha),
        selectedColor = Color(0xFFFFA726),
    ),
    Pie(
        label = "IOS",
        data = 14.0,
        color = Color(0xFFAB47BC).copy(alpha = unselectedPieAlpha),
        selectedColor = Color(0xFFAB47BC),
    ),
    Pie(
        label = "MacOS",
        data = 40.0,
        color = Color(0xFF42A5F5).copy(alpha = unselectedPieAlpha),
        selectedColor = Color(0xFF42A5F5),
    ),
    Pie(
        label = "Ubuntu",
        data = 10.0,
        color = Color(0xFFEC407A).copy(alpha = unselectedPieAlpha),
        selectedColor = Color(0xFFEC407A),
    ),
    Pie(
        label = "Manjaro",
        data = 9.0,
        color = Color(0xFF9CCC65).copy(alpha = unselectedPieAlpha),
        selectedColor = Color(0xFF9CCC65),
    ),
)

@Composable
fun PieSample() {
    var data by remember {
        mutableStateOf(pieData)
    }

    val floatSpec = spring<Float>(
        dampingRatio = .3f,
        stiffness = Spring.StiffnessLow
    )

    PieChart(
        modifier = Modifier
            .size(220.dp)
            .padding(38.dp),
        data = data,
        onPieClick = {
            println("${it.label} Clicked")
            val pieIndex = data.indexOf(it)
            data =
                data.mapIndexed { mapIndex, pie -> pie.copy(selected = pieIndex == mapIndex) }
        },
        selectedScale = 1.2f,
        spaceDegreeAnimEnterSpec = floatSpec,
        colorAnimEnterSpec = tween(300),
        scaleAnimEnterSpec = floatSpec,
        colorAnimExitSpec = tween(300),
        scaleAnimExitSpec = tween(300),
        spaceDegreeAnimExitSpec = tween(300),
        selectedPaddingDegree = 0f,
        style = Pie.Style.Fill
    )
}
@Composable
fun PieSample2() {
    var data by remember {
        mutableStateOf(pieData)
    }

    val floatSpec = spring<Float>(
        dampingRatio = .3f,
        stiffness = Spring.StiffnessLow
    )

    PieChart(
        modifier = Modifier
            .size(220.dp)
            .padding(22.dp),
        data = data,
        onPieClick = {
            println("${it.label} Clicked")
            val pieIndex = data.indexOf(it)
            data =
                data.mapIndexed { mapIndex, pie -> pie.copy(selected = pieIndex == mapIndex) }
        },
        selectedScale = 1.2f,
        spaceDegreeAnimEnterSpec = floatSpec,
        colorAnimEnterSpec = tween(300),
        scaleAnimEnterSpec = floatSpec,
        colorAnimExitSpec = tween(300),
        scaleAnimExitSpec = tween(300),
        spaceDegreeAnimExitSpec = tween(300),
        selectedPaddingDegree = 4f,
        style = Pie.Style.Stroke(120f)
    )
}