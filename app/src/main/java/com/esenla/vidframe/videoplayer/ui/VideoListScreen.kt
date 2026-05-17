package com.esenla.vidframe.videoplayer.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.esenla.vidframe.videoplayer.presentation.VideoViewModel


@Composable
fun VideoListScreen(
    videoViewModel: VideoViewModel = hiltViewModel()
){
    val allvideos by videoViewModel.video_list.collectAsStateWithLifecycle()

    val videoPick = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->

        uri?.let {
                videoViewModel.addVideo(uri)
            }

    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                videoPick.launch(
                    input = PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.VideoOnly)
                )
            }) {
                Icon(imageVector = Icons.Default.Add , contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(allvideos){  vid ->
                Text("${vid.uri }")
                HorizontalDivider(thickness = 2.dp, color = DividerDefaults.color)

            }
        }

    }

}


@Composable
fun AddVideoFromDevice(){


}