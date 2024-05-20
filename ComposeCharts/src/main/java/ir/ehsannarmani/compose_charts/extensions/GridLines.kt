package ir.ehsannarmani.compose_charts.extensions

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp

fun DrawScope.drawGridLines(count:Int,color: Color,strokeWidth:Dp,size: Size? = null,xPadding:Float = 0f,justDividers:Boolean = false){
    val _size = size ?: this.size
    if (!justDividers){
        for (i in 0 until count) {
            val everyHeight = _size.height / count
            val everyWidth = _size.width / count
            drawLine(
                color = color,
                start = Offset(0f+xPadding, everyHeight * i),
                end = Offset(_size.width+xPadding, everyHeight * i),
                strokeWidth = strokeWidth.toPx()
            )
            drawLine(
                color = color,
                start = Offset((everyWidth * i)+xPadding, 0f),
                end = Offset((everyWidth * i)+xPadding, _size.height),
                strokeWidth = strokeWidth.toPx()
            )
        }
    }
    drawLine(
        color = color,
        start = Offset(0f+xPadding,_size.height),
        end = Offset(_size.width+xPadding,_size.height),
        strokeWidth = strokeWidth.toPx()
    )
    if (justDividers){
        drawLine(
            color = color,
            start = Offset(0f+xPadding,0f),
            end = Offset(0f+xPadding,_size.height),
            strokeWidth = strokeWidth.toPx()
        )
    }
}