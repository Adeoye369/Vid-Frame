package com.esenla.vidframe.videoplayer.domain

import android.net.Uri

data class VideoData(
    val id: Int = 0,
    val uri: Uri? = Uri.EMPTY,
    val name: String? = "",
    val duration: Int? = 0
)
