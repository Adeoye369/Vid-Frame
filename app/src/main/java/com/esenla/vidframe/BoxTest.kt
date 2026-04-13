package com.esenla.vidframe

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun BoxTest(){

    // Main Box For Framing Inner Box
    Box(
        contentAlignment = Alignment.Center,
        modifier=Modifier.fillMaxHeight().fillMaxWidth().background(Color.Black.copy(alpha = 0.3f))
    ){

        // Box to be Positioned
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(200.dp)
                .offset(y= (-50).dp, x=(-10).dp)
                .background(color= Color(0xFFFFAC00), shape = RoundedCornerShape(10.dp))
                .align (Alignment.BottomEnd)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally) {
                Image(painter = painterResource(R.drawable.pose_pic),
                    contentDescription = "Pose Pic",
                    // Basic Tint
                    colorFilter = ColorFilter.tint(color=Color.Blue.copy(alpha=0.5f), blendMode = BlendMode.Multiply),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(width = 150.dp, height = 80.dp).clip(RoundedCornerShape(30.dp))
                )
                Text("The beginning of a GOAT", color = Color.DarkGray, fontWeight = FontWeight.Bold)
            }

            Icon(imageVector = Icons.Filled.Person, contentDescription = null,
                tint = Color.Gray,
                modifier= Modifier.align(Alignment.BottomStart).offset(x=(15.dp), y=(-15).dp)
            )

        }
    }

}