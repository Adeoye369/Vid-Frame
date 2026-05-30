package com.esenla.vidframe.videoplayer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.esenla.vidframe.R
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerUi(
    isPlaying : Boolean,
    currentPosition: Long,
    duration: Long,
    isBuffering: Boolean,
    onSeekBarPositionChange: (Long)-> Unit,
    onSeekBarPositionChangeFinished: (Long) -> Unit,
    onPlayPauseClick: () -> Unit

){
    // Overall Box - Apply Gradient Overlay drawing
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(listOf(Color.Transparent, Color.Black))
            ),
        contentAlignment = Alignment.Center
    ){
        // Display Circular Progress indicator
        if(isBuffering){
            CircularProgressIndicator(strokeWidth = 5.dp, trackColor = ProgressIndicatorDefaults.circularColor,
                modifier = Modifier.size(20.dp))
        }


        // Display play and pause
        IconButton(
            onClick = onPlayPauseClick,
            modifier = Modifier.size(100.dp)
        ) {
            Icon(
                imageVector = if (isPlaying) ImageVector.vectorResource(R.drawable.play_24)
                                else ImageVector.vectorResource(R.drawable.play_24),
                contentDescription = if(isPlaying) "Pause" else "Play",
                modifier = Modifier.size(50.dp),
                tint = Color(0.5f, 0.5f, 0.5f, 1.0f)
            )

        } // end IconButton

        //Player Slide
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(alignment = Alignment.BottomCenter).padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(formatDuration(currentPosition), color= Color.White)
            Slider(
                value=currentPosition.toFloat(),
                valueRange = 0f .. duration.toFloat(),
                onValueChange = {newPos-> onSeekBarPositionChange(newPos.toLong())},
                onValueChangeFinished = {onSeekBarPositionChangeFinished(currentPosition)},

                thumb = {
                    Box(modifier = Modifier.size(15.dp)
                        .shadow( 4.dp, CircleShape).background(Color.White))
                },
                track = { sliderState ->
                    Box(modifier = Modifier.fillMaxWidth().height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(color= MaterialTheme.colorScheme.surfaceVariant)){
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(sliderState.value / duration)
                                .fillMaxHeight()
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                },
                
                 modifier = Modifier.weight(1f)
            )
            Text(formatDuration(duration), color= Color.White)
        }// end Row - Slider


    }// end Box

} // end PlayerUi

// format playtime duration
fun formatDuration(millis: Long): String{
    val totalSecs = millis / 1000
    val hours = totalSecs / 3600
    val minutes = (totalSecs % 3600) / 60
    val secs = totalSecs % 60



    return if(hours > 0)
        String.format(Locale.US, "%d:%02d:%02d", hours, minutes, secs)
            else
        String.format(Locale.US, "%02d:%02d", minutes, secs)

}

fun frameFormat(millis: Long) : String {
    return ""
}