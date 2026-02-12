package ir.ehsannarmani.compose_charts.ui

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.extensions.format
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.DividerProperties
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.LineProperties
import ir.ehsannarmani.compose_charts.models.PopupProperties
import ir.ehsannarmani.compose_charts.models.StrokeStyle
import ir.ehsannarmani.compose_charts.models.ZeroLineProperties

val gridProperties = GridProperties(
    xAxisProperties = GridProperties.AxisProperties(
        thickness = .2.dp,
        color = SolidColor(Color.Gray.copy(alpha = .5f)),
        style = StrokeStyle.Dashed(intervals = floatArrayOf(15f,15f), phase = 10f),
    ),
    yAxisProperties = GridProperties.AxisProperties(
        thickness = .2.dp,
        color = SolidColor(Color.Gray.copy(alpha = .5f)),
        style = StrokeStyle.Dashed(intervals = floatArrayOf(15f,15f), phase = 10f),
    ),
)
val dividerProperties = DividerProperties(
    xAxisProperties = LineProperties(
        thickness = .2.dp,
        color = SolidColor(Color.Gray.copy(alpha = .5f)),
        style = StrokeStyle.Dashed(intervals = floatArrayOf(15f,15f), phase = 10f),
    ),
    yAxisProperties = LineProperties(
        thickness = .2.dp,
        color = SolidColor(Color.Gray.copy(alpha = .5f)),
        style = StrokeStyle.Dashed(intervals = floatArrayOf(15f,15f), phase = 10f),
    )
)
@Composable
fun LineSample(modifier: Modifier=Modifier) {
    val popupProperties = PopupProperties(
        textStyle = TextStyle(
            fontSize = 11.sp,
            color = Color.White,
            fontFamily = ubuntu
        ),
        contentBuilder = { popup ->
            popup.value.format(1) + " Million, lineIndex: ${popup.dataIndex}, valueIndex: ${popup.valueIndex}"
        },
        containerColor = Color(0xff414141)
    )
    val data = remember {
        listOf(
            Line(
                label = "Windows",
                values = listOf(
                    75.0,
                    5.0,
                    70.0,
                    85.0,
                    0.0
                ),
                color = SolidColor(Color(0xFF2B8130)),
                firstGradientFillColor = Color(0xFF66BB6A).copy(alpha = .4f),
                secondGradientFillColor = Color.Transparent,
                strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                gradientAnimationDelay = 1000,
                drawStyle = DrawStyle.Stroke(.5.dp),
                curvedEdges = true,
                popupProperties = popupProperties
            ),
            Line(
                label = "Linux",
                values = listOf(
                    1.0,
                    19.0,
                    22.0,
                    0.0,
                    5.0
                ),
                color = SolidColor(Color(0xFFDA860C)),
                firstGradientFillColor = Color(0xFFFFA726).copy(alpha = .4f),
                secondGradientFillColor = Color.Transparent,
                strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                gradientAnimationDelay = 1000,
                drawStyle = DrawStyle.Stroke(.5.dp),
                popupProperties = popupProperties.copy(enabled = false)
            ),
            Line(
                label = "MacOS",
                values = listOf(
                    4.0,
                    40.0,
                    58.0,
                    38.0,
                    22.0
                ),
                color = SolidColor(Color(0xFF0F73C4)),
                firstGradientFillColor = Color(0xFF42A5F5).copy(alpha = .4f),
                secondGradientFillColor = Color.Transparent,
                strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                gradientAnimationDelay = 1000,
                drawStyle = DrawStyle.Stroke(.5.dp),
                curvedEdges = true,
                popupProperties = popupProperties
            ),
        )
    }
    ChartParent(modifier=Modifier) {
        Box(modifier = Modifier.fillMaxSize().padding(vertical = 12.dp)){
            LineChart(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 22.dp),
                data = data,
                animationMode = AnimationMode.Together(delayBuilder = {
                    it * 500L
                }),
                gridProperties = gridProperties,
                dividerProperties = dividerProperties,
                indicatorProperties = HorizontalIndicatorProperties(
                    textStyle = TextStyle(
                        fontSize = 11.sp,
                        fontFamily = ubuntu, color = Color.White,
                    ),
                    contentBuilder = {
                        it.format(1) + " M"
                    },
                ),
                labelHelperProperties = labelHelperProperties,
                curvedEdges = false
            )
        }
    }
}
@Composable
fun LineSample2(modifier: Modifier=Modifier) {
    val data = remember {
        listOf(
            Line(
                label = "Temperature",
                values = listOf(
                    28.0,
                    41.0,
                    -15.0,
                    27.0,
                    54.0
                ),
                color = SolidColor(Color(0xFF23af92)),
                firstGradientFillColor = Color(0xFF2BC0A1).copy(alpha = .5f),
                secondGradientFillColor = Color.Transparent,
                strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                gradientAnimationDelay = 1000,
                drawStyle = DrawStyle.Stroke(),
                curvedEdges = true,
            ),
        )
    }
    ChartParent(modifier=Modifier) {
        Box(modifier = Modifier.fillMaxSize().padding(vertical = 12.dp)){
            LineChart(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 22.dp),
                data = data,
                animationMode = AnimationMode.Together(delayBuilder = {
                    it * 500L
                }),
                gridProperties = gridProperties.copy(
                    yAxisProperties = GridProperties.AxisProperties(enabled = false),
                    xAxisProperties = gridProperties.xAxisProperties.copy(
                        thickness = .5.dp
                    )
                ),
                dividerProperties = DividerProperties(
                    yAxisProperties = LineProperties(enabled = false),
                    xAxisProperties = LineProperties(
                        thickness = .5.dp,
                        color = SolidColor(Color.Gray.copy(alpha = .5f)),
                        style = StrokeStyle.Dashed(intervals = floatArrayOf(15f,15f), phase = 10f),
                    )
                ),
                popupProperties = PopupProperties(
                    textStyle = TextStyle(
                        fontSize = 11.sp,
                        color = Color.White,
                        fontFamily = ubuntu
                    ),
                    contentBuilder = { popup->
                        popup.value.format(1) + " °C"
                    },
                    containerColor = Color(0xff414141)
                ),
                zeroLineProperties = ZeroLineProperties(
                    enabled = true,
                    color = SolidColor(Color(0xFFAD1457)),
                    thickness = 1.dp,
                ),
                indicatorProperties = HorizontalIndicatorProperties(
                    textStyle = TextStyle(
                        fontSize = 11.sp,
                        fontFamily = ubuntu, color = Color.White
                    ),
                    contentBuilder = {
                        it.format(1) + " °C"
                    },
                ),
                labelHelperProperties = labelHelperProperties,
                curvedEdges = false,
                maxValue = 100.0,
                minValue = -20.0
            )
        }
    }
}
@Composable
fun LineSample3(modifier: Modifier=Modifier) {
    val data = remember {
        listOf(
            Line(
                label = "Windows",
                values = listOf(
                    88.0,
                    56.0,
                    70.0,
                    45.0,
                    26.0
                ),
                color = SolidColor(Color(0xffF7B731)),
                strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                gradientAnimationDelay = 1000,
                drawStyle = DrawStyle.Stroke(),
                curvedEdges = false,
                dotProperties = DotProperties(
                    enabled = true,
                    color = SolidColor(Color(0xffE1E2EC)),
                    strokeWidth = 1.dp,
                    strokeColor = SolidColor(Color(0xffF7B731)),
                )
            ),
            Line(
                label = "Linux",
                values = listOf(
                    30.0,
                    70.0,
                    45.0,
                    65.0,
                    17.0
                ),
                color = SolidColor(Color(0xff0FB9B1)),
                strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                gradientAnimationDelay = 1000,
                drawStyle = DrawStyle.Stroke(),
                curvedEdges = false,
                dotProperties = DotProperties(
                    enabled = true,
                    color = SolidColor(Color(0xffE1E2EC)),
                    strokeWidth = 2.dp,
                    strokeColor = SolidColor(Color(0xff0FB9B1)),
                    confirmDraw = {
                        it.valueIndex in listOf(0,4)
                    }
                )
            ),
        )
    }
    ChartParent(modifier=Modifier) {
        Box(modifier = Modifier.fillMaxSize().padding(vertical = 12.dp)){
            LineChart(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 22.dp),
                data = data,
                animationMode = AnimationMode.Together(delayBuilder = {
                    it * 500L
                }),
                gridProperties = gridProperties,
                dividerProperties = dividerProperties,
                popupProperties = PopupProperties(
                    textStyle = TextStyle(
                        fontSize = 11.sp,
                        color = Color.White,
                        fontFamily = ubuntu
                    ),
                    mode = PopupProperties.Mode.PointMode(),
                    contentBuilder = { popup ->
                        popup.value.format(1) + " Million" + " - dataIdx: " + popup.dataIndex + ", valueIdx: " + popup.valueIndex
                    },
                    containerColor = Color(0xff414141)
                ),
                indicatorProperties = HorizontalIndicatorProperties(
                    textStyle = TextStyle(
                        fontSize = 11.sp,
                        fontFamily = ubuntu, color = Color.White
                    ),
                    contentBuilder = {
                        it.format(1) + " M"
                    }
                ),
                labelHelperProperties = labelHelperProperties,
                curvedEdges = false,
                labelProperties = LabelProperties(
                    enabled = true,
                    labels = listOf("Jan","Feb","Mar","Apr","May"),
                    textStyle = TextStyle(
                        fontSize = 11.sp,
                        fontFamily = ubuntu, color = Color.White
                    ),
                ),
            )
        }
    }
}
@Composable
fun LineSample4(modifier: Modifier=Modifier) {
    val data = remember {
        listOf(
            Line(
                label = "Windows",
                values = MutableList(5) { (0..100).random().toDouble() },
                color = SolidColor(Color(0xFF2B8130)),
                strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                gradientAnimationDelay = 1000,
                drawStyle = DrawStyle.Stroke(),
                curvedEdges = true,
            ),
            Line(
                label = "Linux",
                values = MutableList(5) { (0..100).random().toDouble() },
                color = SolidColor(Color(0xFFE65100)),
                strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                gradientAnimationDelay = 1000,
                drawStyle = DrawStyle.Stroke(),
            ),
            Line(
                label = "Android",
                values = MutableList(5) { (0..100).random().toDouble() },
                color = SolidColor(Color(0xFFB71C1C)),
                strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                gradientAnimationDelay = 1000,
                drawStyle = DrawStyle.Stroke(),
            ),
        )
    }
    ChartParent(modifier=Modifier) {
        Box(modifier = Modifier.fillMaxSize().padding(vertical = 12.dp)){
            LineChart(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 22.dp),
                data = data,
                animationMode = AnimationMode.Together(delayBuilder = {
                    it * 500L
                }),
                gridProperties = gridProperties,
                dividerProperties = dividerProperties,
                popupProperties = PopupProperties(
                    textStyle = TextStyle(
                        fontSize = 11.sp,
                        color = Color.White,
                        fontFamily = ubuntu,
                    ),
                    contentBuilder = { popup->
                        popup.value.format(1) + " Million"
                    },
                    containerColor = Color(0xff414141)
                ),
                indicatorProperties = HorizontalIndicatorProperties(
                    textStyle = TextStyle(
                        fontSize = 11.sp,
                        fontFamily = ubuntu, color = Color.White
                    ),
                    contentBuilder = {
                        it.format(1) + " M"
                    }
                ),
                labelHelperProperties = labelHelperProperties,
                curvedEdges = true
            )
        }
    }
}
@Composable
fun LineSample5(modifier: Modifier=Modifier) {
    val data = remember {
        listOf(
            Line(
                label = "Windows",
                values = MutableList(5) { (0..100).random().toDouble() },
                color = SolidColor(Color(0xFFF7B731)),
                strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                gradientAnimationDelay = 1000,
                drawStyle = DrawStyle.Stroke(),
            ),
            Line(
                label = "Linux",
                values = MutableList(5) { (0..100).random().toDouble() },
                color = SolidColor(Color(0xFF0FB9B1)),
                strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                gradientAnimationDelay = 1000,
                drawStyle = DrawStyle.Stroke(),
            ),
        )
    }
    ChartParent(modifier=Modifier) {
        Box(modifier = Modifier.fillMaxSize().padding(vertical = 12.dp)){
            LineChart(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 22.dp),
                data = data,
                animationMode = AnimationMode.Together(delayBuilder = {
                    it * 500L
                }),
                gridProperties = gridProperties,
                dividerProperties = dividerProperties,
                popupProperties = PopupProperties(
                    textStyle = TextStyle(
                        fontSize = 11.sp,
                        fontFamily = ubuntu, color = Color.White,
                    ),
                    contentBuilder = { popup->
                        popup.value.format(1) + " Million"
                    },
                    containerColor = Color(0xff414141)
                ),
                indicatorProperties = HorizontalIndicatorProperties(
                    textStyle = TextStyle(
                        fontSize = 11.sp,
                        fontFamily = ubuntu, color = Color.White
                    ),
                    contentBuilder = {
                        it.format(1) + " M"
                    }
                ),
                labelHelperProperties = labelHelperProperties,
                curvedEdges = false,
                labelProperties = LabelProperties(
                    enabled = true,
                    labels = listOf("Jan","Feb","Mar","Apr","May"),
                    textStyle = TextStyle(
                        fontSize = 11.sp,
                        fontFamily = ubuntu, color = Color.White
                    ),
                ),
            )
        }
    }
}
@Composable
fun LineSample6(modifier: Modifier=Modifier) {
    val data = remember {
        listOf(
            Line(
                label = "Windows",
                values = listOf(
                    67.0,
                    0.0,
                    88.0,
                    90.0,
                    95.0
                ),
                color = SolidColor(Color(0xFFFB8231)),
                strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                gradientAnimationDelay = 1000,
                drawStyle = DrawStyle.Stroke(
                    width = 3.dp,
                    strokeStyle = StrokeStyle.Dashed(intervals = floatArrayOf(10f,10f), phase = 15f)
                ),
                dotProperties = DotProperties(
                    enabled = true,
                    color = SolidColor(Color(0xFFFB8231)),
                    strokeWidth = 2.dp,
                    radius = 3.dp,
                    strokeColor = SolidColor(Color(0xffffffff)),
                )
            ),
            Line(
                label = "Linux",
                values = listOf(
                    98.0,
                    67.0,
                    15.0,
                    20.0,
                    75.0
                ),
                color = SolidColor(Color(0xff23AF92)),
                strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                gradientAnimationDelay = 1000,
                drawStyle = DrawStyle.Stroke(
                    width = 3.dp,
                    strokeStyle = StrokeStyle.Dashed(intervals = floatArrayOf(10f,10f), phase = 15f)
                ),
                dotProperties = DotProperties(
                    enabled = true,
                    color = SolidColor(Color(0xff23AF92)),
                    strokeWidth = 2.dp,
                    radius = 3.dp,
                    strokeColor = SolidColor(Color(0xffffffff)),
                ),
                popupProperties = PopupProperties(enabled = false)
            ),
        )
    }
    ChartParent(modifier=Modifier) {
        Box(modifier = Modifier.fillMaxSize().padding(vertical = 12.dp)){
            LineChart(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 22.dp),
                data = data,
                animationMode = AnimationMode.Together(delayBuilder = {
                    it * 500L
                }),
                gridProperties = gridProperties,
                dividerProperties = dividerProperties,
                popupProperties = PopupProperties(
                    textStyle = TextStyle(
                        fontSize = 11.sp,
                        fontFamily = ubuntu, color = Color.White
                    ),
                    contentBuilder = { popup->
                        popup.value.format(1) + " Million"
                    },
                    containerColor = Color(0xff414141)
                ),
                indicatorProperties = HorizontalIndicatorProperties(
                    textStyle = TextStyle(
                        fontSize = 11.sp,
                        fontFamily = ubuntu, color = Color.White
                    ),
                    contentBuilder = {
                        it.format(1) + " M"
                    }
                ),
                labelProperties = LabelProperties(
                    enabled = true,
                    labels = listOf("Jan","Feb","Mar","Apr","May"),
                    textStyle = TextStyle(
                        fontSize = 11.sp,
                        fontFamily = ubuntu, color = Color.White
                    ),
                ),
                labelHelperProperties = labelHelperProperties,
                curvedEdges = true
            )
        }
    }
}
@Composable
fun LineSample7(modifier: Modifier=Modifier) {
    val data = remember {
        listOf(
            Line(
                label = "Linux",
                values = listOf(
                    71.0,
                    0.0,
                    100.0,
                    50.0,
                    50.0
                ),
                color = Brush.radialGradient(
                    listOf(
                        Color(0xFFFFB300),
                        Color(0xFFD81B60)
                    )
                ),
                strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                gradientAnimationDelay = 1000,
            ),
        )
    }
    ChartParent(modifier=Modifier) {
        Box(modifier = Modifier.fillMaxSize().padding(vertical = 12.dp)){
            LineChart(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 22.dp),
                data = data,
                animationMode = AnimationMode.Together(delayBuilder = {
                    it * 500L
                }),
                gridProperties = gridProperties,
                dividerProperties = dividerProperties,
                popupProperties = PopupProperties(
                    textStyle = TextStyle(
                        fontSize = 11.sp,
                        color = Color.White,
                        fontFamily = ubuntu,
                    ),
                    contentBuilder = { popup->
                        popup.value.format(1) + " Million"
                    },
                    containerColor = Color(0xff414141),
                ),
                indicatorProperties = HorizontalIndicatorProperties(
                    textStyle = TextStyle(
                        fontSize = 11.sp,
                        fontFamily = ubuntu, color = Color.White
                    ),
                    contentBuilder = {
                        it.format(1) + " M"
                    }
                ),
                labelHelperProperties = labelHelperProperties,
                curvedEdges = true
            )
        }
    }
}

