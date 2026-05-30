package com.esenla.vidframe.videoplayer.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.media3.common.Player
import androidx.media3.ui.compose.ContentFrame
import com.esenla.vidframe.videoplayer.presentation.VideoListViewModel
import kotlinx.coroutines.delay

@Composable
fun VideoPlayScreen(
    videoUri: String ="",
    viewModel: VideoListViewModel= hiltViewModel(),
    onBackClick: () -> Unit // 1. Added navigation callback parameter
) {

    var isPlaying by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(0L) }
    var isSeeking by remember { mutableStateOf(false) }
    var isBuffering by remember { mutableStateOf(false) }
    var isPlayerUiVisible by remember { mutableStateOf(false) }


    // Helper lambda to safely pause the player and exit the screen
    val safeExitAndBack = {
        viewModel.player.stop() // Instantly kill audio playback
        viewModel.player.clearMediaItems()
        onBackClick()            // Navigate back
    }

    // 2. Intercepts hardware back button or swipe-to-back gesture
    BackHandler {
        safeExitAndBack()
    }

    // Start video playback when the screen loads
    LaunchedEffect(videoUri) {
        if (videoUri.isNotEmpty()) {
            viewModel.playVideo(videoUri)
        }
    }


    // Handle Player state updates safely with standard DisposableEffect
    DisposableEffect(viewModel.player) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                super.onIsPlayingChanged(playing)
                isPlaying = playing
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                isBuffering = playbackState == Player.STATE_BUFFERING
                if (playbackState == Player.STATE_READY) {
                    duration = viewModel.player.duration.coerceAtLeast(0)
                }
            }

        }
        viewModel.player.addListener(listener)

        onDispose {
            viewModel.player.removeListener(listener)
            viewModel.player.release()
        }
    }

    LaunchedEffect(isPlayerUiVisible, isSeeking, isPlaying) {
        delay(5000L) // if after 5secs we are not seeking hide
        if (!isSeeking) {
            isPlayerUiVisible = false
        }
    }

    LaunchedEffect(viewModel.player, isPlaying, isSeeking) {
        while (isPlaying) {
            if (!isSeeking)
                currentPosition = viewModel.player.currentPosition.coerceAtLeast(0)
            delay(16L)
        }
    }

    // Column to hold video
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        // Content Frame container
        Box(
            modifier = Modifier.align(Alignment.Center)

        ) {
            ContentFrame(
                player = viewModel.player,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {
                        isPlayerUiVisible = !isPlayerUiVisible
                    }
            )

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                AnimatedVisibility(
                    visible = isPlayerUiVisible,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // 3. Top Action Bar row layout
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.TopStart)
                                .padding(vertical = 30.dp, horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { safeExitAndBack() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White
                                )
                            }

                            // Pushes the rotation button completely to the top-right corner
                            Box(modifier = Modifier.weight(1f))

                            // 4. Manual Rotation UI Click Trigger
                            IconButton(
                                onClick = { }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Rotate Video",
                                    tint = Color.White
                                )
                            }
                        }

                        PlayerUi(
                            isPlaying = isPlaying,
                            duration = duration,
                            currentPosition = currentPosition,
                            isBuffering = isBuffering,
                            onSeekBarPositionChange = { newPos ->
                                isSeeking = true
                                currentPosition = newPos
                            },
                            onSeekBarPositionChangeFinished = { currentPos ->
                                viewModel.player.seekTo(currentPos)
                                isSeeking = false
                            },
                            onPlayPauseClick = {
                                when {
                                    !isPlaying && viewModel.player.playbackState == Player.STATE_ENDED -> {
                                        viewModel.player.seekTo(0)
                                        viewModel.player.play()
                                    }

                                    !isPlaying -> viewModel.player.play()
                                    isPlaying -> viewModel.player.pause()
                                }
                            }
                        )
                    }
                }


            }
        }
    }
}