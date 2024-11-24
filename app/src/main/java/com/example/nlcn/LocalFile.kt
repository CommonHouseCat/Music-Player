@file:Suppress("LiftReturnOrAssignment", "RedundantIf", "KotlinConstantConditions")

package com.example.nlcn

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.runtime.collectAsState
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
import android.Manifest
import android.util.Log
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalFile() {
    var playlistTitle by remember { mutableStateOf("") }
    var playlists by remember { mutableStateOf(listOf<PlaylistEntity>()) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var playlistToDelete by remember { mutableStateOf<PlaylistEntity?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var playlistToEdit by remember { mutableStateOf<PlaylistEntity?>(null) }
    var editPlaylistTitle by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var permissionGranted by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val playlistDao = remember { database.playlistDao() }
    val coroutineScope = rememberCoroutineScope()
    val dataStore = remember { PreferenceDataStore(context) }
    val currentLanguage = dataStore.getLanguage.collectAsState(initial = "en")

    // Requesting the READ_MEDIA_AUDIO permission
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission() // Use the correct contract
    ) { isGranted ->
        if(isGranted) { // Check if the permission is granted
            Log.d("LocalFile", "Permission granted")
            permissionGranted = isGranted
        }else { // Display a toast if the permission is denied
            Log.d("LocalFile", "Permission denied")
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
            Toast.makeText(context, "Please grant permission\nin app settings", Toast.LENGTH_SHORT).show()
        }
    }

    // Update configuration when language changes
    val updatedContext = remember(currentLanguage.value) {
        val locale = Locale(currentLanguage.value)
        Locale.setDefault(locale)

        val configuration = context.resources.configuration
        configuration.setLocale(locale)

        context.createConfigurationContext(configuration)
    }

    // Load playlists when the composable is first created
    LaunchedEffect(Unit) {
        launcher.launch(Manifest.permission.READ_MEDIA_AUDIO)
        coroutineScope.launch {
            playlists = playlistDao.getAllPlaylists()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
    ) {
        TopAppBar(
            title = {
                Row {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = "Local File",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        with(updatedContext) { getString(R.string.localFile) },
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),

            // The add button is disable of the permission if not granted
            actions = {
                IconButton(
                    onClick = {
                        if (permissionGranted) {
                            showAddDialog = true
                        } else {
                            launcher.launch(Manifest.permission.READ_MEDIA_AUDIO)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Playlist",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        )

        // Display the list of playlists
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
                    onEditClick = {
                        coroutineScope.launch {
                            playlistToEdit = playlist
                            editPlaylistTitle = playlist.title
                            showEditDialog = true
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
            title = { Text(with(updatedContext) { getString(R.string.createNewPlaylist) }, color = MaterialTheme.colorScheme.onPrimary) },
            text = {
                Column {
                    TextField(
                        value = playlistTitle,
                        onValueChange = { playlistTitle = it },
                        label = { Text(with(updatedContext) { getString(R.string.playlistTitle) }, color = MaterialTheme.colorScheme.onPrimary) },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                            unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
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
                    Text(with(updatedContext) { getString(R.string.confirm) }, color = MaterialTheme.colorScheme.onPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddDialog = false
                    playlistTitle = ""
                }) {
                    Text(with(updatedContext) { getString(R.string.cancel) }, color = MaterialTheme.colorScheme.onPrimary)
                }
            },
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    }

    // Dialog box for deleting a playlist
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                playlistToDelete = null
            },
            title = { Text(with(updatedContext) { getString(R.string.deletePlaylist) }, color = MaterialTheme.colorScheme.onPrimary) },
            text = { Text(with(updatedContext) { getString(R.string.deletePlaylistConfirmation) }, color = MaterialTheme.colorScheme.onPrimary) },
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
                    Text(with(updatedContext) { getString(R.string.delete) }, color = MaterialTheme.colorScheme.secondaryContainer)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    playlistToDelete = null
                }) {
                    Text(with(updatedContext) { getString(R.string.cancel) }, color = MaterialTheme.colorScheme.onPrimary)
                }
            },
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    }

    // Dialog box for editing a playlist title
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = {
                showEditDialog = false
                playlistToEdit = null
                editPlaylistTitle = ""
            },
            title = { Text(with(updatedContext) { getString(R.string.editPlaylistTitle) }, color = MaterialTheme.colorScheme.onPrimary) },
            text = {
                Column {
                    TextField(
                        value = editPlaylistTitle,
                        onValueChange = { editPlaylistTitle = it },
                        label = { Text(with(updatedContext) { getString(R.string.playlistTitle) }, color = MaterialTheme.colorScheme.onPrimary) },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                            unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if(editPlaylistTitle.isNotBlank()) {
                        coroutineScope.launch {
                            playlistToEdit?.let { playlist ->
                                playlistDao.updatePlaylistTitle(playlist.id, editPlaylistTitle)
                                playlists = playlistDao.getAllPlaylists()
                                showEditDialog = false
                                playlistToEdit = null
                                editPlaylistTitle = ""
                            }
                        }
                    }
                }) {
                    Text(with(updatedContext) { getString(R.string.confirm) }, color = MaterialTheme.colorScheme.onPrimary)
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        showEditDialog = false
                        playlistToEdit = null
                        editPlaylistTitle = ""
                    }) {
                    Text(with(updatedContext) { getString(R.string.cancel) }, color = MaterialTheme.colorScheme.onPrimary)
                }
            },

            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    }
}


@Composable
fun PlaylistItem(
    playlist: PlaylistEntity,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit,
    onItemClick: () -> Unit
) {
    val context = LocalContext.current
    val dataStore = remember { PreferenceDataStore(context) }
    val currentLanguage = dataStore.getLanguage.collectAsState(initial = "en")

    // Update configuration when language changes
    val updatedContext = remember(currentLanguage.value) {
        val locale = Locale(currentLanguage.value)
        Locale.setDefault(locale)

        val configuration = context.resources.configuration
        configuration.setLocale(locale)

        context.createConfigurationContext(configuration)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 6.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.onTertiary)
            .clickable(onClick = onItemClick),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column (modifier = Modifier
            .weight(1f)
            .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)){
            Text(
                text = playlist.title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimary,
            )

            Text(
                text = with(updatedContext) {
                    getString(R.string.createdOn) + " " + SimpleDateFormat("dd/MMM/yyyy", Locale.getDefault()).format(Date(playlist.createTime))
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        IconButton(onClick = onEditClick) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Title",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(24.dp)
            )
        }

        IconButton(onClick = onDeleteClick) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

