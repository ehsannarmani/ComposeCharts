package ir.ehsannarmani.compose_charts.utils

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import ir.ehsannarmani.compose_charts.models.Bars
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/*
* RC: Row/Column Animation
* */
@Composable
fun ImplementRCAnimation(
    data:List<Bars>,
    animationMode: Bars.AnimationMode,
    spec: (Bars.Data)->AnimationSpec<Float>,
    delay:Long,
    before:()->Unit
) {
    LaunchedEffect(data) {
        before()
        delay(delay)
        data.forEachIndexed { colIndex, columnChart ->
            columnChart.values.forEachIndexed { dataIndex, data ->
                val animate: suspend () -> Unit = {
                    data.animator.animateTo(
                        1f,
                        animationSpec = spec(data)
                    )
                }
                when (animationMode) {
                    is Bars.AnimationMode.OneByOne -> {
                        animate()
                    }

                    is Bars.AnimationMode.Together -> {
                        launch {
                            delay(animationMode.delayBuilder((colIndex * columnChart.values.count()) + dataIndex))
                            animate()
                        }
                    }
                }
            }
        }
    }
}