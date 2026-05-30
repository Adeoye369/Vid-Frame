package com.esenla.vidframe.videoplayer

import android.content.Context
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.RetainedEffect
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.ContentFrame
import com.esenla.vidframe.videoplayer.ui.PlayerUi
import kotlinx.coroutines.delay

@Composable
fun MediaDisplayVideo(context: Context, uri: Uri?){


    val player = retain{
        ExoPlayer.Builder(context.applicationContext).build()
    }

    var isPlaying by retain { mutableStateOf(false) }
    var currentPosition by retain { mutableLongStateOf(0L) }
    var duration by retain { mutableLongStateOf(0L) }
    var isSeeking by retain { mutableStateOf(false) }
    var isBuffering by retain { mutableStateOf(false) }
    var isPlayerUiVisible by retain { mutableStateOf(false) }

//    val videoLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.PickVisualMedia()
//    ) {
//            uri -> uri?.let {
//        player.setMediaItem(MediaItem.fromUri(uri))
//        player.prepare()
//        player.play()
//    }
//
//    }

    LaunchedEffect(uri) {
        uri?.let {
            player.setMediaItem(MediaItem.fromUri(uri))
            player.prepare()
            player.play()
        }
    }


    RetainedEffect(key1=player){
        val listener = object : Player.Listener{
            override fun onIsPlayingChanged(playing: Boolean) {
                super.onIsPlayingChanged(playing)
                isPlaying = playing
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                isBuffering = playbackState == Player.STATE_BUFFERING
                if(playbackState == Player.STATE_READY ){
                    duration = player.duration.coerceAtLeast(0)
                }
            }

        }
        player.addListener(listener)

        onRetire(){
            player.removeListener(listener)
            player.release()
        }
    }

    LaunchedEffect( isPlayerUiVisible, isSeeking, isPlaying) {
        delay(5000L) // if after 5secs we are not seeking hide
        if(!isSeeking) {
            isPlayerUiVisible = false
        }
    }

    LaunchedEffect(player, isPlaying, isSeeking) {
        while (isPlaying){
            if(!isSeeking)
                currentPosition = player.currentPosition.coerceAtLeast(0)
            delay(16L)
        }
    }

    // Column to hold video
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
//        // Picker Button
//        Button(
//            onClick = {
//                videoLauncher.launch(
//                    input = PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.VideoOnly)
//                )
//            }
//        ) {
//            Text("PickVideo")
//        }

        // Content Frame container
        Box(
            modifier= Modifier.align (Alignment.Center)

        ){
            ContentFrame(
                player=player,
                modifier= Modifier.fillMaxWidth()
                    .clickable(
                        interactionSource = null,
                        indication = null,
                    ){
                        isPlayerUiVisible = !isPlayerUiVisible
                    }
            )

            Column(
                modifier= Modifier.fillMaxSize()
            ) {
                AnimatedVisibility(
                    visible = isPlayerUiVisible,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    PlayerUi(
                        isPlaying = isPlaying,
                        duration = duration,
                        currentPosition = currentPosition,
                        isBuffering = isBuffering,
                        onSeekBarPositionChange = { newPos ->
                            isSeeking = true
                            currentPosition = newPos
                        },
                        onSeekBarPositionChangeFinished = { curentPos ->
                            player.seekTo(curentPos)
                            isSeeking = false
                        },
                        onPlayPauseClick = {
                            when {
                                !isPlaying && player.playbackState == Player.STATE_ENDED -> {
                                    player.seekTo(0)
                                    player.play()
                                }

                                !isPlaying -> player.play()
                                isPlaying -> player.pause()
                            }
                        }
                    )
                }
            }

        }// end Box


    }

}