@Composable
fun LineSample8(modifier: Modifier=Modifier) {
    val data = remember {
        listOf(
            Line(
                label = "Linux",
                values = listOf(
                    10.0,
                    20.0,
                    7.0,
                    35.0,
                    20.0
                ),
                color = SolidColor(Color(0xff5A47CF)),
                firstGradientFillColor = Color(0xff6655CF).copy(alpha = .5f),
                secondGradientFillColor = Color.Transparent,
                strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                gradientAnimationDelay = 1000,
                drawStyle = DrawStyle.Stroke(3.dp)
            ),
        )
    }
    ChartParent(modifier=Modifier) {
        Box(modifier = Modifier.fillMaxSize().padding(vertical = 12.dp)){
            LineChart(
                modifier = Modifier
                    .fillMaxSize(),
                data = data,
                animationMode = AnimationMode.Together(delayBuilder = {
                    it * 500L
                }),
                dividerProperties = DividerProperties(enabled = false),
                gridProperties = GridProperties(enabled = false),
                popupProperties = PopupProperties(
                    textStyle = TextStyle(
                        fontSize = 11.sp,
                        color = Color.White,
                        fontFamily = ubuntu,
                    ),
                    contentBuilder = { popup->
                        popup.value.format(1) + " Million"
                    },
                    containerColor = Color(0xff414141)
                ),
                indicatorProperties = HorizontalIndicatorProperties(
                    enabled = false
                ),
                labelProperties = LabelProperties(enabled = false),
                labelHelperProperties = LabelHelperProperties(enabled = false),
                curvedEdges = true
            )
        }
    }
}

