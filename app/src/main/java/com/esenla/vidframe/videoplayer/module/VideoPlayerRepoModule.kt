package com.esenla.vidframe.videoplayer.module

import com.esenla.vidframe.videoplayer.data.VideoDataRepoImpl
import com.esenla.vidframe.videoplayer.domain.VideoDataRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class VideoPlayerRepoModule {

    @Binds
    abstract fun bindVideoRepo(imp: VideoDataRepoImpl): VideoDataRepo
}