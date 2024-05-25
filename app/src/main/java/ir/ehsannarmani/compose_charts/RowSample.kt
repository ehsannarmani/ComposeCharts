package ir.ehsannarmani.compose_charts

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
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.PopupProperties
import ir.ehsannarmani.compose_charts.ui.theme.ubuntu

@Composable
fun RowScope.RowSample() {

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
            RowChart(
                modifier= Modifier
                    .fillMaxSize()
                    .padding(horizontal = 22.dp)
                ,
                data = data,
                barProperties = BarProperties(
                    cornerRadius = Bars.Data.Radius.Rectangle(topRight = 6.dp, bottomRight = 6.dp),
                    spacing = 4.dp,
                    thickness = 20.dp,
                ),
                indicatorProperties = IndicatorProperties(
                    textStyle = TextStyle(fontSize = 12.sp, fontFamily = ubuntu, color = Color.White),
                    count = 4
                ),
                gridProperties = GridProperties(
                    enabled = true,
                    thickness = .2.dp
                ),
                labelStyle = TextStyle(fontSize = 12.sp, fontFamily = ubuntu, color = Color.White),
                animationSpec = spring(
                    dampingRatio = .6f,
                    stiffness = Spring.StiffnessLow
                ),
                animationMode = AnimationMode.Together(delayBuilder = {it*100L}),
                animationDelay = 300,
                labelHelperProperties = LabelHelperProperties(textStyle = LocalTextStyle.current.copy(
                    fontSize = 12.sp,
                    fontFamily = ubuntu,
                    color = Color.White
                )),
                popupProperties = PopupProperties(
                    textStyle = TextStyle(
                        fontSize = 11.sp,
                        color = Color.White,
                        fontFamily = ubuntu,
                    ),
                    contentBuilder = {
                        "%.1f".format(it) + " Million"
                    },
                    containerColor = Color(0xff414141),
                ),
            )
        }
    }
}
@Composable
fun RowScope.RowSample2() {

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
            RowChart(
                modifier= Modifier
                    .fillMaxSize()
                    .padding(horizontal = 22.dp)
                ,
                data = data,
                barProperties = BarProperties(
                    cornerRadius = Bars.Data.Radius.Rectangle(topRight = 6.dp, bottomRight = 6.dp),
                    spacing = 1.dp,
                    thickness = 5.dp
                ),
                indicatorProperties = IndicatorProperties(
                    textStyle = TextStyle(fontSize = 12.sp, fontFamily = ubuntu, color = Color.White),
                    count = 4
                ),
                gridProperties = GridProperties(enabled = true, thickness = (.2).dp),
                labelStyle = TextStyle(fontSize = 12.sp, fontFamily = ubuntu, color = Color.White),
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                animationMode = AnimationMode.Together(delayBuilder = {it*100L}),
                animationDelay = 300,
                labelHelperProperties = LabelHelperProperties(textStyle = LocalTextStyle.current.copy(
                    fontSize = 12.sp,
                    fontFamily = ubuntu,
                    color = Color.White
                )),
                popupProperties = PopupProperties(
                    textStyle = TextStyle(
                        fontSize = 11.sp,
                        color = Color.White,
                        fontFamily = ubuntu,
                    ),
                    contentBuilder = {
                        "%.1f".format(it) + " Million"
                    },
                    containerColor = Color(0xff414141),
                ),
            )
        }
    }
}
@Composable
fun RowScope.RowSample3() {
    val barColor = SolidColor(Color(0xFF42A5F5))
    val data = remember {
        MutableList(10){
            Bars(
                label = (it+1).toString(),
                values = listOf(
                    Bars.Data(
                        value = (10..80).random().toDouble(),
                        color = barColor,
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
            RowChart(
                modifier= Modifier
                    .fillMaxSize()
                    .padding(horizontal = 22.dp)
                ,
                data = data,
                barProperties = BarProperties(
                    spacing = 1.dp,
                    thickness = 10.dp,
                ),
                indicatorProperties = IndicatorProperties(
                    textStyle = TextStyle(fontSize = 12.sp, fontFamily = ubuntu, color = Color.White),
                    count = 4,

                ),
                gridProperties = GridProperties(enabled = true, thickness = (.2).dp),
                labelStyle = TextStyle(fontSize = 12.sp, fontFamily = ubuntu, color = Color.White),
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                animationMode = AnimationMode.Together(delayBuilder = {it*100L}),
                animationDelay = 300,
                labelHelperProperties = LabelHelperProperties(textStyle = LocalTextStyle.current.copy(
                    fontSize = 12.sp,
                    fontFamily = ubuntu,
                    color = Color.White
                ), enabled = false),
                popupProperties = PopupProperties(
                    textStyle = TextStyle(
                        fontSize = 11.sp,
                        color = Color.White,
                        fontFamily = ubuntu,
                    ),
                    contentBuilder = {
                        "%.1f".format(it) + " Million"
                    },
                    containerColor = Color(0xff414141),
                ),
            )
        }
    }
}