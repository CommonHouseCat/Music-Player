package com.example.nlcn

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalFile() {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val playlistDao = remember { database.playlistDao() }
    val coroutineScope = rememberCoroutineScope()

    var showAddDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var playlistToDelete by remember { mutableStateOf<PlaylistEntity?>(null) }
    var playlistTitle by remember { mutableStateOf("") }
    var playlists by remember { mutableStateOf(listOf<PlaylistEntity>()) }



    // Load playlists when the composable is first created
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            playlists = playlistDao.getAllPlaylists()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        TopAppBar(
            title = {
                Row {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = "Local File",
                        tint = Color.White,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "Local File",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black),
            actions = {
                IconButton(onClick = { showAddDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Playlist",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        )

        LazyColumn {
            items(playlists) { playlist ->
                PlaylistItem(
                    playlist = playlist,
                    onDeleteClick = {
                        coroutineScope.launch {
                            playlistToDelete = playlist
                            showDeleteDialog = true
                        }
                    },
                    onItemClick = {
                        val intent = Intent(context, PlaylistActivity::class.java).apply {
                            putExtra("PLAYLIST_ID", playlist.id)
                            putExtra("PLAYLIST_TITLE", playlist.title)
                        }
                        context.startActivity(intent)
                    }
                )
            }
        }
    }

    // Dialog box for adding a playlist
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
                playlistTitle = ""
            },
            title = { Text("Create new playlist", color = Color.White) },
            text = {
                Column {
                    TextField(
                        value = playlistTitle,
                        onValueChange = { playlistTitle = it },
                        label = { Text("Playlist Title", color = Color.LightGray) },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (playlistTitle.isNotBlank()) {
                        coroutineScope.launch {
                            val newPlaylist = PlaylistEntity(title = playlistTitle)
                            playlistDao.insertPlaylist(newPlaylist)
                            playlists = playlistDao.getAllPlaylists()
                            showAddDialog = false
                            playlistTitle = ""
                        }
                    }
                }) {
                    Text("Confirm", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddDialog = false
                    playlistTitle = ""
                }) {
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
                playlistToDelete = null
            },
            title = { Text("Delete Playlist", color = Color.White) },
            text = { Text("Do you want to delete ${playlistToDelete?.title}?", color = Color.White) },
            confirmButton = {
                TextButton(onClick = {
                    playlistToDelete?.let { playlist ->
                        coroutineScope.launch {
                            playlistDao.deletePlaylist(playlist)
                            playlists = playlistDao.getAllPlaylists()
                            showDeleteDialog = false
                            playlistToDelete = null
                        }
                    }
                }) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    playlistToDelete = null
                }) {
                    Text("Cancel", color = Color.White)
                }
            },
            containerColor = Color.DarkGray
        )
    }
}


@Composable
fun PlaylistItem(playlist: PlaylistEntity, onDeleteClick: () -> Unit, onItemClick: () -> Unit) {
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
        Column (modifier = Modifier.weight(1f).padding(start = 16.dp, top = 8.dp, bottom = 8.dp)){
            Text(
                text = playlist.title,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
            )

            Text(
                text = "Created on: ${SimpleDateFormat("dd/MMM/yyyy", Locale.getDefault()).format(Date(playlist.createTime))}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.LightGray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

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

