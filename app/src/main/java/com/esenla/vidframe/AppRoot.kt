package com.esenla.vidframe

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.esenla.vidframe.videoplayer.presentation.VideoListViewModel
import com.esenla.vidframe.videoplayer.presentation.VideoPlayerViewModel
import com.esenla.vidframe.videoplayer.ui.VideoListScreen
import com.esenla.vidframe.videoplayer.ui.VideoPlayScreen
import kotlinx.serialization.Serializable
import kotlin.collections.listOf


// VP => Video Player
sealed interface VPRoute : NavKey{
    @Serializable
    data object VideoHome: VPRoute
    @Serializable
    data class VideoPlay(val uri: String): VPRoute
}

@Composable
fun AppRoot( modifier: Modifier = Modifier) {

    val backStack = remember{mutableStateListOf<Any>(VPRoute.VideoHome)}
    val sharedViewModel = hiltViewModel<VideoListViewModel>()

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },

        // Defines directional slide animations (slide in from right, slide out to left)
        transitionSpec = {
            slideInHorizontally(tween(300), { it }) togetherWith slideOutHorizontally(tween(300), { -it })
        },

        // Defines reverse slide animations for backward navigation
        popTransitionSpec = {
            slideInHorizontally(tween(300), { -it }) togetherWith slideOutHorizontally(tween(300), { it })
        },

        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),


        entryProvider = entryProvider {
            entry<VPRoute.VideoHome>{
                VideoListScreen(
                    videoViewModel = hiltViewModel<VideoListViewModel>(),
                    onVideoClick = {uriString ->
                        backStack.add(VPRoute.VideoPlay(uri = uriString))
                    }
                )
            }

            entry<VPRoute.VideoPlay> { key ->
                VideoPlayScreen(key.uri ,
                    viewModel = hiltViewModel<VideoPlayerViewModel>(),
                    onBackClick = {backStack.removeLastOrNull()})
            }
        },

//        // 2. Use transitionSpec for forward navigation (slide left)
//        transitionSpec = {
//            slideInHorizontally(
//                animationSpec = tween(durationMillis = 300),
//                initialOffsetX = { it } // Slides in from the right
//            ) togetherWith slideOutHorizontally(
//                animationSpec = tween(durationMillis = 300),
//                targetOffsetX = { -it } // Slides out to the left
//            )
//        },
//
//        // 3. Use popTransitionSpec for backward navigation (slide right)
//        popTransitionSpec = {
//            slideInHorizontally(
//                animationSpec = tween(durationMillis = 300),
//                initialOffsetX = { -it } // Slides in from the left
//            ) togetherWith slideOutHorizontally(
//                animationSpec = tween(durationMillis = 300),
//                targetOffsetX = { it } // Slides out to the right
//            )
//        },



    )
}

