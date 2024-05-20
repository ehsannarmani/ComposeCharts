package ir.ehsannarmani.compose_charts.extensions

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import ir.ehsannarmani.compose_charts.models.Bars


context(DrawScope)
fun Path.addRoundRect(rect: Rect,radius: Bars.Data.Radius){
    when (radius) {
        is Bars.Data.Radius.None -> {
            addRect(rect)
        }

        is Bars.Data.Radius.Circular -> {
            addRoundRect(
                roundRect = RoundRect(
                    rect = rect,
                    cornerRadius = CornerRadius(
                        x = radius.radius.toPx(),
                        y = radius.radius.toPx()
                    )
                )
            )
        }

        is Bars.Data.Radius.Rectangle -> {
            addRoundRect(
                roundRect = RoundRect(
                    rect = rect,
                    topLeft = CornerRadius(
                        x = radius.topLeft.toPx(),
                        y = radius.topLeft.toPx()
                    ),
                    topRight = CornerRadius(
                        x = radius.topRight.toPx(),
                        y = radius.topRight.toPx()
                    ),
                    bottomLeft = CornerRadius(
                        x = radius.bottomLeft.toPx(),
                        y = radius.bottomLeft.toPx()
                    ),
                    bottomRight = CornerRadius(
                        x = radius.bottomRight.toPx(),
                        y = radius.bottomRight.toPx()
                    ),
                )
            )
        }
    }
}