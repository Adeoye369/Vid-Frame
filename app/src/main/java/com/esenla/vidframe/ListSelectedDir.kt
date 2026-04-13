package com.esenla.vidframe

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.documentfile.provider.DocumentFile



/**
 * Read User-Selected Directories (SAF)
 * ==================================
 * To access public folders like "Downloads" or custom directories, use "ActivityResultContracts.OpenDocumentTree".
 * This returns a Uri that you can use to list files via "DocumentFile" */

@Composable
fun ListSelectedDir(){

    val context = LocalContext.current
    var directoryUri by remember{ mutableStateOf<Uri?>(null) }

    val fileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        directoryUri = uri
    }

    Box(modifier=Modifier.fillMaxSize()
        .background(Color(0.251f, 0.384f, 0.298f, 1.0f)),
        contentAlignment = Alignment.Center) {
        Column() {
            Button(onClick = { fileLauncher.launch(null) }) { Text("Select Folder")}
            SelectedFolderContent(context, directoryUri)
        }
    }


}

/**
 * List Files from the Selected URI* */
@Composable
fun SelectedFolderContent(context: Context, uri: Uri?,){
    val documentFile = uri?.let { DocumentFile.fromTreeUri(context, it) }
    val files = documentFile?.listFiles() ?: emptyArray()

    LazyColumn {
        items(files) { file ->
            Text(text = "${if (file.isDirectory) "📁" else "📄"} ${file.name}")
        }
    }

}


