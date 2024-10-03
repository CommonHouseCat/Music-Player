package com.example.nlcn

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class PlayPreLoadedSound:ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val soundFileName = intent.getStringExtra("soundFileName") ?: return

        setContent {
            PlayPreLoadedSoundScreen(context = this, soundFileName = soundFileName)
        }
    }
}

@Composable
fun PlayPreLoadedSoundScreen(context: Context, soundFileName: String) {
    var mediaPlayer: MediaPlayer? = remember { null }

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
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        Button(onClick = { mediaPlayer?.start() }) {
            Text("Play")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { mediaPlayer?.pause() }) {
            Text("Pause")
        }
    }
}