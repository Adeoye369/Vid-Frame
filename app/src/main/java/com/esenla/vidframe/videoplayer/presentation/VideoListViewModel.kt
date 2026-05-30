package com.esenla.vidframe.videoplayer.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.esenla.vidframe.videoplayer.domain.VideoData
import com.esenla.vidframe.videoplayer.domain.VideoDataRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoListViewModel @Inject constructor(
    private val videoRepo: VideoDataRepo,
    val player : ExoPlayer
): ViewModel() {

    // read all videos
    val videoList = videoRepo.getVideoList().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun addVideo(uri: Uri){
        viewModelScope.launch{
            videoRepo.addVideo(uri)
        }
    }

    fun delete(video: VideoData){
        viewModelScope.launch(Dispatchers.IO) {
            videoRepo.deleteVideo(video)
        }
    }

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