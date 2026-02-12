package ir.ehsannarmani.compose_charts.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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