package ir.ehsannarmani.compose_charts.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.RowChart
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.IndicatorCount
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.Pie

@Composable
fun PhoneSample() {
    LazyColumn(
        modifier = Modifier
            .statusBarsPadding(), verticalArrangement = Arrangement.spacedBy(28.dp),
        contentPadding = PaddingValues(22.dp)
    ) {
        // Pie
        item {
            PieSample()
        }
        item {
            PieSample2()
        }
        item {
            PieSample3()
        }

        // Column
        item {
            ColumnSample()
        }
        item {
            ColumnSample2()
        }
        item {
            ColumnSample3()
        }

        // Row
        item {
            RowSample()
        }
        item {
            RowSample2()
        }
        item {
            RowSample3()
        }

        // Line
        item {
            LineSample()
        }
        item {
            LineSample2()
        }
        item {
            LineSample4()
        }
        item {
            LineSample8()
        }
        item {
            LineSample7()
        }
        item {
            LineSample6()
        }
        item {
            LineSample5()
        }
        item {
            LineSample3()
        }
        item {
            LineSample9()
        }
    }
}