package ir.ehsannarmani.compose_charts.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.extensions.format
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.IndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.PopupProperties


val columnGridProperties = GridProperties(
    enabled = true,
    xAxisProperties = GridProperties.AxisProperties(thickness = .2.dp, color = SolidColor(Color.Gray.copy(alpha = .6f))),
    yAxisProperties = GridProperties.AxisProperties(thickness = .2.dp, color = SolidColor(Color.Gray.copy(alpha = .6f))),
)

@Composable
fun RowScope.ColumnSample() {

    val data = remember {
        listOf(
            Bars(
                label = "Jan",
                values = listOf(
                    Bars.Data(
                        label = "Linux",
                        value = -50.0,
                        color = Brush.verticalGradient(
                            listOf(
                                Color(0xFFE65100),
                                Color(0xFFFFD54F),
                            )
                        )
                    ),
                    Bars.Data(
                        label = "Windows",
                        value = 100.0,
                        color = Brush.verticalGradient(
                            listOf(
                                Color(0xFF7E57C2),
                                Color(0xFF7986CB),
                            )
                        )
                    ),
                )
            ),
            Bars(
                label = "Feb",
                values = listOf(
                    Bars.Data(
                        label = "Linux",
                        value = 25.0,
                        color = Brush.verticalGradient(
                            listOf(
                                Color(0xFFE65100),
                                Color(0xFFFFD54F),
                            )
                        )
                    ),
                    Bars.Data(
                        label = "Windows",
                        value = 45.0,
                        color = Brush.verticalGradient(
                            listOf(
                                Color(0xFF7E57C2),
                                Color(0xFF7986CB),
                            )
                        )
                    ),
                )
            ),
            Bars(
                label = "Mar",
                values = listOf(
                    Bars.Data(
                        label = "Linux",
                        value = 60.0,
                        color = Brush.verticalGradient(
                            listOf(
                                Color(0xFFE65100),
                                Color(0xFFFFD54F),
                            )
                        )
                    ),
                    Bars.Data(
                        label = "Windows",
                        value = 50.0,
                        color = Brush.verticalGradient(
                            listOf(
                                Color(0xFF7E57C2),
                                Color(0xFF7986CB),
                            )
                        )
                    ),
                )
            )
        )
    }
    Card(modifier=Modifier.height(270.dp).fillMaxWidth().weight(1f)
        .border(2.dp,Color.Transparent, RoundedCornerShape(12.dp)),
        elevation = CardDefaults.elevatedCardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xff2D2D2D)
        )
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(vertical = 12.dp)){
            ColumnChart(
                modifier= Modifier
                    .fillMaxSize()
                    .padding(horizontal = 22.dp)
                ,
                data = data,
                barProperties = BarProperties(
                    cornerRadius = Bars.Data.Radius.Rectangle(topRight = 6.dp, topLeft = 6.dp),
                    spacing = 3.dp,
                    thickness = 20.dp
                ),
                indicatorProperties = IndicatorProperties(
                    textStyle = TextStyle(fontSize = 12.sp, fontFamily = ubuntu, color = Color.White),
                    count = 4
                ),
                gridProperties = columnGridProperties,
                labelProperties = LabelProperties(
                    enabled = true,
                    textStyle = TextStyle(fontSize = 12.sp, fontFamily = ubuntu, color = Color.White)
                ),
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                animationMode = AnimationMode.Together(delayBuilder = {it*100L}),
                animationDelay = 300,
                popupProperties = PopupProperties(
                    textStyle = TextStyle(
                        fontSize = 11.sp,
                        color = Color.White,
                        fontFamily = ubuntu,
                    ),
                    contentBuilder = {
                        it.format(1) + " Million"
                    },
                    containerColor = Color(0xff414141),
                ),
                labelHelperProperties = LabelHelperProperties(textStyle = TextStyle(fontSize = 12.sp, fontFamily = ubuntu, color = Color.White)),
                onBarClick = {
                    println(it)
                },
                onBarLongClick = {
                    println("long: $it")
                }
            )
        }
    }
}
@Composable
fun RowScope.ColumnSample2() {

    val data = remember {
        listOf(
            Bars(
                label = "Jan",
                values = listOf(
                    Bars.Data(
                        label = "Linux",
                        value = 50.0,
                        color = Brush.verticalGradient(
                            listOf(
                                Color(0xFFE65100),
                                Color(0xFFFFD54F),
                            )
                        )
                    ),
                    Bars.Data(
                        label = "Windows",
                        value = 100.0,
                        color = Brush.verticalGradient(
                            listOf(
                                Color(0xFF7E57C2),
                                Color(0xFF7986CB),
                            )
                        )
                    ),
                    Bars.Data(
                        label = "Android",
                        value = 60.0,
                        color = Brush.verticalGradient(
                            listOf(
                                Color(0xFFB71C1C),
                                Color(0xFFEC407A),
                            )
                        )
                    ),
                )
            ),
            Bars(
                label = "Feb",
                values = listOf(
                    Bars.Data(
                        label = "Linux",
                        value = 25.0,
                        color = Brush.verticalGradient(
                            listOf(
                                Color(0xFFE65100),
                                Color(0xFFFFD54F),
                            )
                        )
                    ),
                    Bars.Data(
                        label = "Windows",
                        value = 50.0,
                        color = Brush.verticalGradient(
                            listOf(
                                Color(0xFF7E57C2),
                                Color(0xFF7986CB),
                            )
                        )
                    ),
                    Bars.Data(
                        label = "Android",
                        value = 45.0,
                        color = Brush.verticalGradient(
                            listOf(
                                Color(0xFFB71C1C),
                                Color(0xFFEC407A),
                            )
                        )
                    ),
                )
            ),
            Bars(
                label = "Mar",
                values = listOf(
                    Bars.Data(
                        label = "Linux",
                        value = 60.0,
                        color = Brush.verticalGradient(
                            listOf(
                                Color(0xFFE65100),
                                Color(0xFFFFD54F),
                            )
                        )
                    ),
                    Bars.Data(
                        label = "Windows",
                        value = 50.0,
                        color = Brush.verticalGradient(
                            listOf(
                                Color(0xFF7E57C2),
                                Color(0xFF7986CB),
                            )
                        )
                    ),
                    Bars.Data(
                        label = "Android",
                        value = 90.0,
                        color = Brush.verticalGradient(
                            listOf(
                                Color(0xFFB71C1C),
                                Color(0xFFEC407A),
                            )
                        )
                    ),
                )
            ),
            Bars(
                label = "Apr",
                values = listOf(
                    Bars.Data(
                        label = "Linux",
                        value = 20.0,
                        color = Brush.verticalGradient(
                            listOf(
                                Color(0xFFE65100),
                                Color(0xFFFFD54F),
                            )
                        )
                    ),
                    Bars.Data(
                        label = "Windows",
                        value = 32.0,
                        color = Brush.verticalGradient(
                            listOf(
                                Color(0xFF7E57C2),
                                Color(0xFF7986CB),
                            )
                        )
                    ),
                    Bars.Data(
                        label = "Android",
                        value = 76.0,
                        color = Brush.verticalGradient(
                            listOf(
                                Color(0xFFB71C1C),
                                Color(0xFFEC407A),
                            )
                        )
                    ),
                )
            )
        )
    }
    Card(modifier=Modifier.height(270.dp).fillMaxWidth().weight(1f)
        .border(2.dp,Color.Transparent, RoundedCornerShape(12.dp)),
        elevation = CardDefaults.elevatedCardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xff2D2D2D)
        )
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(vertical = 12.dp)){
            ColumnChart(
                modifier= Modifier
                    .fillMaxSize()
                    .padding(horizontal = 22.dp)
                ,
                data = data,
                barProperties = BarProperties(
                    cornerRadius = Bars.Data.Radius.Rectangle(topRight = 6.dp, topLeft = 6.dp),
                    spacing = 1.dp,
                    thickness = 5.dp
                ),
                indicatorProperties = IndicatorProperties(
                    textStyle = TextStyle(fontSize = 12.sp, fontFamily = ubuntu, color = Color.White),
                    count = 4
                ),
                gridProperties = columnGridProperties,
                labelProperties = LabelProperties(
                    enabled = true,
                    textStyle = TextStyle(fontSize = 12.sp, fontFamily = ubuntu, color = Color.White)
                ),
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                animationMode = AnimationMode.Together(delayBuilder = {it*100L}),
                animationDelay = 300,
                popupProperties = PopupProperties(
                    textStyle = TextStyle(
                        fontSize = 11.sp,
                        color = Color.White,
                        fontFamily = ubuntu,
                    ),
                    contentBuilder = {
                        it.format(1) + " Million"
                    },
                    containerColor = Color(0xff414141),
                ),
                labelHelperProperties = LabelHelperProperties(textStyle = TextStyle(fontSize = 12.sp, fontFamily = ubuntu, color = Color.White))
            )
        }
    }
}
@Composable
fun RowScope.ColumnSample3() {

    val positiveBarColor = SolidColor(Color(0xFF42A5F5))
    val negativeBarColor = SolidColor(Color(0x9742A5F5))
    val data = remember {
        MutableList(20){
            val value = (-50..40).random().toDouble()
            Bars(
                label = (it+1).toString(),
                values = listOf(
                    Bars.Data(
                        value = value,
                        color = if (value < 0) negativeBarColor else positiveBarColor ,
                    ),
                )
            )
        }
    }
    Card(modifier=Modifier.height(270.dp).fillMaxWidth().weight(1f)
        .border(2.dp,Color.Transparent, RoundedCornerShape(12.dp)),
        elevation = CardDefaults.elevatedCardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xff2D2D2D)
        )
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(vertical = 12.dp)){
            ColumnChart(
                modifier= Modifier
                    .fillMaxSize()
                    .padding(horizontal = 22.dp),
                data = data,
                barProperties = BarProperties(
                    spacing = 1.dp,
                    thickness = 10.dp,
                ),
                indicatorProperties = IndicatorProperties(
                    textStyle = TextStyle(fontSize = 12.sp, fontFamily = ubuntu, color = Color.White),
                    count = 4,
                ),
                gridProperties = columnGridProperties,
                labelProperties = LabelProperties(
                    enabled = true,
                    textStyle = TextStyle(fontSize = 11.sp, fontFamily = ubuntu, color = Color.White)
                ),
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                animationMode = AnimationMode.Together(delayBuilder = {it*100L}),
                popupProperties = PopupProperties(
                    textStyle = TextStyle(
                        fontSize = 11.sp,
                        color = Color.White,
                        fontFamily = ubuntu,
                    ),
                    contentBuilder = {
                        it.format(1) + " Million"
                    },
                    containerColor = Color(0xff414141),
                ),
                animationDelay = 300,
                labelHelperProperties = LabelHelperProperties(enabled = false, textStyle = TextStyle(fontSize = 12.sp, fontFamily = ubuntu, color = Color.White)),
                minValue = -75.0,
                maxValue = 75.0
            )
        }
    }
}