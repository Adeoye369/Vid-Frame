package com.esenla.vidframe.videoplayer.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDataDao {

    @Query("SELECT * FROM videos")
    fun getAllVideos(): Flow<List<VideoDataEntity>>

    @Upsert
    suspend fun insert(vid: VideoDataEntity)

    @Update
    suspend fun update(vid: VideoDataEntity)

    @Query("DELETE FROM videos WHERE id = :videoId")
    suspend fun deleteById(videoId: Int)
}