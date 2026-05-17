package com.esenla.vidframe.videoplayer


import android.content.ContentUris
import android.content.Context
import android.content.Intent
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
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import android.graphics.Canvas
import android.graphics.Color
import android.util.Log
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


fun isAndroid11andAbove() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
fun isAndroid10andAbove() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

data class VideoMetaData(
    val uri: Uri,
    val name: String,
    val duration: Int
)


fun createBitmapLocal(): ImageBitmap{
    // create mutable bitmap
    val bitmap = createBitmap(100, 100)

    // set color with canvas
    val canvas = Canvas(bitmap)
    canvas.drawColor(Color.BLUE)

    return bitmap.asImageBitmap()
}
@Composable
fun ListVideoMediaStoreScreen(){
    val context = LocalContext.current
    var videoFiles : List<VideoMetaData> by remember { mutableStateOf(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var videoClick by remember {mutableStateOf(false)}
    var selectedRelativePath: String? by remember { mutableStateOf("") }

    var fileLaucher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) {
        uri ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            selectedRelativePath = getRelativePathFromDocumentTreeUri(it)
            Log.d("MVM", "Selected Uri: ${it.path}")
        }

    }


//    val videoFiles = getVideo(context)
    LaunchedEffect(videoFiles, selectedRelativePath) {
            isLoading = true
            videoFiles = withContext(Dispatchers.IO) {
                getVideo(context, selectedRelativePath)
            }

            isLoading = false
    }

    if(isLoading) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(strokeWidth = 10.dp, modifier = Modifier.size(200.dp))
        }
    }
    else
        LazyColumn {

            items(videoFiles) { video ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(vertical = 5.dp)
                        .clickable{
                            videoClick = true
                        }
                    ) {
                        if (videoClick){
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                Alignment.Center
                            ){
                                MediaDisplayVideo(context, uri = video.uri)
                            }
                        }
                        val imageBitmap = getVideoThumbnail(video.uri, context)?.asImageBitmap()

                        Image(bitmap = imageBitmap as ImageBitmap, contentDescription = "",
                            contentScale = ContentScale.Crop,
                             modifier = Modifier.size(100.dp)
                                    )
                        Column{
                            Text(video.name, style = MaterialTheme.typography.bodyLarge,maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("${ video.duration }")
                        }


                    }
                }
            }

    }


    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()){
        IconButton(onClick = {
            fileLaucher.launch(null)
        }) {
            Icon(Icons.Default.AddCircle, "",
                modifier = Modifier.size(200.dp))
        }
    }
}


/**
 * @param {Context} context                  - Current App Context
 * @param {String?} selectedRelativePath     - Selected path by user
 * @return {List<VideoMetaData>}                     - List of VideoMetaData Data */
fun getVideo(context: Context, selectedRelativePath: String? = null): List<VideoMetaData>{
    // video list
    val videoList = mutableListOf<VideoMetaData>()


    // Select collection based on android version {Basically Table}

    val collection =  // search all External Volume directory {Database}
        if (isAndroid11andAbove()) MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        else MediaStore.Video.Media.EXTERNAL_CONTENT_URI

    // Projection are basic, synonymous to {fields column}
    val projection = arrayOf(
        MediaStore.Video.Media._ID,
        MediaStore.Video.Media.DISPLAY_NAME,
        MediaStore.Video.Media.DURATION
    )

//    Log.d("MVM", "Download Log: $coll \n Volume Dir: $collection, \nSelectedFolder: $selectedRelativePath")

    // Filter for files located specifically in the Download directory
    val selection = if(selectedRelativePath?.isNotEmpty() == true) "${MediaStore.Video.Media.RELATIVE_PATH} LIKE ?" else null
    val selectionArgs = if(selectedRelativePath?.isNotEmpty() == true ) arrayOf("${selectedRelativePath}/%") else null

    // Query the context
    context.contentResolver.query(
        collection,
        projection,
        selection,
        selectionArgs,
        "${ MediaStore.Video.Media.DATE_ADDED } DESC"
    )?.use{cursor ->
        val idCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
        val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
        val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)

        while (cursor.moveToNext()){

            val contentUri = ContentUris.withAppendedId(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                cursor.getLong(idCol)
            )

            videoList.add(
                VideoMetaData(
                    uri = contentUri,
                    name = cursor.getString(nameCol),
                    duration = cursor.getInt(durationCol)
                )
            )
        }
    }

    return videoList
}

fun getVideoThumbnail(uri : Uri, context: Context): Bitmap? {

   return if(isAndroid10andAbove()){
       try {
        context.contentResolver.loadThumbnail(uri, Size(320, 240), null)
       }catch (e: Exception){
           null
       }
    } else{
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(context, uri)
            retriever.getFrameAtTime(2000000) // get frame at 2sec (in microseconds)

        }catch (e: Exception){
            null
        }finally {
            retriever.release()
        }
    }


}

fun getRelativePathFromDocumentTreeUri(treeUri: Uri): String? {
    val path = treeUri.path ?: return null

    // SAF URIs often look like /tree/primary:Folder01/subFolder01/MyFolder
    // We need only the "Folder01/subFolder01/MyFolder" path
    return if(path.contains(":")){
       val split =  path.split(":")
        if(split.size > 1) return split[1] else null
    } else null

}