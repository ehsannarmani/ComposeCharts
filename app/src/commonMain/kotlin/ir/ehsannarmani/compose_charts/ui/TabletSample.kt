package ir.ehsannarmani.compose_charts.ui

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.Bars

@Composable
fun TabletSample() {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .statusBarsPadding(), verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        Box(Modifier.fillMaxSize().background(Color.White), contentAlignment = Alignment.Center) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                ColumnChart(
                    modifier = Modifier.width(400.dp).height(350.dp), data = listOf(
                        Bars(
                            label = "شنبه",
                            values = listOf(Bars.Data(label = "", color = SolidColor(Color.Red), value = 20.0))
                        ),
                        Bars(
                            label = "یک شنبه",
                            values = listOf(Bars.Data(label = "", color = SolidColor(Color.Red), value = 0.0))
                        ),
                        Bars(
                            label = "دو شنبه",
                            values = listOf(Bars.Data(label = "", color = SolidColor(Color.Red), value = 0.0))
                        ),
                        Bars(
                            label = "سه شنبه",
                            values = listOf(Bars.Data(label = "", color = SolidColor(Color.Red), value = 0.0))
                        ),
                        Bars(
                            label = "چهار شنبه",
                            values = listOf(Bars.Data(label = "", color = SolidColor(Color.Red), value = 0.0))
                        ),
                        Bars(
                            label = "پنج شنبه",
                            values = listOf(Bars.Data(label = "", color = SolidColor(Color.Red), value = 20.0))
                        ),
                        Bars(
                            label = "جمعه",
                            values = listOf(Bars.Data(label = "", color = SolidColor(Color.Red), value = 20.0))
                        ),
                    ),
                    animationSpec = tween(500),
                    animationMode = AnimationMode.Together { it * 200L },
                    animationDelay = 100
                )
            }
        }
        return
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 48.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(48.dp)
        ) {
            PieSample()
            PieSample2()
            PieSample3()
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 48.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(48.dp)
        ) {
            ColumnSample()
            ColumnSample2()
            ColumnSample3()
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 48.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(48.dp)
        ) {
            RowSample()
            RowSample2()
            RowSample3()
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 48.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(48.dp)
        ) {
            LineSample()
            LineSample2()
            LineSample4()
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 48.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(48.dp)
        ) {
            LineSample8()
            LineSample7()
            LineSample6()
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 48.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(48.dp)
        ) {
            LineSample5()
            LineSample3()
            LineSample9()
        }
        Spacer(modifier = Modifier.height(100.dp))
    }
}





