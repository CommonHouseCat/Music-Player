package com.example.nlcn

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.nlcn.ui.theme.NLCNTheme
import kotlinx.coroutines.launch


class PlaylistActivity : ComponentActivity() {

    private lateinit var pickAudioFileLauncher: ActivityResultLauncher<String>
    private var fileName by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val playlistId = intent.getIntExtra("PLAYLIST_ID", -1)
        val playlistTitle = intent.getStringExtra("PLAYLIST_TITLE") ?: "Playlist"

        pickAudioFileLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val audioFile = getAudioFileName(this, uri)
                fileName = audioFile
            }
        }

        setContent {
            NLCNTheme {
                PlaylistScreen(
                    context = this,
                    playlistId, playlistTitle,
                    pickAudioFileLauncher = pickAudioFileLauncher,
                    fileName = fileName,
                    onFileNameChange = { newFileName -> fileName = newFileName }
                )
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistScreen(
    context: Context,
    playlistId: Int,
    playlistTitle: String,
    pickAudioFileLauncher: ActivityResultLauncher<String>,
    fileName: String,
    onFileNameChange: (String) -> Unit
) {
    val database = remember { AppDatabase.getDatabase(context) }
    val songDao = remember { database.songDao() }
    val coroutineScope = rememberCoroutineScope()

    var showAddDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var songToDelete by remember { mutableStateOf<SongEntity?>(null) }
    var displayName by remember { mutableStateOf("") }
    var songs by remember { mutableStateOf<List<SongEntity>>(emptyList()) }

    // Load songs when the composable is first created
    LaunchedEffect(playlistId) {
        songs = songDao.getSongsForPlaylist(playlistId)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        Column {
            TopAppBar(
                title = { Text(playlistTitle, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { (context as? ComponentActivity)?.onBackPressedDispatcher?.onBackPressed() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black),
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Song",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            )

            LazyColumn {
                items(songs) { song ->
                    SongItem(
                        song = song,
                        onDeleteClick = {
                            songToDelete = song
                            showDeleteDialog = true
                        },
                        onItemClick = {
                            // Does nothing for now
                        }
                    )
                }
            }
        }

        // Dialog box for adding a song
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = {
                    showAddDialog = false
                    displayName = ""
                    onFileNameChange("")
                },
                title = { Text("Add audio from Device", color = Color.White) },
                text = {
                    Column {
                        TextField(
                            value = displayName,
                            onValueChange = { displayName = it },
                            label = { Text("Display name", color = Color.LightGray) },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            )
                        )

                        IconButton(onClick = { pickAudioFileLauncher.launch("audio/*") }, modifier = Modifier.padding(top = 4.dp)) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = "Add from local device",
                                tint = Color.LightGray,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        if (fileName.isNotEmpty()) {
                            Text(text = "Selected file: \n$fileName", color = Color.White)
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        coroutineScope.launch {
                            val newSong = SongEntity(
                                playlistId = playlistId,
                                displayName = displayName,
                                filePath = "" // You might want to store the actual file path here
                            )
                            songDao.insertSong(newSong)
                            songs = songDao.getSongsForPlaylist(playlistId)
                            showAddDialog = false
                            displayName = ""
                            onFileNameChange("")
                        }
                    }) {
                        Text("Confirm", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showAddDialog = false
                        displayName = ""
                        onFileNameChange("")
                    }) {
                        Text("Cancel", color = Color.White)
                    }
                },
                containerColor = Color.DarkGray
            )
        }

        // Dialog box for deleting a song
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                    songToDelete = null
                },
                title = { Text("Delete Song", color = Color.White) },
                text = { Text("Do you want to delete ${songToDelete?.displayName}?", color = Color.White) },
                confirmButton = {
                    TextButton(onClick = {
                        songToDelete?.let { song ->
                            coroutineScope.launch {
                                songDao.deleteSong(song)
                                songs = songDao.getSongsForPlaylist(playlistId)
                                showDeleteDialog = false
                                songToDelete = null
                            }
                        }
                    }) {
                        Text("Delete", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                        songToDelete = null
                    }) {
                        Text("Cancel", color = Color.White)
                    }
                },
                containerColor = Color.DarkGray
            )
        }
    }
}
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun PlaylistScreen(
//    context: Context,
//    playlistId: Int,
//    playlistTitle: String,
//    pickAudioFileLauncher: ActivityResultLauncher<String>,
//    fileName: String,
//    onFileNameChange: (String) -> Unit
//) {
//    val database = remember { AppDatabase.getDatabase(context) }
//    val songDao = remember { database.songDao() }
//    val coroutineScope = rememberCoroutineScope()
//
//    var showAddDialog by remember { mutableStateOf(false)}
//    var showDeleteDialog by remember { mutableStateOf(false) }
//    var songToDelete by remember { mutableStateOf<SongEntity?>(null) }
//    var displayName by remember { mutableStateOf("") }
//    var songs by remember { mutableStateOf(listOf<SongEntity>()) }
//
//
////    // Load playlists when the composable is first created
////    LaunchedEffect(Unit) {
////        coroutineScope.launch {
////            songs = songDao.getSongsForPlaylist(playlistId)
////        }
////    }
//
//    // Load songs when the composable is first created
//    LaunchedEffect(playlistId) {
//        songs = songDao.getSongsForPlaylist(playlistId)
//    }
//
//    Surface(
//        modifier = Modifier.fillMaxSize(),
//        color = Color.Black
//    ) {
//        Column {
//            TopAppBar (
//                title = { Text(playlistTitle , color = Color.White) },
//                navigationIcon = {
//                    IconButton(onClick = { (context as? ComponentActivity)?.onBackPressedDispatcher?.onBackPressed() }) {
//                        Icon(
//                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                            contentDescription = "Back",
//                            tint = Color.White
//                        )
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors( containerColor = Color.Black ),
//                actions = {
//                    IconButton(onClick = { showAddDialog = true }) {
//                        Icon(
//                            imageVector = Icons.Default.Add,
//                            contentDescription = "Add Song",
//                            tint = Color.White,
//                            modifier = Modifier.size(32.dp)
//                        )
//                    }
//                }
//            )
//
//            LazyColumn {
//                items(songs) { song ->
//                    SongItem(
//                        song = song,
//                        onDeleteClick = {
//                            songToDelete = song
//                            showDeleteDialog = true
//                        },
//                        onItemClick = {
//                            // Does nothing for now
//                        }
//                    )
//                }
//            }
//        }
//
//        // Dialog box for adding a song
//        if (showAddDialog) {
//            AlertDialog(
//                onDismissRequest = {
//                    showAddDialog = false
//                    displayName = ""
//                    onFileNameChange("")
//                },
//                title = { Text("Add audio from Device", color = Color.White) },
//                text = {
//                    Column {
//                        TextField(
//                            value = displayName,
//                            onValueChange = { displayName = it },
//                            label = { Text("Display name", color = Color.LightGray) },
//                            singleLine = true,
//                            colors = TextFieldDefaults.colors(
//                                focusedTextColor = Color.White,
//                                unfocusedTextColor = Color.White,
//                                focusedContainerColor = Color.Transparent,
//                                unfocusedContainerColor = Color.Transparent
//                            )
//                        )
//
//                        IconButton(
//                            onClick = { pickAudioFileLauncher.launch("audio/*") },
//                            modifier = Modifier.padding(top = 4.dp)
//                        ){
//                            Icon(
//                                imageVector = Icons.Default.Folder,
//                                contentDescription = "Add from local device",
//                                tint = Color.LightGray,
//                                modifier = Modifier.size(32.dp)
//                            )
//                        }
//
//                        if (fileName.isNotEmpty()) {
//                            Text(text = "Selected file: \n$fileName", color = Color.White)
//                        }
//                    }
//                },
//                confirmButton = {
//                    TextButton(onClick = {
//                        coroutineScope.launch {
//                            val newSong = SongEntity(
//                                playlistId = playlistId,
//                                displayName = displayName,
//                                filePath = "" // You might want to store the actual file path here
//                            )
//                            songDao.insertSong(newSong)
//                            songs = songDao.getSongsForPlaylist(playlistId)
//                            showAddDialog = false
//                            displayName = ""
//                            onFileNameChange("")
//                        }
//                    }) {
//                        Text("Confirm", color = Color.White)
//                    }
//                },
//                dismissButton = {
//                    TextButton(onClick = {
//                        showAddDialog = false
//                        displayName = ""
//                        onFileNameChange("")
//                    }) {
//                        Text("Cancel", color = Color.White)
//                    }
//                },
//                containerColor = Color.DarkGray
//            )
//        }
//
//        if (showDeleteDialog) {
//            AlertDialog(
//                onDismissRequest = {
//                    showDeleteDialog = false
//                    songToDelete = null
//                },
//                title = { Text("Delete Song", color = Color.White) },
//                text = { Text("Do you want to delete ${songToDelete?.displayName}?", color = Color.White) },
//                confirmButton = {
//                    TextButton(onClick = {
//                        songToDelete?.let { song ->
//                            coroutineScope.launch {
//                                songDao.deleteSong(song)
//                                songs = songDao.getSongsForPlaylist(playlistId)
//                                showDeleteDialog = false
//                                songToDelete = null
//                            }
//                        }
//                    }) {
//                        Text("Delete", color = Color.Red)
//                    }
//                },
//                dismissButton = {
//                    TextButton(onClick = {
//                        showDeleteDialog = false
//                        songToDelete = null
//                    }) {
//                        Text("Cancel", color = Color.White)
//                    }
//                },
//                containerColor = Color.DarkGray
//            )
//        }
//    }
//}
// Helper function to get the file name from the Uri
private fun getAudioFileName(context: Context, uri: Uri): String {
    var name = "Unknown"
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1) {
                name = it.getString(nameIndex)
            }
        }
    }
    return name
}

@Composable
fun SongItem(song: SongEntity, onDeleteClick: () -> Unit, onItemClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding( start = 16.dp, end = 16.dp, top = 6.dp, bottom = 6.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.DarkGray)
            .clickable (onClick = onItemClick),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = song.displayName,
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            modifier = Modifier.weight(1f).padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
        )

        IconButton(onClick = onDeleteClick) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color.Red,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

