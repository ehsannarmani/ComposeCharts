package ir.ehsannarmani.compose_charts.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ChartParent(modifier: Modifier, content: @Composable () -> Unit) {
    Card(
        modifier = modifier
            .height(270.dp)
            .fillMaxWidth()
            .border(2.dp, Color.Transparent, RoundedCornerShape(12.dp)),
        elevation = CardDefaults.elevatedCardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xff2D2D2D)
        )

    ) {
        content()
    }
}