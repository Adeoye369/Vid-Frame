package com.esenla.vidframe.videoplayer.module

import android.content.Context
import androidx.room.Room
import com.esenla.vidframe.videoplayer.data.VideoDataDao
import com.esenla.vidframe.videoplayer.data.VidFrameDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object VideoPlayerAppModule {

    @Provides
    fun provideVideoDatabase(@ApplicationContext app: Context) : VidFrameDatabase{
        return Room.databaseBuilder(
            app,
            VidFrameDatabase::class.java,
            "videos_db"
        )
//            .fallbackToDestructiveMigration(true) // 👈 Destructive fallback added
            .build()
    }

    @Provides
    fun provideVideoDao(db : VidFrameDatabase) : VideoDataDao{
        return  db.videoDao()
    }
}