package com.esenla.vidframe.videoplayer.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.net.URI

@Database(entities = [VideoEntity::class], exportSchema = false, version = 1)
@TypeConverters(UriConverters::class)
abstract class VideoDatabase: RoomDatabase() {
    abstract fun videoDao() : VideoDao
}