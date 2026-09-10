package ir.ehsannarmani.compose_charts.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun App(){
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xff101010))
    ){
        Samples()
    }
}