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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.nlcn.ui.theme.NLCNTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale


class PlayPreLoadedSound:ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Get the sound file name and display name from the intent in Main Activity
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
    val dataStore = remember { PreferenceDataStore(context) }
    val currentLanguage = dataStore.getLanguage.collectAsState(initial = "en")
    val coroutineScope = rememberCoroutineScope()
    val mainActivity = LocalContext.current as MainActivity

    var isTimerOn by remember { mutableStateOf(false) }
    var timerDuration by remember { mutableIntStateOf(0) }
    var showTimerPicker by remember { mutableStateOf(false) }
    var remainingTime by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(true) }
    var currentPosition by remember { mutableFloatStateOf(0f) }
    var duration by remember { mutableFloatStateOf(0f) }
    var isRepeatOn by remember { mutableStateOf(false) }

    // Hide the bottom bar when the screen is displayed
    LaunchedEffect(Unit) {
        mainActivity.showBottomBar = false
    }

    // Update configuration when language changes
    val updatedContext = remember(currentLanguage.value) {
        val locale = Locale(currentLanguage.value)
        Locale.setDefault(locale)

        val configuration = context.resources.configuration
        configuration.setLocale(locale)

        context.createConfigurationContext(configuration)
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

        "Mưa rơi trên cửa sổ" -> R.drawable.rain_window
        "Giông bão" -> R.drawable.thunderstorm
        "Mưa trong rừng" -> R.drawable.rain_in_forest
        "Lò sưởi cổ điển" -> R.drawable.classic_fireplace
        "Lò sưởi trong cơn bão" -> R.drawable.fireplace_thunderstorm
        "Cắm trại đêm khuya" ->  R.drawable.camp_place_night
        "Suối" -> R.drawable.creek
        "Bờ biển" -> R.drawable.beach_shore
        "Rừng" -> R.drawable.forest
        "Chuyến xe yên tĩnh" -> R.drawable.car_ride
        "Tiếng trò chuyện phòng bên" -> R.drawable.iaminyourwall
        else -> R.drawable.music_disc
    }


    // MediaPlayer initialization and progress tracking
    DisposableEffect(key1 = soundFileName) {
        val assetFileDescriptor = context.assets.openFd(soundFileName) // Open the sound file from assets
        // Set the data source for the MediaPlayer
        mediaPlayer.setDataSource(assetFileDescriptor.fileDescriptor, assetFileDescriptor.startOffset, assetFileDescriptor.length)
        mediaPlayer.prepareAsync() // Prepare the MediaPlayer asynchronously

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
                mediaPlayer.start() // Restart the track if repeat is on or the timer is set
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

        // Cleaning up resources when the composable is disposed
        onDispose {
            updatePositionJob.cancel()// Cancel the coroutine job that updates the current position
            mediaPlayer.release() // Release the MediaPlayer resources
            mainActivity.showBottomBar = true // Show the bottom bar again
        }
    }

    // Handle the timer feature
    LaunchedEffect(isTimerOn, timerDuration) {
        if (isTimerOn && timerDuration > 0) { // If the timer is on and the duration > 0
            remainingTime = timerDuration * 60 // Calculate the remaining time in seconds
            while (remainingTime > 0 && isTimerOn) { // While the timer is on and duration > 0
                delay(1000)
                remainingTime--
            }
            if (remainingTime <= 0) { // If the timer has finished
                isTimerOn = false
                mediaPlayer.pause()
                isPlaying = false
            }
        }
    }

    // Composable function to display the timer picker dialog
    @Composable
    fun TimerPickerDialog(
        onTimeSelected: (Int) -> Unit, // Callback function invoked when a time is selected
        onDismissRequest: () -> Unit // Callback function invoked when the dialog is dismissed
    ) {
        var selectedTime by remember { mutableFloatStateOf(0f) }

        AlertDialog(
            onDismissRequest = { onDismissRequest() }, // Callback when dialog is dismissed
            title = { Text(with(updatedContext) { getString(R.string.setTimer) }, color = MaterialTheme.colorScheme.onPrimary)  },
            text = {
                Column {
                    Text( // Display the selected time in minutes directly
                        text = with(updatedContext){getString(R.string.Duration) + " ${selectedTime.toInt()}"},
                        color = MaterialTheme.colorScheme.onPrimary)
                    Slider(
                        value = selectedTime,
                        onValueChange = { selectedTime = it },
                        valueRange = 0f..480f, // Range of the slider (0 to 480 minutes)
                        steps = 479, // Number of steps on the slider
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
                    Text(with(updatedContext) { getString(R.string.cancel) }, color = MaterialTheme.colorScheme.onPrimary)
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back",  tint = MaterialTheme.colorScheme.onPrimary)
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
                with(updatedContext) { getString(R.string.nowPlaying) },
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
                color = MaterialTheme.colorScheme.onPrimary
            )

            // If a timer is set then display the timer countdown, otherwise do not
            if (isTimerOn) {
                Text(
                    text = with(updatedContext) {getString(R.string.timer) + ": ${formatTimeForCounter(remainingTime)}"},
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Audio progress bar
            Slider(
                value = if (duration > 0) currentPosition / duration else 0f,// Current progress of the audio track
                onValueChange = { newValue ->
                    if (duration > 0) { // Only seek if the duration is known
                        mediaPlayer.seekTo((newValue * duration).toInt()) // Seek to the new position
                        currentPosition = newValue * duration // Update the current position state
                    }
                },
                modifier = Modifier.padding(horizontal = 36.dp),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.secondaryContainer,
                    activeTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                    inactiveTrackColor = Color.DarkGray.copy(alpha = 0.5f)
                )
            )

            // Display current time and duration
            // Similar to 00:30 / 10:00 in youtube
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTime(currentPosition.toInt()),
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(start = 30.dp)
                )
                Text(
                    text = formatTime(duration.toInt()),
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(end = 30.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Row to display buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Repeat button
                IconButton(
                    onClick = { isRepeatOn = !isRepeatOn }, // Toggle repeat state
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
                        // Pause if playing, play if paused
                        if(isPlaying) { mediaPlayer.pause() }
                        else { mediaPlayer.start() }
                        isPlaying = !isPlaying // Toggle the playing state
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

                // Set time button
                IconButton(
                    onClick = {
                        if (isTimerOn) { isTimerOn = false }  // If timer is on, turn it off
                        else { showTimerPicker = true } // Otherwise, show the timer picker dialog
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
        }
    }

    // Show the timer picker dialog
    if (showTimerPicker) {
        TimerPickerDialog(
            onTimeSelected = { minutes ->
                timerDuration = minutes // Set timer duration
                isTimerOn = true // Start timer
                showTimerPicker = false // Hide dialog
                if (!isPlaying) { // If the audio is not playing
                    mediaPlayer.start() // Start playing audio
                    isPlaying = true // Update playing state
                }
            },
            onDismissRequest = { showTimerPicker = false } // Dismiss the timer picker dialog
        )
    }
}
// Helper function to format time in mm:ss
@SuppressLint("DefaultLocale")
fun formatTime(timeMs: Int): String { // take milliseconds for input
    val minutes = (timeMs / 1000) / 60
    val seconds = (timeMs / 1000) % 60
    return String.format("%02d:%02d", minutes, seconds)
}

// Helper function to format time for the counter in mm:ss format.
@SuppressLint("DefaultLocale")
fun formatTimeForCounter(timeMs: Int): String {
    val minutes = (timeMs) / 60
    val seconds = (timeMs) % 60
    return String.format("%02d:%02d", minutes, seconds)
}

