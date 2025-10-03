package ir.ehsannarmani.compose_charts.models

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.ehsannarmani.compose_charts.extensions.format

data class PopupProperties(
    val enabled: Boolean = true,
    val animationSpec: AnimationSpec<Float> = tween(400),
    val duration: Long = 1500,
    val textStyle: TextStyle = TextStyle.Default.copy(fontSize = 12.sp),
    val containerColor: Color = Color(0xff313131),
    val cornerRadius: Dp = 6.dp,
    val contentHorizontalPadding: Dp = 4.dp,
    val contentVerticalPadding: Dp = 2.dp,
    val mode: Mode = Mode.Normal,
    val contentBuilder: (Popup) -> String = { popup ->
        popup.value.format(1)
    },
    val confirmDraw: (Popup) -> Boolean = { popup ->
        true
    }
) {
    data class Popup(
        val dataIndex: Int,
        val valueIndex: Int,
        val value: Double
    )

    sealed class Mode {
        data object Normal : Mode()
        data class PointMode(val threshold: Dp = 16.dp) : Mode()
    }
}
