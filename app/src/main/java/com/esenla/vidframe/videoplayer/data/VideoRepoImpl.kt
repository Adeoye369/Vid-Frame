package com.esenla.vidframe.videoplayer.data

import android.net.Uri
import com.esenla.vidframe.videoplayer.domain.Video
import com.esenla.vidframe.videoplayer.domain.VideoRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class VideoRepoImpl @Inject constructor(
    private val videoDao: VideoDao
): VideoRepo {
    override fun getVideoList(): Flow<List<Video>> {
        return videoDao.getAllVideos().map { flow ->
            flow.map { entity ->
                Video( uri = entity.uri)
            }
        }
    }

    override suspend fun addVideo(vidUri: Uri) {
        videoDao.insert(VideoEntity(uri = vidUri))
    }

    override suspend fun updateVideo(vid: Video) {
        videoDao.update(VideoEntity(id=vid.id, uri=vid.uri))
    }

    override suspend fun deleteVideo(vid: Video) {
        videoDao.delete(VideoEntity(id=vid.id, uri = vid.uri))
    }
}