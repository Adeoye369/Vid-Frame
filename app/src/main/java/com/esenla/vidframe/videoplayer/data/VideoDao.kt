package com.esenla.vidframe.videoplayer.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {

    @Query("SELECT * FROM videos")
    fun getAllVideos(): Flow<List<VideoEntity>>

    @Upsert
    suspend fun insert(vid: VideoEntity)

    @Update
    suspend fun update(vid: VideoEntity)

    @Delete
    suspend fun delete(vid: VideoEntity)
}