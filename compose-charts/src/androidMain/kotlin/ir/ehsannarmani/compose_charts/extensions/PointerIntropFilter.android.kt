package ir.ehsannarmani.compose_charts.extensions

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter

@OptIn(ExperimentalComposeUiApi::class)
actual fun Modifier.pointerInteropFilter(onTouchEvent: (MotionEvent) -> Boolean): Modifier {
    return this.pointerInteropFilter { event -> onTouchEvent.invoke(event.toCommon()) }
}

fun android.view.MotionEvent.toCommon(): MotionEvent {
    return MotionEvent(
        action = this.action, x = this.x, y = this.y
    )
}