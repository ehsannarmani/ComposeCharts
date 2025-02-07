package ir.ehsannarmani.compose_charts.extensions

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path
import ir.ehsannarmani.compose_charts.models.Bars

fun Path.addRoundRect(
    rect: Rect,
    radius: Bars.Data.RadiusPx
){
    when (radius) {
        is Bars.Data.RadiusPx.None -> {
            addRect(rect)
        }

        is Bars.Data.RadiusPx.Circular -> {
            addRoundRect(
                roundRect = RoundRect(
                    rect = rect,
                    cornerRadius = CornerRadius(
                        x = radius.radius,
                        y = radius.radius
                    )
                )
            )
        }

        is Bars.Data.RadiusPx.Rectangle -> {
            addRoundRect(
                roundRect = RoundRect(
                    rect = rect,
                    topLeft = CornerRadius(
                        x = radius.topLeft,
                        y = radius.topLeft
                    ),
                    topRight = CornerRadius(
                        x = radius.topRight,
                        y = radius.topRight
                    ),
                    bottomLeft = CornerRadius(
                        x = radius.bottomLeft,
                        y = radius.bottomLeft
                    ),
                    bottomRight = CornerRadius(
                        x = radius.bottomRight,
                        y = radius.bottomRight
                    ),
                )
            )
        }
    }
}