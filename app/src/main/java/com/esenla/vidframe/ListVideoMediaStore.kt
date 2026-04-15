package com.esenla.vidframe


import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import android.graphics.Canvas
import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap


fun isAndroid10andAbove() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

fun createBitmapLocal(): ImageBitmap{
    // create mutable bitmap
    val bitmap = createBitmap(100, 100)

    // set color with canvas
    val canvas = Canvas(bitmap)
    canvas.drawColor(Color.BLUE)

    return bitmap.asImageBitmap()
}
@Composable
fun ListVideoMediaStore(){
    val context = LocalContext.current
    val videoFiles = getVideo(context)
    LazyColumn {
        items(videoFiles) { video ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(vertical = 5.dp)) {
                    val imageBitmap = getVideoThumbnail(video.uri, context)?.asImageBitmap()
                    Image(bitmap = imageBitmap as ImageBitmap, contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(100.dp))

                    Column() {
                        Text(video.name, style = MaterialTheme.typography.bodyLarge,maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("${ video.duration }")
                    }
                }
            }
        }
    }
}

data class Video(val uri: Uri, val name: String, val duration: Int)
fun getVideo(context: Context): List<Video>{
    // video list
    val videoList = mutableListOf<Video>()

    // Select collection based on android version {Basically Table}
    val collection =
        if (isAndroid10andAbove())
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        else
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI

    // Projection are basic, synonymous to fields column
    val projection = arrayOf(
        MediaStore.Video.Media._ID,
        MediaStore.Video.Media.DISPLAY_NAME,
        MediaStore.Video.Media.DURATION
    )


    // Query the context
    context.contentResolver.query(
        collection, projection,
        null, null,
        "${ MediaStore.Video.Media.DATE_ADDED } DESC"
    )?.use{cursor ->
        val idCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
        val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
        val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)

        while (cursor.moveToNext()){
            val id = cursor.getLong(idCol)
            val contentUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
            videoList.add(Video(contentUri,  cursor.getString(nameCol), cursor.getInt(durationCol)) )
        }
    }

    return videoList
}

fun getVideoThumbnail(uri : Uri, context: Context): Bitmap? {

    val retriever = MediaMetadataRetriever()
    return try {
            retriever.setDataSource(context, uri)
            retriever.getFrameAtTime(2000000) // get frame at 2sec (in microseconds)

    }catch (e: Exception){
        null
    }finally {
        retriever.release()
    }
}