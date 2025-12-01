import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import ir.ehsannarmani.compose_charts.ui.App
import kotlinx.browser.document
import org.jetbrains.skiko.wasm.onWasmReady

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    onWasmReady {
        ComposeViewport("app") {
            App()
        }
    }
}