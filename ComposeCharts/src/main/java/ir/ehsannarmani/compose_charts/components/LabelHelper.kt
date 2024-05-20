package ir.ehsannarmani.compose_charts.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.ehsannarmani.compose_charts.Line
import ir.ehsannarmani.compose_charts.models.Bars

@Composable
fun LabelHelper(
    data: List<Pair<String, Color>>
) {
    LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier) {
        items(data) { (label, color) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(color)
                )
                Text(text = label, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun RCChartLabelHelper(data:List<Bars>) {
    val labels = data.map { it.values.map { it.label } }.flatten().toSet().toList()
    val colors = labels.map { label-> data.map { it.values.find { it.label == label }?.color }.firstOrNull() }
    LabelHelper(data = labels.mapIndexed { index, label -> label.orEmpty() to (colors[index] ?: Color.Unspecified)  })
}
