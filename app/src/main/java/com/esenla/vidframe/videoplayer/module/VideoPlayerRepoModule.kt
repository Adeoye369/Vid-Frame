package com.esenla.vidframe.videoplayer.module

import com.esenla.vidframe.videoplayer.data.VideoRepoImpl
import com.esenla.vidframe.videoplayer.domain.VideoRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class VideoPlayerRepoModule {

    @Binds
    abstract fun bindVideoRepo(imp: VideoRepoImpl): VideoRepo
}