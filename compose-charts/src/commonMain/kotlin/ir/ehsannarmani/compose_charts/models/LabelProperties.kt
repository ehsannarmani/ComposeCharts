package ir.ehsannarmani.compose_charts.models

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class LabelProperties(
    val enabled: Boolean,
    val textStyle: TextStyle = TextStyle.Default.copy(fontSize = 12.sp, textAlign = TextAlign.End),
    val padding: Dp = 12.dp,
    val labels: List<String> = listOf(),
    val builder: (@Composable (modifier:Modifier,label:String,shouldRotate:Boolean,index:Int) -> Unit)? = null,
    val rotation: Rotation = Rotation()
){
    data class Rotation(
        val mode: Mode = Mode.IfNecessary,
        val degree:Float = -45f,
        val padding: Dp? = null
    ){
        enum class Mode{
            Force,IfNecessary
        }
    }
}

