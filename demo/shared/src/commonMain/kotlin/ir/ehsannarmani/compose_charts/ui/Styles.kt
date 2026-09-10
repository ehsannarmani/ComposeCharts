package ir.ehsannarmani.compose_charts.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties

val labelHelperProperties: LabelHelperProperties @Composable get()  = LabelHelperProperties(textStyle = TextStyle(fontSize = 12.sp, fontFamily = ubuntu, color = Color.White))
val labelProperties: LabelProperties @Composable get()  = LabelProperties(
    enabled = true,
    textStyle = TextStyle(fontSize = 12.sp, fontFamily = ubuntu, color = Color.White)
)