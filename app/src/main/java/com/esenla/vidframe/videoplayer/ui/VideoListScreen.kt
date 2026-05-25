package com.esenla.vidframe.videoplayer.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxDefaults
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.esenla.vidframe.videoplayer.presentation.VideoListViewModel


@Composable
fun VideoListScreen(
    videoViewModel: VideoListViewModel = hiltViewModel()
) {
    val allvideos by videoViewModel.videoList.collectAsStateWithLifecycle()

    val videoPick = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->

        uri?.let {
            println("Selected Uri: $uri")
            videoViewModel.addVideo(uri)
        }

    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                videoPick.launch(arrayOf("video/*"))
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(items = allvideos, key = { it.id }) { vidItem ->


                // 1. Define the swipe behavior and trigger your ViewModel
                val dismissState = rememberSwipeToDismissBoxState(
                    initialValue = SwipeToDismissBoxValue.Settled,
                   positionalThreshold =  SwipeToDismissBoxDefaults.positionalThreshold

                )

                if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                    videoViewModel.delete(vidItem)
                    true // Confirm and dismiss row
                } else {
                    false // Ignore start-to-end swipes
                }

                SwipeToDismissBox(
                    state = dismissState,
                    // 2. The background layout revealed while swiping (e.g. Red Trash Icon)
                    backgroundContent = {
                        val color = when (dismissState.targetValue) {
                            SwipeToDismissBoxValue.EndToStart -> Color.Red.copy(alpha = 0.8f)
                            else -> Color.Transparent
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color)
                                .padding(horizontal = 20.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Video",
                                tint = Color.White
                            )
                        }
                    },
                    // 3. Keep your original item UI as the foreground content
                    enableDismissFromStartToEnd = false // Only swipe left to right
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Row(modifier = Modifier.padding(16.dp)) {
                            Column(Modifier.weight(1f)) {
                                vidItem.name?.let {
                                    Text(
                                        it,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                vidItem.duration?.let { Text("$it") }
                            }
                        }
                    }
                }

                HorizontalDivider(thickness = 2.dp, color = DividerDefaults.color)
            }
        }
    }
}




