package com.esenla.vidframe.videoplayer.module

import android.content.Context
import androidx.room.Room
import com.esenla.vidframe.videoplayer.data.VideoDao
import com.esenla.vidframe.videoplayer.data.VideoDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object VideoPlayerAppModule {

    @Provides
    fun provideVideoDatabase(@ApplicationContext app: Context) : VideoDatabase{
        return Room.databaseBuilder(
            app,
            VideoDatabase::class.java,
            "videos_db"
        ).build()
    }

    @Provides
    fun provideVideoDao(videoDatabase : VideoDatabase) : VideoDao{
        return  videoDatabase.videoDao()
    }
}