package ir.ehsannarmani.compose_charts.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ChartParent(modifier: Modifier, content: @Composable () -> Unit) {
    Card(
        modifier = modifier
            .height(270.dp)
            .fillMaxWidth(),
    ) {
        content()
    }
}