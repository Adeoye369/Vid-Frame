package com.esenla.vidframe

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.esenla.vidframe.navdrawer.NavDrawerDemo
import com.esenla.vidframe.videoplayer.ListVideoMediaStoreScreen
import com.esenla.vidframe.videoplayer.ui.VideoListScreen


@Composable
fun AppRoot( modifier: Modifier = Modifier) {

    // NavDrawer Demo Comment
//    NavDrawerDemo()

//    ListSelectedDir()

//    ListVideoMediaStoreScreen()

    VideoListScreen()
}

