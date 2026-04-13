package com.esenla.vidframe

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun ListRootFoldersAndFiles (){
    val context = LocalContext.current

    // First time toast
    var firstTimeToast by remember{mutableStateOf(true)}
    // Track permission state locally to trigger UI updates
    var isPermissionGranted by remember {
        mutableStateOf(
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                Environment.isExternalStorageManager() else true
        )
    }

    // Track Current folder location
    val rootDir = Environment.getExternalStorageDirectory()

    var currentDir by remember { mutableStateOf(rootDir) }
    var files by remember { mutableStateOf<List<File>>(emptyList()) }

    val fileAccessLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {_->
        // 2. Re-check the status when the user returns from the Settings screen
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            isPermissionGranted = Environment.isExternalStorageManager()

        // Grants Permission Access
        else
            Toast.makeText(context, "❌ Permission Denied", Toast.LENGTH_SHORT).show()

        }

    // Reload File when Current Directory Changes
    LaunchedEffect(isPermissionGranted, currentDir ) {
        if(isPermissionGranted){
            // Permission Granted

            withContext(Dispatchers.IO){
                files = currentDir.listFiles()?.toList()?.sortedWith(
                    compareBy({ !it.isDirectory }, { it.name.lowercase() })
                )?.filterNotNull() ?: emptyList()

            }
        }
    }

    // Handle system back button to go up folder
    BackHandler(enabled = currentDir != rootDir) {
        currentDir = currentDir.parentFile ?: rootDir
    }

    if (!isPermissionGranted) {
        PermissionButton(context, fileAccessLauncher)
    }
else {
            Toast.makeText(context, " ✅ Root Access Granted", Toast.LENGTH_SHORT).show()
            Box(Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.TopStart) {
                //List files
                LazyColumn(
                    Modifier
                        .fillMaxWidth()
                        .offset(y=100.dp).padding(10.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start
                ) {
                    items(files) { file ->
                        FileItem(file, onClick= {
                            if (file.isDirectory) { // Navigate deeper
                                currentDir = file
//                            Toast.makeText(context, "${file.name}", Toast.LENGTH_SHORT).show()
                            }

                        })
                    }
                }

                //Current Directory Display
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .offset(y = 30.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Black, Color.Black.copy(alpha = 0.8f))
                            )
                        )
                        .align(Alignment.TopCenter)
                ) {
                    Column(Modifier.padding(start = 10.dp)) {
                        Text("${currentDir.name}", style=MaterialTheme.typography.titleMedium)
                        Text("${currentDir.absolutePath}", style= MaterialTheme.typography.labelSmall)
                        // "Go Up" button (only if not at root)
                        if (currentDir != rootDir) {
                            TextButton(onClick = { currentDir = currentDir.parentFile ?: rootDir }) {
                                Text("⬅️ .. (Back)")
                            }
                        }



                    }
                }
            }
        }

}



@Composable
fun PermissionButton(context: Context, launcher: ManagedActivityResultLauncher<Intent, ActivityResult>){
    Box(Modifier.fillMaxSize().offset(y=50.dp), contentAlignment = Alignment.TopCenter) {

    Button(onClick = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                    data = "package:${context.packageName}".toUri() // Goto ONLY this App Permission
                }

                launcher.launch(intent)

            }
        }
    }) { Text("Grant Root Access") }
    }
}
@Composable
fun FileItem(file: File, onClick: ()->Unit){

    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        if (file.isDirectory) Text("📁 - ${file.name}", style = MaterialTheme.typography.bodyLarge, maxLines = 1,
            overflow = TextOverflow.Ellipsis)
        else Text("📝 - ${file.name}", style = MaterialTheme.typography.bodyLarge, maxLines = 1,
            overflow = TextOverflow.Ellipsis)

        Spacer(Modifier.weight(1f))

        Icon(
            imageVector = Icons.AutoMirrored.Outlined.ArrowForward, null,
            modifier = Modifier.padding(end = 10.dp)
        )


    }
}

fun listExternalDirectoryFiles(): List<File> {
    // List Files and Directories
    val root = Environment.getExternalStorageDirectory()
    val files = root.listFiles()
//    files?.forEach { f ->
//        if (f.isDirectory) Log.d("TAG", "Directory:  ${f.name}")
//        else Log.d("TAG", "    File:  ${f.name}")
//    }

    return root?.listFiles()?.toList() ?: emptyList()



}