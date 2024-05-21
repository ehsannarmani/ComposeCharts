package ir.ehsannarmani.compose_charts

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.ehsannarmani.compose_charts.models.GridLineStyle
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.IndicatorProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.PopupProperties


fun generateLineData(): List<Line> {
    return listOf(
        Line(
            label = "Windows",
            values = MutableList(5) { (0..100).random().toDouble() },
            color = Color(0xFF2B8130),
            firstGradientFillColor = Color(0xFF66BB6A).copy(alpha = .4f),
            secondGradientFillColor = Color.Transparent,
            strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
            gradientAnimationDelay = 1000,
            drawStyle = Line.DrawStyle.Stroke(.5.dp),
            curvedEdges = true
        ),
        Line(
            label = "Linux",
            values = MutableList(5) { (0..100).random().toDouble() },
            color = Color(0xFFDA860C),
            firstGradientFillColor = Color(0xFFFFA726).copy(alpha = .4f),
            secondGradientFillColor = Color.Transparent,
            strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
            gradientAnimationDelay = 1000,
            drawStyle = Line.DrawStyle.Stroke(.5.dp)
        ),
        Line(
            label = "MacOS",
            values = MutableList(5) { (0..100).random().toDouble() },
            color = Color(0xFF0F73C4),
            firstGradientFillColor = Color(0xFF42A5F5).copy(alpha = .4f),
            secondGradientFillColor = Color.Transparent,
            strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
            gradientAnimationDelay = 1000,
            drawStyle = Line.DrawStyle.Stroke(.5.dp),
            curvedEdges = false,
        ),
    )
}

@Composable
fun LineSample() {
    val data = remember {
        mutableStateOf(generateLineData())
    }
    val font = FontFamily(
        listOf(
            Font(
                ir.ehsannarmani.compose_charts.R.font.dana_regular
            )
        )
    )
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        LineChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(270.dp)
                .padding(horizontal = 22.dp),
            data = data.value,
            animationMode = Line.AnimationMode.Together(delayBuilder = {
                it * 500L
            }),
            gridProperties = GridProperties(
                enabled = true,
                strokeWidth = .5.dp,
                color = Color.Gray.copy(alpha = .5f),
                style = GridLineStyle.Dashed(intervals = floatArrayOf(15f,15f), phase = 10f),
                lineCount = 4
            ),
            popupProperties = PopupProperties(
                textStyle = TextStyle(
                    fontSize = 11.sp,
                    color = Color.White,
                    textDirection = TextDirection.Rtl,
                    fontFamily = font
                ),
                contentBuilder = {
                    "%.2f".format(it) + " میلیون"
                }
            ),
            indicatorProperties = IndicatorProperties(
                textStyle = TextStyle(
                    fontSize = 11.sp,
                    textDirection = TextDirection.Rtl,
                    fontFamily = font
                ),
                contentBuilder = {
                    "%.2f".format(it) + " م"
                }
            ),
            curvedEdges = false
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            data.value = generateLineData()
        }, shape = RoundedCornerShape(8.dp)) {
            Text(text = "Show Animation")
        }
    }
}

@Composable
fun LineSample2() {
    val data = remember {
        mutableStateOf(
            listOf(
                Line(
                    label = "Windows",
                    values = MutableList(15) { (0..100).random().toDouble() },
                    color = Color(0xFF2B8130),
                    firstGradientFillColor = Color(0xFF66BB6A).copy(alpha = .4f),
                    secondGradientFillColor = Color.Transparent,
                    strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                    gradientAnimationDelay = 1000,
                    drawStyle = Line.DrawStyle.Stroke(.5.dp)
                ),
            )
        )
    }
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        LineChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(270.dp)
                .padding(horizontal = 22.dp),
            data = data.value,
            animationMode = Line.AnimationMode.Together(delayBuilder = {
                it * 500L
            }),
            gridProperties = GridProperties(enabled = false),
            indicatorProperties = IndicatorProperties(
                enabled = false,
                textStyle = TextStyle(fontSize = 12.sp)
            ),
            popupProperties = PopupProperties(
                contentBuilder = {
                    "%.2f".format(it) + " M"
                },
                textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
            ),
            drawDividers = false
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            data.value = listOf(
                Line(
                    label = "Windows",
                    values = MutableList(5) { (0..100).random().toDouble() },
                    color = Color(0xFF2B8130),
                    firstGradientFillColor = Color(0xFF66BB6A).copy(alpha = .4f),
                    secondGradientFillColor = Color.Transparent,
                    strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                    gradientAnimationDelay = 1000,
                ),
                Line(
                    label = "Linux",
                    values = MutableList(5) { (0..100).random().toDouble() },
                    color = Color(0xFFDA860C),
                    firstGradientFillColor = Color(0xFFFFA726).copy(alpha = .4f),
                    secondGradientFillColor = Color.Transparent,
                    strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                    gradientAnimationDelay = 1000,
                ),
                Line(
                    label = "MacOS",
                    values = MutableList(5) { (0..100).random().toDouble() },
                    color = Color(0xFF0F73C4),
                    firstGradientFillColor = Color(0xFF42A5F5).copy(alpha = .4f),
                    secondGradientFillColor = Color.Transparent,
                    strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                    gradientAnimationDelay = 1000,
                ),
            )
        }, shape = RoundedCornerShape(8.dp)) {
            Text(text = "Show Animation")
        }
    }
}

@Composable
fun LineSample3() {
    val data = remember {
        mutableStateOf(
            listOf(
                Line(
                    label = "Windows",
                    values = MutableList(5) { (0..100).random().toDouble() },
                    color = Color(0xFF2B8130),
                    strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                    gradientAnimationDelay = 1000,
                ),
                Line(
                    label = "Linux",
                    values = MutableList(5) { (0..100).random().toDouble() },
                    color = Color(0xFFDA860C),
                    strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                    gradientAnimationDelay = 1000,
                ),
                Line(
                    label = "MacOS",
                    values = MutableList(5) { (0..100).random().toDouble() },
                    color = Color(0xFF0F73C4),
                    strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                    gradientAnimationDelay = 1000,
                ),
            )
        )
    }
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        LineChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(270.dp)
                .padding(horizontal = 22.dp),
            data = data.value,
            animationMode = Line.AnimationMode.Together(delayBuilder = {
                it * 500L
            }),
            gridProperties = GridProperties(enabled = false),
            indicatorProperties = IndicatorProperties(
                textStyle = TextStyle(fontSize = 12.sp)
            ),
            popupProperties = PopupProperties(
                contentBuilder = {
                    "%.2f".format(it) + " M"
                },
                textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
            ),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            data.value = listOf(
                Line(
                    label = "Windows",
                    values = MutableList(5) { (0..100).random().toDouble() },
                    color = Color(0xFF2B8130),
                    strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                    gradientAnimationDelay = 1000,
                ),
                Line(
                    label = "Linux",
                    values = MutableList(5) { (0..100).random().toDouble() },
                    color = Color(0xFFDA860C),
                    strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                    gradientAnimationDelay = 1000,
                ),
                Line(
                    label = "MacOS",
                    values = MutableList(5) { (0..100).random().toDouble() },
                    color = Color(0xFF0F73C4),
                    strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                    gradientAnimationDelay = 1000,
                ),
            )
        }, shape = RoundedCornerShape(8.dp)) {
            Text(text = "Show Animation")
        }
    }
}