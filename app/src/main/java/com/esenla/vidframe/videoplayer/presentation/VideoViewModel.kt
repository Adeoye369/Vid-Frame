package com.esenla.vidframe.videoplayer.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esenla.vidframe.videoplayer.domain.Video
import com.esenla.vidframe.videoplayer.domain.VideoRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(private val videoRepo: VideoRepo): ViewModel() {

    // read all videos
    val video_list = videoRepo.getVideoList().stateIn(
        viewModelScope, SharingStarted.Lazily, emptyList())


    fun addVideo(uri: Uri){
        viewModelScope.launch{
            videoRepo.addVideo(uri)
        }
    }


}