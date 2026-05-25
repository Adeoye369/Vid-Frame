package com.esenla.vidframe.videoplayer.data

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import com.esenla.vidframe.videoplayer.domain.VideoData
import com.esenla.vidframe.videoplayer.domain.VideoDataRepo
import com.esenla.vidframe.videoplayer.isAndroid10andAbove
import com.esenla.vidframe.videoplayer.isAndroid11andAbove
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class VideoDataRepoImpl @Inject constructor(
    private val videoDataDao: VideoDataDao,
    @param:ApplicationContext private val context : Context
): VideoDataRepo {
    override fun getVideoList(): Flow<List<VideoData>> {
        return videoDataDao.getAllVideos().map { flow ->
            flow.map { entity ->
                VideoData(
                    id = entity.id,
                    uri = entity.uri,
                    name = entity.name,
                    duration = entity.duration
                )
            }
        }
    }

    override suspend fun addVideo(vidUri: Uri) {

        val videoData = getSelectedVideo(uri = vidUri, context = context)
        videoData?.duration?.let { Log.d("VFD", it.toString()) }
        videoDataDao.insert(VideoDataEntity(
            uri = videoData?.uri,
            name = videoData?.name,
            duration = getDurationFromUri(vidUri)
        ))
    }

    override suspend fun updateVideo(vid: VideoData) {
        videoDataDao.update(VideoDataEntity(
            id =vid.id,
            uri =vid.uri,
            name = vid.name,
            duration = vid.duration
        ))
    }

    override suspend fun deleteVideo(vid: VideoData) {
        videoDataDao.deleteById(vid.id)
    }


    // Get the video Duration from Uri
    fun getDurationFromUri(uri: Uri): Int{
            val retriever = MediaMetadataRetriever()
            return try {
                retriever.setDataSource(context, uri)
                val durationString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                durationString?.toIntOrNull()?: 0L

            }catch (e: Exception){
                null
            }finally {
                retriever.release()
            } as Int

    }

    // Get Video Thumbnail from Uri
    fun getVideoThumbnailFromUri(uri : Uri): Bitmap? {

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


    fun getSelectedVideo(context: Context, uri: Uri): VideoData?{


        // Projection are basic, synonymous to {fields column}
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME
        )

        // Query the context
        val vid = context.contentResolver.query(
            uri,
            projection,
            null,
            null,
            null
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)

            if(cursor.moveToFirst()) {


                VideoData(
                    uri = uri,
                    name = cursor.getString(nameCol)
                )
            }
            else{
                null
            }

        }

        return vid

    }

    fun getVideosFromDirectory(context: Context, selectedRelativePath: String? = null): List<VideoData>{
        // video list
        val videoList = mutableListOf<VideoData>()


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
                    VideoData(
                        uri = contentUri,
                        name = cursor.getString(nameCol),
                        duration = cursor.getInt(durationCol)
                    )
                )
            }
        }

        return videoList
    }

}