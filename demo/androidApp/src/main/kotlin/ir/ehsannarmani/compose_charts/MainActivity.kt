package ir.ehsannarmani.compose_charts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ir.ehsannarmani.compose_charts.ui.App
import ir.ehsannarmani.compose_charts.ui.theme.ComposeChartsTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        enableEdgeToEdge()
        setContent {
            ComposeChartsTheme(false) {
                App()
            }
        }
    }
}

