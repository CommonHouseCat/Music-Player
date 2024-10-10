package com.example.nlcn

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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

class PlayPreLoadedSound:ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val soundFileName = intent.getStringExtra("soundFileName") ?: return
        val displayName = intent.getStringExtra("displayName") ?: "Unknown Track"

        setContent {
            PlayPreLoadedSoundScreen(context = this,  soundFileName = soundFileName, displayName = displayName)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayPreLoadedSoundScreen(context: Context, soundFileName: String, displayName: String) {
    var mediaPlayer: MediaPlayer? = remember { null }
    val mainActivity = LocalContext.current as MainActivity // Get MainActivity instance

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
        else -> R.drawable.music_disc // Provide a default image if displayName doesn't match
    }

    DisposableEffect(key1 = soundFileName) {
        mediaPlayer = MediaPlayer().apply {
            val assetFileDescriptor = context.assets.openFd(soundFileName)
            setDataSource(assetFileDescriptor.fileDescriptor, assetFileDescriptor.startOffset, assetFileDescriptor.length)
            prepare()
            start()
        }

        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
            mainActivity.showBottomBar = true
        }
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
                .padding(0.dp)
                .background(Color.Black),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text ( // Display the "Now Playing" text
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
                        contentDescription = "Image",
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

            Spacer(modifier = Modifier.height(36.dp))

            // -----------------------------------------------------------------------------------------------------------------
            // Play button
            Button(onClick = { mediaPlayer?.start() }) {
                Text("Play")
            }

            Spacer(modifier = Modifier.height(8.dp))
            // Pause button

            Button(onClick = { mediaPlayer?.stop() }) {
                Text("Pause")
            }
        }
    }
}
