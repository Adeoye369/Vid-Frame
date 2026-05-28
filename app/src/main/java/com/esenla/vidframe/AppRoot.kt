package com.esenla.vidframe

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.esenla.vidframe.videoplayer.ui.VideoListScreen
import com.esenla.vidframe.videoplayer.ui.VideoPlayScreen
import kotlinx.serialization.Serializable


// VP => Video Player
sealed interface VPRoute : NavKey{
    @Serializable
    data object VideoHome: VPRoute
    @Serializable
    data class VideoListPlay(val uri: String): VPRoute
}

@Composable
fun AppRoot( modifier: Modifier = Modifier) {

    //    NavDrawer Demo Comment
    //    NavDrawerDemo()
    //    ListSelectedDir()
    //    ListVideoMediaStoreScreen()

    val backStack = remember{mutableStateListOf<Any>(VPRoute.VideoHome)}

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },

        entryProvider = entryProvider {
            entry<VPRoute.VideoHome>{
                VideoListScreen(
                    onVideoClick = {uriString ->
                        backStack.add(VPRoute.VideoListPlay(uri = uriString))
                    }
                )
            }

            entry<VPRoute.VideoListPlay> { key ->
                VideoPlayScreen(key.uri)
            }
        }


    )
}

