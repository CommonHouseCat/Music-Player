package com.example.nlcn

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun Home(navController: NavController){
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        item { CategoryTitle(title = "Rain") }
        item {
            CategoryRow(
                sounds = listOf(
                    SoundItem("Refreshing Rain", R.drawable.test),
                    SoundItem("Rain on Window", R.drawable.test),
                    SoundItem("Rain in Forest", R.drawable.test),
                    // Add more sounds as needed
                ),
                navController = navController
            )
        }

        item { CategoryTitle(title = "Wind") }
        item {
            CategoryRow(
                sounds = listOf(
                    SoundItem("Gentle Breeze", R.drawable.test),
                    SoundItem("Strong Wind", R.drawable.test),
                    SoundItem("Wind through Trees", R.drawable.test),
                    // Add more sounds as needed
                ),
                navController = navController
            )
        }
    }
}

@Composable
fun CategoryTitle(title: String) {
    Text(
        text = title,
        fontSize = 24.sp,
        color = Color.Black,
        modifier = Modifier.padding(8.dp)
    )
}

@Composable
fun CategoryRow(sounds: List<SoundItem>, navController: NavController) {
    LazyRow {
        items(sounds.size) { index ->
            val sound = sounds[index]
            SoundCard(sound, navController)
        }
    }
}

@Composable
fun SoundCard(sound: SoundItem, navController: NavController) {
    val soundFileName = when (sound.name) {
        "Refreshing Rain" -> "rain.mp3"
        "Rain on Window" -> "rain.mp3"
        "Rain in Forest" -> "rain.mp3"
        "Gentle Breeze" -> "rain.mp3"
        "Strong Wind" -> "rain.mp3"
        "Wind through Trees" -> "rain.mp3"
        else -> ""
    }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .size(150.dp)
            .clickable {
                if (soundFileName.isNotEmpty()) {
                    navController.navigate("detail_screen/$soundFileName")
                }
            },
        shape = RoundedCornerShape(8.dp)
    ) {
        Box {
            Image(
                painter = painterResource(id = sound.imageRes),
                contentDescription = sound.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Text(
                text = sound.name,
                fontSize = 16.sp,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            )
        }
    }
}

data class SoundItem(val name: String, val imageRes: Int)
