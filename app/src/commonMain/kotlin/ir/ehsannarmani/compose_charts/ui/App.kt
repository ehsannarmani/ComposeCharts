package ir.ehsannarmani.compose_charts.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ir.ehsannarmani.compose_charts.ui.theme.ComposeChartsTheme

@Composable
fun App() {

    ComposeChartsTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            TabletSample()
        }
    }
}
