package com.esenla.vidframe.videoplayer.presentation

import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    val player : ExoPlayer
) : ViewModel(){
    fun playVideo(uri: String) {

        val mediaItem = MediaItem.fromUri(uri)

        // 1. Clears the previous video timeline, position history, and tracks
        player.stop()
        player.clearMediaItems()

        // 2. Load the new video and force it to start at index 0, position 0
        player.setMediaItem(mediaItem)
        player.seekTo(0, 0L)

        // 3. Prepare and stream
        player.prepare()
        player.play()
    }

    override fun onCleared( ) {
        player.release() // Properly releases the @ViewModelScoped player instance
    }


}