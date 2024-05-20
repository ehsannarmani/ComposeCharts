package ir.ehsannarmani.compose_charts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.ehsannarmani.compose_charts.ui.theme.ComposeChartsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeChartsTheme() {
                Column(modifier=Modifier.verticalScroll(rememberScrollState()),) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        PieSample()
                        PieSample2()
                    }
                    ColumnSample()
                    Spacer(modifier = Modifier.height(32.dp))
                    RowSample()
                    Spacer(modifier = Modifier.height(48.dp))
                    LineSample()
                    Spacer(modifier = Modifier.height(48.dp))
                    LineSample2()
                    Spacer(modifier = Modifier.height(48.dp))
                    LineSample3()
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}





