package ir.ehsannarmani.compose_charts.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import composecharts.app.generated.resources.Res
import composecharts.app.generated.resources.dark_mode
import composecharts.app.generated.resources.light_mode
import ir.ehsannarmani.compose_charts.ui.theme.ComposeChartsTheme
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val systemInDarkTheme = isSystemInDarkTheme()
    var useDarkTheme: Boolean by remember(systemInDarkTheme) { mutableStateOf(systemInDarkTheme) }
    ComposeChartsTheme(darkTheme = useDarkTheme) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("Compose Charts")
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                useDarkTheme = !useDarkTheme
                            }
                        ) {
                            Icon(
                                painter = painterResource(
                                    if (useDarkTheme) Res.drawable.light_mode else Res.drawable.dark_mode
                                ),
                                contentDescription = if (useDarkTheme) "Switch to light mode" else "Switch to dark mode"
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                TabletSample()
            }
        }
    }
}
