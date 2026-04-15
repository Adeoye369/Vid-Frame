package com.esenla.vidframe

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable

@Composable
fun ListVideoMediaStore(){

}

data class Video(val uri: Uri, val name: String, val duration: Int)

fun getVideo(context: Context){

}