@Composable
fun LineSample9(modifier: Modifier=Modifier) {
    val data = remember {
        listOf(
            Line(
                label = "Windows",
                values = MutableList(5) { (0..100).random().toDouble() },
                color = SolidColor(Color(0xFFfd9644)),
                strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                gradientAnimationDelay = 1000,
                drawStyle = DrawStyle.Fill,
                curvedEdges = true,
            ),
        )
    }
    ChartParent(modifier=Modifier) {
        Box(modifier = Modifier.fillMaxSize().padding(vertical = 12.dp)){
            LineChart(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 22.dp),
                data = data,
                animationMode = AnimationMode.Together(delayBuilder = {
                    it * 500L
                }),
                gridProperties = gridProperties,
                dividerProperties = dividerProperties,
                popupProperties = PopupProperties(
                    textStyle = TextStyle(
                        fontSize = 11.sp,
                        color = Color.White,
                        fontFamily = ubuntu,
                    ),
                    contentBuilder = { popup->
                        popup.value.format(1) + " Million"
                    },
                    confirmDraw = {
                        it.value > 50
                    },
                    containerColor = Color(0xff414141)
                ),
                indicatorProperties = HorizontalIndicatorProperties(
                    textStyle = TextStyle(
                        fontSize = 11.sp,
                        fontFamily = ubuntu, color = Color.White
                    ),
                    contentBuilder = {
                        it.format(1) + " M"
                    }
                ),
                labelHelperProperties = labelHelperProperties,
                curvedEdges = false
            )
        }
    }
}
