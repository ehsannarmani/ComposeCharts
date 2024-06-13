package ir.ehsannarmani.compose_charts.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.Pie

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
fun RowScope.PieSample() {
    var data by remember {
        mutableStateOf(pieData)
    }

    val floatSpec = spring<Float>(
        dampingRatio = .3f,
        stiffness = Spring.StiffnessLow
    )

    Card(modifier= Modifier
        .height(270.dp)
        .fillMaxWidth()
        .weight(1f)
        .border(2.dp, Color.Transparent, RoundedCornerShape(12.dp)),
        elevation = CardDefaults.elevatedCardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xff2D2D2D)
        )

    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
            PieChart(
                modifier = Modifier
                    .size(200.dp),
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
    }
}
@Composable
fun RowScope.PieSample2() {
    var data by remember {
        mutableStateOf(pieData)
    }

    val floatSpec = spring<Float>(
        dampingRatio = .3f,
        stiffness = Spring.StiffnessLow
    )

    Card(modifier= Modifier
        .height(270.dp)
        .fillMaxWidth()
        .weight(1f)
        .border(2.dp, Color.Transparent, RoundedCornerShape(12.dp))
,
        elevation = CardDefaults.elevatedCardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xff2D2D2D)
        )

    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
            PieChart(
                modifier = Modifier
                    .size(200.dp),
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
                style = Pie.Style.Stroke(width = 32.dp)
            )
        }
    }
}
@Composable
fun RowScope.PieSample3() {
    var data by remember {
        mutableStateOf(pieData)
    }

    val floatSpec = spring<Float>(
        dampingRatio = .3f,
        stiffness = Spring.StiffnessLow
    )

    Card(modifier= Modifier
        .height(270.dp)
        .fillMaxWidth()
        .weight(1f)
        .border(2.dp, Color.Transparent, RoundedCornerShape(12.dp)),
        elevation = CardDefaults.elevatedCardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xff2D2D2D)
        )

    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
            PieChart(
                modifier = Modifier
                    .size(200.dp),
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
                style = Pie.Style.Stroke(),
                spaceDegree = 7f
            )
        }
    }
}