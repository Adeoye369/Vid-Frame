    package com.esenla.vidframe.videoplayer.module

    import android.content.Context
    import androidx.media3.exoplayer.ExoPlayer
    import androidx.room.Room
    import com.esenla.vidframe.videoplayer.data.VideoDataDao
    import com.esenla.vidframe.videoplayer.data.VidFrameDatabase
    import dagger.Module
    import dagger.Provides
    import dagger.hilt.InstallIn
    import dagger.hilt.android.components.ViewModelComponent
    import dagger.hilt.android.qualifiers.ApplicationContext
    import dagger.hilt.android.scopes.ViewModelScoped
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

    @InstallIn(ViewModelComponent::class)
    @Module
    object VideoPlayerModule {

        @Provides
        @ViewModelScoped // Creates 1 player instance per ViewModel instance
        fun provideExoPlayer(@ApplicationContext app: Context): ExoPlayer {
            return ExoPlayer.Builder(app).build()
        }
    }