package com.esenla.vidframe.videoplayer.domain

import android.net.Uri
import kotlinx.coroutines.flow.Flow

interface VideoDataRepo {
    fun getVideoList() : Flow<List<VideoData>>

    suspend fun addVideo( vidUri: Uri)
    suspend fun updateVideo(vid : VideoData)
    suspend fun deleteVideo(vid: VideoData)
}