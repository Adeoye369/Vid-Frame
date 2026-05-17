package com.esenla.vidframe.videoplayer.domain

import android.net.Uri
import kotlinx.coroutines.flow.Flow

interface VideoRepo {
    fun getVideoList() : Flow<List<Video>>

    suspend fun addVideo( vidUri: Uri)
    suspend fun updateVideo(vid : Video)
    suspend fun deleteVideo(vid: Video)
}