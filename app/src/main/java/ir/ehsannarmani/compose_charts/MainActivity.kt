package ir.ehsannarmani.compose_charts

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ir.ehsannarmani.compose_charts.ui.theme.ComposeChartsTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeChartsTheme(false) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xff101010))){
                    if (isTablet(this@MainActivity)) {
                        TabletSample()
                    }else{
                        PhoneSample()
                    }
                }
            }
        }
    }
}

private fun isTablet(context: Context): Boolean {
    return (context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE
}


@Composable
fun TabletSample() {
    Column(modifier= Modifier
        .verticalScroll(rememberScrollState())
        .statusBarsPadding(), verticalArrangement = Arrangement.spacedBy(28.dp)) {
        Row(
            modifier= Modifier
                .fillMaxWidth()
                .padding(horizontal = 48.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(48.dp)
        ) {
            PieSample()
            PieSample2()
            PieSample3()
        }
        Row(modifier= Modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(48.dp)) {
            ColumnSample()
            ColumnSample2()
            ColumnSample3()
        }
        Row(modifier= Modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(48.dp)) {
            RowSample()
            RowSample2()
            RowSample3()
        }
        Row(modifier= Modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(48.dp)) {
            LineSample()
            LineSample2()
            LineSample4()
        }
        Row(modifier= Modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(48.dp)) {
            LineSample8()
            LineSample7()
            LineSample6()
        }
        Row(modifier= Modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(48.dp)) {
            LineSample5()
            LineSample3()
            LineSample9()
        }
        Spacer(modifier = Modifier.height(100.dp))
    }
}
@Composable
fun PhoneSample() {
    LazyColumn(modifier= Modifier
        .statusBarsPadding(), verticalArrangement = Arrangement.spacedBy(28.dp)) {
        item {
            Column(
                modifier= Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(22.dp)
            ) {
                Row {
                    PieSample()
                }
                Row {
                    PieSample2()
                }
                Row {
                    PieSample3()
                }
            }
        }
        item {
            Column(modifier= Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(22.dp)) {
                Row {
                    ColumnSample()
                }
                Row {
                    ColumnSample2()
                }
                Row {
                    ColumnSample3()
                }
            }
        }
        item {
            Column(modifier= Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(22.dp)) {
                Row {
                    RowSample()
                }
                Row {
                    RowSample2()
                }
                Row {
                    RowSample3()
                }
            }
        }
        item {
            Column(modifier= Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(22.dp)) {
                Row {
                    LineSample()
                }
                Row {
                    LineSample2()
                }
                Row {
                    LineSample4()
                }
            }
        }
        item {
            Column(modifier= Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(22.dp)) {
                Row {
                    LineSample8()
                }
                Row {
                    LineSample7()
                }
                Row {
                    LineSample6()
                }
            }
        }
        item {
            Column(modifier= Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(22.dp)) {
                Row {
                    LineSample5()
                }
                Row {
                    LineSample3()
                }
                Row {
                    LineSample9()
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}




