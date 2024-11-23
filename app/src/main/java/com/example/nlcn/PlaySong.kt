@file:Suppress("ObjectLiteralToLambda")

package com.example.nlcn

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.AlarmOn
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.nlcn.ui.theme.NLCNTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale


class PlaySong:ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val playlistId = intent.getIntExtra("playlistId", -1)
        val currentSongIndex = intent.getIntExtra("songIndex", -1)
        val soundFileUri = intent.getStringExtra("soundFileName") ?: return
        val displayName = intent.getStringExtra("displayName") ?: "Unknown Track"

        // Take persistent permissions if possible
        try {
            val uri = Uri.parse(soundFileUri)
            val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
            contentResolver.takePersistableUriPermission(uri, takeFlags)
        } catch (e: SecurityException) {
            Log.d("PlaySong", "Couldn't take persistent permission: ${e.message}")
        }

        setContent {
            NLCNTheme {
                PlaylistSongScreen(
                    context = this,
                    playlistId = playlistId,
                    initialSongIndex = currentSongIndex,
                    initialSoundFileUri = soundFileUri,
                    initialDisplayName = displayName
                )
            }
        }
    }
}

@SuppressLint("AutoboxingStateCreation")
@Composable
fun PlaylistSongScreen(
    context: Context,
    playlistId: Int,
    initialSongIndex: Int,
    initialSoundFileUri: String,
    initialDisplayName: String
) {
    val database = remember { AppDatabase.getDatabase(context) }
    val songDao = remember { database.songDao() }

    // State to track playlist songs and current index
    var playlistSongs by remember { mutableStateOf<List<SongEntity>>(emptyList()) }
    var currentSongIndex by remember { mutableStateOf(initialSongIndex) }

    // Get shuffled sequence if available
    val isShuffled = (context as? ComponentActivity)?.intent?.getBooleanExtra("isShuffled", false) ?: false
    val shuffledIndices = (context as? ComponentActivity)?.intent?.getIntegerArrayListExtra("shuffledIndices")

    // Fetch playlist songs
    LaunchedEffect(playlistId) {
        playlistSongs = songDao.getSongsForPlaylist(playlistId)
    }

    // Function to play next song
    val playNextSong: () -> Unit = {
        if (isShuffled && shuffledIndices != null) {
            val currentPosition = shuffledIndices.indexOf(currentSongIndex)
            if (currentPosition < shuffledIndices.size - 1) {
                val nextIndex = shuffledIndices[currentPosition + 1]
                val nextSong = playlistSongs[nextIndex]

                // Start a new PlaySong activity with the next shuffled song
                val intent = Intent(context, PlaySong::class.java).apply {
                    putExtra("playlistId", playlistId)
                    putExtra("songIndex", nextIndex)
                    putExtra("soundFileName", nextSong.contentUri)
                    putExtra("displayName", nextSong.displayName)
                    putExtra("isShuffled", true)
                    putIntegerArrayListExtra("shuffledIndices", shuffledIndices)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(intent)
                (context as? ComponentActivity)?.finish()
            } else {
                Toast.makeText(context, "End of Shuffled Playlist", Toast.LENGTH_SHORT).show()
                (context as? ComponentActivity)?.finish()
            }
        } else {
            // Original sequential playback logic
            if (currentSongIndex < playlistSongs.size - 1) {
                val nextIndex = currentSongIndex + 1
                val nextSong = playlistSongs[nextIndex]

                val intent = Intent(context, PlaySong::class.java).apply {
                    putExtra("playlistId", playlistId)
                    putExtra("songIndex", nextIndex)
                    putExtra("soundFileName", nextSong.contentUri)
                    putExtra("displayName", nextSong.displayName)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(intent)
                (context as? ComponentActivity)?.finish()
            } else {
                Toast.makeText(context, "End of Playlist", Toast.LENGTH_SHORT).show()
                (context as? ComponentActivity)?.finish()
            }
        }
    }

    // Function to play previous song
    val playPreviousSong: () -> Unit = {
        if (isShuffled && shuffledIndices != null) {
            val currentPosition = shuffledIndices.indexOf(currentSongIndex)
            if (currentPosition > 0) {
                val previousIndex = shuffledIndices[currentPosition - 1]
                val previousSong = playlistSongs[previousIndex]

                val intent = Intent(context, PlaySong::class.java).apply {
                    putExtra("playlistId", playlistId)
                    putExtra("songIndex", previousIndex)
                    putExtra("soundFileName", previousSong.contentUri)
                    putExtra("displayName", previousSong.displayName)
                    putExtra("isShuffled", true)
                    putIntegerArrayListExtra("shuffledIndices", shuffledIndices)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(intent)
                (context as? ComponentActivity)?.finish()
            }
        } else {
            // Original previous song logic
            if (currentSongIndex > 0) {
                val previousIndex = currentSongIndex - 1
                val previousSong = playlistSongs[previousIndex]

                val intent = Intent(context, PlaySong::class.java).apply {
                    putExtra("playlistId", playlistId)
                    putExtra("songIndex", previousIndex)
                    putExtra("soundFileName", previousSong.contentUri)
                    putExtra("displayName", previousSong.displayName)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(intent)
                (context as? ComponentActivity)?.finish()
            }
        }
    }

//    // Function to play next song
//    val playNextSong: () -> Unit = {
//        if (currentSongIndex < playlistSongs.size - 1) {
//            val nextIndex = currentSongIndex + 1
//            val nextSong = playlistSongs[nextIndex]
//
//            // Update current index
//            currentSongIndex = nextIndex
//
//            // Start a new PlaySong activity with the next song
//            val intent = Intent(context, PlaySong::class.java).apply {
//                putExtra("playlistId", playlistId)
//                putExtra("songIndex", nextIndex)
//                putExtra("soundFileName", nextSong.contentUri)
//                putExtra("displayName", nextSong.displayName)
//                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            }
//            context.startActivity(intent)
//
//            // Finish the current activity
//            (context as? ComponentActivity)?.finish()
//        } else {
//            // Stop playback and finish the activity when the last song is reached
//            Toast.makeText(context, "End of Playlist", Toast.LENGTH_SHORT).show()
//            (context as? ComponentActivity)?.finish()
//        }
//    }
//
//    val playPreviousSong: () -> Unit = {
//        if (playlistSongs.isNotEmpty()) {
//            val previousIndex = if (currentSongIndex > 0) currentSongIndex - 1 else playlistSongs.size - 1
//            val previousSong = playlistSongs[previousIndex]
//
//            // Update current index
//            currentSongIndex = previousIndex
//
//            // Start a new PlaySong activity with the previous song
//            val intent = Intent(context, PlaySong::class.java).apply {
//                putExtra("playlistId", playlistId)
//                putExtra("songIndex", previousIndex)
//                putExtra("soundFileName", previousSong.contentUri)
//                putExtra("displayName", previousSong.displayName)
//                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            }
//            context.startActivity(intent)
//
//            // Finish the current activity
//            (context as? ComponentActivity)?.finish()
//        }
//    }

    // Modify PlaySongScreen to include playlist navigation
    PlaySongScreen(
        context = context,
        soundFileUri = initialSoundFileUri,
        displayName = initialDisplayName,
        onSongCompleted = { playNextSong() },
        onNextClick = { playNextSong() },
        onPreviousClick = { playPreviousSong() },
        enableControls = playlistSongs.size > 1
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaySongScreen(
    context: Context,
    soundFileUri: String,
    displayName: String,
    onSongCompleted: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    enableControls: Boolean
) {
    val mediaPlayer = remember { MediaPlayer() }
    val coroutineScope = rememberCoroutineScope()
    val dataStore = remember { PreferenceDataStore(context) }
    val currentLanguage = dataStore.getLanguage.collectAsState(initial = "en")

    var isPlaying by remember { mutableStateOf(true) }
    var currentPosition by remember { mutableFloatStateOf(0f) }
    var duration by remember { mutableFloatStateOf(0f) }
    var isRepeatOn by remember { mutableStateOf(false) }
    var isTimerOn by remember { mutableStateOf(false) }
    var timerDuration by remember { mutableIntStateOf(0) }
    var showTimerPicker by remember { mutableStateOf(false) }
    var remainingTime by remember { mutableIntStateOf(0) }

    // Update configuration when language changes
    val updatedContext = remember(currentLanguage.value) {
        val locale = Locale(currentLanguage.value)
        Locale.setDefault(locale)

        val configuration = context.resources.configuration
        configuration.setLocale(locale)

        context.createConfigurationContext(configuration)
    }

    // MediaPlayer initialization and progress tracking
    DisposableEffect(key1 = soundFileUri) {
        try {
            val uri = Uri.parse(soundFileUri)
            context.contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
                mediaPlayer.setDataSource(pfd.fileDescriptor)
                mediaPlayer.prepareAsync()
            } ?: throw IllegalStateException("Could not open file descriptor")
        } catch (e: Exception) {
            Log.e("PlayPreLoadedSound", "Error setting up MediaPlayer", e)
            Toast.makeText(context, "Error playing audio file", Toast.LENGTH_SHORT).show()
            (context as? ComponentActivity)?.finish()
        }

        // When the MediaPlayer is prepared, set the duration
        mediaPlayer.setOnPreparedListener {
            duration = it.duration.toFloat() // Set the track duration in milliseconds
            if (isPlaying) {
                mediaPlayer.start()
            }
        }

        // Set up listener to detect when the track finishes
        mediaPlayer.setOnCompletionListener {
            if (isRepeatOn || isTimerOn) {
                mediaPlayer.seekTo(0)
                mediaPlayer.start() // Restart the track if repeat is on
            } else {
                isPlaying = false // Mark as not playing when finished
                onSongCompleted()
            }
        }

        // Set a listener to update the current position during playback
        val updatePositionJob = coroutineScope.launch {
            while (true) {
                currentPosition = mediaPlayer.currentPosition.toFloat()
                delay(1000)
            }
        }

        onDispose {
            updatePositionJob.cancel()
            mediaPlayer.release()
        }
    }

    LaunchedEffect(isTimerOn, timerDuration) {
        if (isTimerOn && timerDuration > 0) {
            remainingTime = timerDuration * 60
            while (remainingTime > 0 && isTimerOn) {
                delay(1000)
                remainingTime--
            }
            if (remainingTime <= 0) {
                isTimerOn = false
                mediaPlayer.pause()
                isPlaying = false
            }
        }
    }

    @Composable
    fun TimerPickerDialog(
        onTimeSelected: (Int) -> Unit,
        onDismissRequest: () -> Unit
    ) {
        var selectedTime by remember { mutableFloatStateOf(0f) }

        AlertDialog(
            onDismissRequest = { onDismissRequest() },
            title = { Text(with(updatedContext) { getString(R.string.setTimer) }, color = MaterialTheme.colorScheme.onPrimary) },
            text = {
                Column {
                    // Display the selected time in minutes directly
                    Text(
                        text = with(updatedContext){
                            getString(R.string.Duration) + " ${selectedTime.toInt()}"},
                        color = MaterialTheme.colorScheme.onPrimary)
                    Slider(
                        value = selectedTime,
                        onValueChange = { selectedTime = it },
                        valueRange = 0f..480f,
                        steps = 479,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.secondaryContainer,
                            activeTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                            inactiveTrackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { onTimeSelected(selectedTime.toInt()) }) {
                    Text(with(updatedContext) { getString(R.string.confirm) },  color = MaterialTheme.colorScheme.onPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(with(updatedContext) { getString(R.string.cancel) },  color = MaterialTheme.colorScheme.onPrimary)
                }
            },
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { (context as? ComponentActivity)?.onBackPressedDispatcher?.onBackPressed() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.secondary),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            Text( // Display the "Now Playing" text
                text = with(updatedContext) { getString(R.string.nowPlaying) },
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Card( // Display the track image
                modifier = Modifier
                    .padding(8.dp)
                    .size(180.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Box {
                    Image(
                        painter = painterResource(id = R.drawable.music_disc),
                        contentDescription = "Track Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text( // Display the track name
                text = displayName,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimary
            )

            if (isTimerOn) {
                Text(
                    text = with(updatedContext) {getString(R.string.timer) + ": ${formatTimeForCounter2(remainingTime)}"},
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Audio progress bar
            Slider(
                value = if (duration > 0) currentPosition / duration else 0f,
                onValueChange = { newValue ->
                    if (duration > 0) {
                        mediaPlayer.seekTo((newValue * duration).toInt())
                        currentPosition = newValue * duration
                    }
                },
                modifier = Modifier.padding(horizontal = 36.dp),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.secondaryContainer,
                    activeTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                    inactiveTrackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                )
            )

            // Display current time and duration
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTime2(currentPosition.toInt()),
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(start = 30.dp)
                )
                Text(
                    text = formatTime2(duration.toInt()),
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(end = 30.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Row of repeat, play/pause and set timer buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(
                    onClick = { isRepeatOn = !isRepeatOn },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if(isRepeatOn) Icons.Filled.RepeatOne else Icons.Outlined.Repeat,
                        contentDescription = "Repeat",
                        tint = if(isRepeatOn) Color.Green else MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(32.dp),
                    )
                }

                // Pause/play button
                IconButton(
                    onClick = {
                        if(isPlaying) {
                            mediaPlayer.pause()
                        } else {
                            mediaPlayer.start()
                        }
                        isPlaying = !isPlaying
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if(mediaPlayer.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (mediaPlayer.isPlaying) "Pause" else "Play",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(32.dp),
                    )
                }

                // Row of next and previous button
                IconButton(
                    onClick = {
                        if (isTimerOn) {
                            isTimerOn = false
                        } else {
                            showTimerPicker = true
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if (isTimerOn) Icons.Filled.AlarmOn else Icons.Filled.AlarmOff,
                        contentDescription = "Timer",
                        tint = if (isTimerOn) Color.Green else MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(32.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Row of next and previous button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    space = when {
                        LocalConfiguration.current.screenWidthDp > 600 -> 16.dp
                        LocalConfiguration.current.screenWidthDp > 320 -> 88.dp
                        else -> 4.dp
                    },
                    alignment = Alignment.CenterHorizontally
                )
            ) {
                IconButton(
                    onClick = onPreviousClick,
                    enabled = enableControls
                ) {
                    Icon(
                        imageVector = Icons.Filled.SkipPrevious,
                        contentDescription = "Previous",
                        tint = if (enableControls) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                        modifier = Modifier.size(32.dp),
                    )
                }

                IconButton(
                    onClick = onNextClick,
                    enabled = enableControls
                ) {
                    Icon(
                        imageVector = Icons.Filled.SkipNext,
                        contentDescription = "Next",
                        tint = if (enableControls) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                        modifier = Modifier.size(32.dp),
                    )
                }
            }
        }
    }

    if (showTimerPicker) {
        TimerPickerDialog(
            onTimeSelected = { minutes ->
                timerDuration = minutes
                isTimerOn = true
                showTimerPicker = false
                if (!isPlaying) {
                    mediaPlayer.start()
                    isPlaying = true
                }
            },
            onDismissRequest = { showTimerPicker = false }
        )
    }
}
// Helper function to format time in mm:ss
@SuppressLint("DefaultLocale")
fun formatTime2(timeMs: Int): String {
    val minutes = (timeMs / 1000) / 60
    val seconds = (timeMs / 1000) % 60
    return String.format("%02d:%02d", minutes, seconds)
}

@SuppressLint("DefaultLocale")
fun formatTimeForCounter2(timeMs: Int): String {
    val minutes = (timeMs) / 60
    val seconds = (timeMs) % 60
    return String.format("%02d:%02d", minutes, seconds)
}