package ir.ehsannarmani.compose_charts.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun TabletSample() {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(250.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp),
        horizontalArrangement = Arrangement.spacedBy(28.dp),
        contentPadding = PaddingValues(28.dp)
    ){
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

        //Row
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





