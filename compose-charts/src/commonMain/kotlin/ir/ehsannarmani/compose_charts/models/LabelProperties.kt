package ir.ehsannarmani.compose_charts.models

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class LabelProperties(
    val enabled:Boolean,
    val textStyle: TextStyle = TextStyle.Default.copy(fontSize = 12.sp, textAlign = TextAlign.End),
    val padding:Dp = 12.dp,
    val labels: List<String> = listOf(),
    val rotationDegreeOnSizeConflict: Float = -45f,
)
