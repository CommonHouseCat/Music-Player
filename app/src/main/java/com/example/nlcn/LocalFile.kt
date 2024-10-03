package com.example.nlcn

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalFile(navController: NavController){
    var playlists by remember { mutableStateOf(listOf<String>()) }
    var showAddDialog by remember { mutableStateOf(false) }
    val sortType by remember { mutableStateOf(SortType.NAME) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Playlists") },
            navigationIcon = {
                IconButton(onClick = { showSortMenu(sortType) }) {
                    Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort")
                }
            },
            actions = {
                IconButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Playlist")
                }
            }
        )

        // Display the playlists using LazyColumn
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            items(playlists.size) { index ->
                PlaylistItem(playlists[index])
            }
        }

        // Dialog for adding a new playlist
        if (showAddDialog) {
            AddPlaylistDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { playlistName ->
                    playlists = playlists + playlistName
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun PlaylistItem(playlistName: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)

    ) {
        Text(
            text = playlistName,
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Composable
fun AddPlaylistDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var playlistName by remember { mutableStateOf(TextFieldValue("")) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Create New Playlist") },
        text = {
            TextField(
                value = playlistName,
                onValueChange = { playlistName = it },
                label = { Text("Playlist Name") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(playlistName.text) }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}

enum class SortType {
    NAME, LAST_MODIFIED, DATE
}

// Placeholder function to handle sorting logic
fun showSortMenu(sortType: SortType) {
    // Implement sorting logic here
}