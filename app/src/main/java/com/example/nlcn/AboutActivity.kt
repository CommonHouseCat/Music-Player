package com.example.nlcn

import android.os.Bundle
import androidx.compose.material3.Surface
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import android.content.Context
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


import com.example.nlcn.ui.theme.NLCNTheme

class AboutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NLCNTheme {
                AboutScreen(this)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(context: Context) {
   Surface (
       modifier = Modifier.fillMaxSize(),
       color = Color.Black
   ) {
       Column {
           TopAppBar(
               title = {
                   Row(verticalAlignment = Alignment.CenterVertically) {
                       Text(text = "About", color = Color.White)
                       Spacer(modifier = Modifier.width(8.dp))
                       Icon(
                           imageVector = Icons.Outlined.Info,
                           contentDescription = "Info",
                           tint = Color.White
                       )
                   }
               },
               navigationIcon = {
                   IconButton(onClick = { (context as? ComponentActivity)?.onBackPressedDispatcher?.onBackPressed() }) {
                       Icon(
                           imageVector =  Icons.AutoMirrored.Filled.ArrowBack,
                           contentDescription = "Back",
                           tint = Color.White
                       )
                   }
               },
               colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
           )

           Column(
               modifier = Modifier
                   .fillMaxSize()
                   .padding(16.dp)
           ) {
               // General app description
               Text("About This App", color = Color.White, style = MaterialTheme.typography.headlineSmall)
               Text(
                   "This app is a university course project, designed to help your fall asleep easier and with the help of a variety of soothing white noise sounds to drown out all of one's inner thoughts. You can also personalize the sleeping experience by adding you own audio files from the local device.",
                   color = Color.White,
                   style = MaterialTheme.typography.bodyMedium,
                   textAlign = TextAlign.Justify
               )

               Spacer(modifier = Modifier.height(12.dp))

               // Project Scope
               Text("Project Scope", color = Color.White, style = MaterialTheme.typography.headlineSmall)
               Text(
                   "This is an open-source, non-commercial project developed for educational purposes. Do with it what you will.",
                   color = Color.White,
                   style = MaterialTheme.typography.bodyMedium,
                   textAlign = TextAlign.Justify
               )
               
               Spacer(modifier = Modifier.height(12.dp))

               // Technology Used
               Text("Technology", color = Color.White, style = MaterialTheme.typography.headlineSmall)
               Text(
                   "Built with Jetpack Compose, Kotlin, Room database, and MediaPlayer.",
                   color = Color.White,
                   style = MaterialTheme.typography.bodyMedium,
                   textAlign = TextAlign.Justify
               )

               Spacer(modifier = Modifier.height(12.dp))

               // Technology Used
               Text("Disclaimer", color = Color.White, style = MaterialTheme.typography.headlineSmall)
               Text(
                   "This app is provided as-is, without warranty. It is not intended for medical use and should not replace professional advice.",
                   color = Color.White,
                   style = MaterialTheme.typography.bodyMedium,
                   textAlign = TextAlign.Justify
               )

               Spacer(modifier = Modifier.height(12.dp))

               // Credit
               Text("Credits", color = Color.White, style = MaterialTheme.typography.headlineSmall)
               Text(
                   "Developed by: Nguyễn Quang Vinh\nStudent ID: B2125727\nAs part of: Project - Specialized Topics Course (CT501H) - CanTho University",
                   color = Color.White,
                   style = MaterialTheme.typography.bodyMedium,
                   textAlign = TextAlign.Justify
               )
           }
       }
   }
}