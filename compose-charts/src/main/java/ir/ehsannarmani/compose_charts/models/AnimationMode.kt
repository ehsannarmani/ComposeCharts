package ir.ehsannarmani.compose_charts.models

sealed class AnimationMode {
    data class Together(val delayBuilder: (index: Int) -> Long = { 0 }) : AnimationMode()
    data object OneByOne : AnimationMode()
}