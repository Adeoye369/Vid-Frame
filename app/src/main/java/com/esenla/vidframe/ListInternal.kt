package com.esenla.vidframe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.esenla.vidframe.ui.theme.Purple40
import java.io.File
import java.io.IOException

@Composable
fun ListInternal (){
    val filesList = internalFoldersAndFilesList()
    LazyColumn(
        modifier= Modifier
            .fillMaxSize()
            .background(Purple40),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item(){

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                if(filesList.isEmpty())
                {
                    Text("no Internal files/directory is empty")
                }
                SaveFileToInternalDir()
            }
        }
        items(filesList){ file ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(if (file.isDirectory) "📁 - ${file.name}" else "📝 - ${file.name}",
                    style = TextStyle(
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Normal,
                        fontSize = 30.sp,
                        lineHeight = 24.sp,
                        letterSpacing = 0.5.sp
                    ))
            }
        }
    }

}

@Composable
fun internalFoldersAndFilesList(): List<File>{
    val context = LocalContext.current

    // Access App specific external directory
    val folder = context.getExternalFilesDir(null)
    val files = folder?.listFiles()?.toList() ?: emptyList()

    return files
}


@Composable
fun SaveFileToInternalDir() {

    val context = LocalContext.current
    Button(
        onClick = {

            //  create folder and file name
            val picFolderName = "Fold/2026/April"
            val filename = "Pic001.txt"

            // Get App specific Directory
            val appBaseDir = context.getExternalFilesDir(null) // com.esenla.vidframe/files
            val picFolder = File(appBaseDir, picFolderName)

            // Create directory if not exist
            if (!picFolder.exists()) picFolder.mkdirs()

            // Create and write to file Directory
            val picFile = File(picFolder, filename)

            try {
                picFile.writeText("This is file For Pic001")
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    ) {
        Text("Create Files")
    }
}