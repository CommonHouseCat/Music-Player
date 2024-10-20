package com.example.nlcn

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Shuffle
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
import java.io.IOException

class PlaylistActivity : ComponentActivity() {
    private lateinit var pickAudioFileLauncher: ActivityResultLauncher<String>
    private var fileName by mutableStateOf("")
    private var filePath by mutableStateOf<Uri?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val playlistId = intent.getIntExtra("PLAYLIST_ID", -1)
        val playlistTitle = intent.getStringExtra("PLAYLIST_TITLE") ?: "Playlist"

        pickAudioFileLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                try {
                    // Get the content resolver
                    val contentResolver = applicationContext.contentResolver

                    // Take persistable permission
                    val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION

                    // First try to verify we can read the file
                    contentResolver.openInputStream(uri)?.use { _ ->
                        // If we get here, we can read the file
                        // Now try to take persistent permission if possible
                        try {
                            contentResolver.takePersistableUriPermission(uri, takeFlags)
                        } catch (e: SecurityException) {
                            // If we can't take persistent permission, that's okay
                            // as long as we can read the file
                            Log.d("PlaylistActivity", "Couldn't take persistent permission, but file is readable")
                        }

                        val audioFile = getFileNameFromUri(this, uri)
                        fileName = audioFile
                        filePath = uri
                    } ?: throw IOException("Cannot open input stream for URI")

                } catch (e: SecurityException) {
                    Log.e("PlaylistActivity", "Security Exception: ${e.message}")
                    Toast.makeText(
                        this,
                        "Cannot access this file. Please choose another.",
                        Toast.LENGTH_LONG
                    ).show()
                } catch (e: Exception) {
                    Log.e("PlaylistActivity", "Error accessing file: ${e.message}")
                    Toast.makeText(
                        this,
                        "Error accessing file. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        setContent {
            NLCNTheme {
                PlaylistScreen(
                    context = this,
                    playlistId = playlistId,
                    playlistTitle = playlistTitle,
                    pickAudioFileLauncher = pickAudioFileLauncher,
                    fileName = fileName,
                    filePath = filePath,
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
    filePath: Uri?,
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
                    IconButton(onClick = { /* Handle shuffle */ }) {
                        Icon(
                            imageVector = Icons.Default.Shuffle,
                            contentDescription = "Shuffle",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
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
                            // Handle song click
                        }
                    )
                }
            }
        }

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

                        IconButton(
                            onClick = { pickAudioFileLauncher.launch("audio/*") },
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
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
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                filePath?.let { uri ->
                                    try {
                                        val newSong = SongEntity(
                                            playlistId = playlistId,
                                            displayName = displayName.ifEmpty { getFileNameFromUri(context, uri) },
                                            contentUri = uri.toString()
                                        )
                                        songDao.insertSong(newSong)
                                        songs = songDao.getSongsForPlaylist(playlistId)
                                        showAddDialog = false
                                        displayName = ""
                                        onFileNameChange("")
                                    } catch (e: Exception) {
                                        Log.e("PlaylistActivity", "Error adding song: ${e.message}", e)
                                        Toast.makeText(
                                            context,
                                            "Error adding song. Please try again.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                    ) {
                        Text("Confirm", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showAddDialog = false
                            displayName = ""
                            onFileNameChange("")
                        }
                    ) {
                        Text("Cancel", color = Color.White)
                    }
                },
                containerColor = Color.DarkGray
            )
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                    songToDelete = null
                },
                title = { Text("Delete Song", color = Color.White) },
                text = { Text("Do you want to delete ${songToDelete?.displayName}?", color = Color.White) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            songToDelete?.let { song ->
                                coroutineScope.launch {
                                    songDao.deleteSong(song)
                                    songs = songDao.getSongsForPlaylist(playlistId)
                                    showDeleteDialog = false
                                    songToDelete = null
                                }
                            }
                        }
                    ) {
                        Text("Delete", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            songToDelete = null
                        }
                    ) {
                        Text("Cancel", color = Color.White)
                    }
                },
                containerColor = Color.DarkGray
            )
        }
    }
}

@Composable
fun SongItem(song: SongEntity, onDeleteClick: () -> Unit, onItemClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 6.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.DarkGray)
            .clickable(onClick = onItemClick),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = song.contentUri,
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
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

fun getFileNameFromUri(context: Context, uri: Uri): String {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    result = it.getString(index)
                }
            }
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/')
        if (cut != -1) {
            result = result?.substring(cut!! + 1)
        }
    }
    return result ?: "Unknown"
}