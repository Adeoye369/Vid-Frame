package com.esenla.vidframe.videoplayer.data

import android.net.Uri
import androidx.core.net.toUri
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

@Entity(tableName = "videos")
data class VideoDataEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val uri: Uri? ,
    val name: String? ,
    val duration: Int?
)

class UriConverters {
    @TypeConverter
    fun fromUri(uri: Uri?): String? = uri?.toString()

    @TypeConverter
    fun toUri(uriString: String?): Uri? = uriString?.toUri()
}
