package com.esenla.vidframe.videoplayer.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [VideoDataEntity::class],
    exportSchema = false,
    version = 1
//    autoMigrations = [AutoMigration(from = 1, to = 2)],

)
@TypeConverters(UriConverters::class)
abstract class VidFrameDatabase: RoomDatabase() {
    abstract fun videoDao() : VideoDataDao
}