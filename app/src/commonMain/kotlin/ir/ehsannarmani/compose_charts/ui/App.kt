package ir.ehsannarmani.compose_charts.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun App(){
    val density = LocalDensity.current
    var composableWidth by remember { mutableStateOf(0.dp) }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xff101010)).onGloballyPositioned { coordinates ->
            composableWidth = with(density){ coordinates.size.width.toDp() }
        }){
        if (composableWidth >= 600.dp) {
            TabletSample()
        }else{
            PhoneSample()
        }
    }
}