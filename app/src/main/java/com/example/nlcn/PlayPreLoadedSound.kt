@file:Suppress("ObjectLiteralToLambda")

package com.example.nlcn

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.nlcn.ui.theme.NLCNTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class PlayPreLoadedSound:ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val soundFileName = intent.getStringExtra("soundFileName") ?: return
        val displayName = intent.getStringExtra("displayName") ?: "Unknown Track"

        setContent {
            NLCNTheme {
                PlayPreLoadedSoundScreen(context = this,  soundFileName = soundFileName, displayName = displayName)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayPreLoadedSoundScreen(context: Context, soundFileName: String, displayName: String) {
    val mediaPlayer = remember { MediaPlayer() }
    var isPlaying by remember { mutableStateOf(true) }
    var currentPosition by remember { mutableFloatStateOf(0f) }
    var duration by remember { mutableFloatStateOf(0f) }
    var isRepeatOn by remember { mutableStateOf(false) }
    val mainActivity = LocalContext.current as MainActivity

    var isTimerOn by remember { mutableStateOf(false) }
    var timerDuration by remember { mutableIntStateOf(0) }
    var showTimerPicker by remember { mutableStateOf(false) }
    var remainingTime by remember { mutableIntStateOf(0) }

    val coroutineScope = rememberCoroutineScope()

    // Hide the bottom bar when the screen is displayed
    LaunchedEffect(Unit) {
        mainActivity.showBottomBar = false
    }

    // Map the displayName to the corresponding image resource
    val imageResId = when (displayName) {
        "Rain on Window" -> R.drawable.rain_window
        "Thunderstorm" -> R.drawable.thunderstorm
        "Rain in a Forest" -> R.drawable.rain_in_forest
        "Classic Fireplace" -> R.drawable.classic_fireplace
        "Fireplace during a storm" -> R.drawable.fireplace_thunderstorm
        "Camping at night" -> R.drawable.camp_place_night
        "Creek" -> R.drawable.creek
        "Beach shore" -> R.drawable.beach_shore
        "Forest" -> R.drawable.forest
        "A Silent Car Ride" -> R.drawable.car_ride
        "People talking in the other room" -> R.drawable.iaminyourwall
        else -> R.drawable.music_disc
    }

    // MediaPlayer initialization and progress tracking
    DisposableEffect(key1 = soundFileName) {
        val assetFileDescriptor = context.assets.openFd(soundFileName)
        mediaPlayer.setDataSource(assetFileDescriptor.fileDescriptor, assetFileDescriptor.startOffset, assetFileDescriptor.length)
        mediaPlayer.prepareAsync()

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
            mainActivity.showBottomBar = true
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
            title = { Text("Set Timer (in minutes)") },
            text = {
                Column {
                    // Display the selected time in minutes directly
                    Text(text = "Duration: ${selectedTime.toInt()} minutes")
                    Slider(
                        value = selectedTime,
                        onValueChange = { selectedTime = it },
                        valueRange = 0f..480f,  // Max 480 minutes (8 hours)
                        steps = 479  // 479 steps for a total of 480 options
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { onTimeSelected(selectedTime.toInt()) }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text("Cancel")
                }
            }
        )
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { (context as? ComponentActivity)?.onBackPressedDispatcher?.onBackPressed() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            Text( // Display the "Now Playing" text
                text = "Now Playing",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
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
                        painter = painterResource(id = imageResId),
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
                color = Color.White
            )

            if (isTimerOn) {
                Text(
                    text = "Timer: ${formatTimeForCounter(remainingTime)}",
                    color = Color.White,
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
                    thumbColor = Color.White,
                    activeTrackColor = Color.Blue,
                    inactiveTrackColor = Color.LightGray
                )
            )

            // Display current time and duration
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTime(currentPosition.toInt()),
                    color = Color.White,
                    modifier = Modifier.padding(start = 30.dp)
                )
                Text(
                    text = formatTime(duration.toInt()),
                    color = Color.White,
                    modifier = Modifier.padding(end = 30.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

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
                        tint = if(isRepeatOn) Color.Green else Color.White,
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
                        tint = Color.White,
                        modifier = Modifier.size(32.dp),
                    )
                }

                // Set time button
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
                        tint = if (isTimerOn) Color.Green else Color.White,
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
fun formatTime(timeMs: Int): String {
    val minutes = (timeMs / 1000) / 60
    val seconds = (timeMs / 1000) % 60
    return String.format("%02d:%02d", minutes, seconds)
}

@SuppressLint("DefaultLocale")
fun formatTimeForCounter(timeMs: Int): String {
    val minutes = (timeMs) / 60
    val seconds = (timeMs) % 60
    return String.format("%02d:%02d", minutes, seconds)
}