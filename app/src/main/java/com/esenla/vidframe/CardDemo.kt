package com.esenla.vidframe

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Preview(showBackground = true)
@Composable
fun CardDemo(){

    val modifier = Modifier.fillMaxWidth().height(100.dp).padding(horizontal = 10.dp, vertical = 5.dp)
    val style = MaterialTheme.typography.titleMedium
    val texModifier = Modifier.padding(start = 20.dp, top = 20.dp)


    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){

        Column() {
            Card(modifier=modifier){
                Text("Filed Card(Default)", modifier=texModifier, style=style)
            }

            ElevatedCard(modifier=modifier) {
                Text("Elevated Card", modifier=texModifier, style=style)
            }

            OutlinedCard(modifier=modifier, border = BorderStroke(2.dp, Color(0xFF00ACFF))) {
                Text("Outlined Card", modifier=texModifier, style=style)
            }

        }
    }

}