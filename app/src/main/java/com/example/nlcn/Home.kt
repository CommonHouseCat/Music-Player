@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.nlcn

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun Home(navController: NavController){
    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)) {
        TopAppBar(
            title = {
                Row {
                    Icon(imageVector = Icons.Default.Home,
                        contentDescription = "Home",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Home", color = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Black
            )
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(8.dp)
        ) {
            item { CategoryTitle(title = "Rain") }
            item {
                CategoryRow(
                    sounds = listOf(
                        SoundItem("Rain in a Forest", R.drawable.rain_in_forest),
                        SoundItem("Rain on Window", R.drawable.rain_window),
                        SoundItem("Thunderstorm", R.drawable.thunderstorm)
                    ),
                    navController = navController
                )
            }

            item { CategoryTitle(title = "Fireplace") }
            item {
                CategoryRow(
                    sounds = listOf(
                        SoundItem("Classic Fireplace", R.drawable.classic_fireplace),
                        SoundItem("Fireplace during a storm", R.drawable.fireplace_thunderstorm)
                    ),
                    navController = navController
                )
            }

            item { CategoryTitle(title = "Nature") }
            item {
                CategoryRow(
                    sounds = listOf(
                        SoundItem("Camping at night", R.drawable.camp_place_night),
                        SoundItem("Creek", R.drawable.creek),
                        SoundItem("Beach shore", R.drawable.beach_shore),
                        SoundItem("Forest", R.drawable.forest)
                    ),
                    navController = navController
                )
            }

            item { CategoryTitle(title = "Background Noise") }
            item {
                CategoryRow(
                    sounds = listOf(
                        SoundItem("Busy Intersection", R.drawable.busy_intersection),
                        SoundItem("A Silent Car Ride", R.drawable.car_ride),
                        SoundItem("People talking in the other room", R.drawable.iaminyourwall)
                    ),
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun CategoryTitle(title: String) {
    Text(
        text = title,
        fontSize = 24.sp,
        color = Color.White,
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
        "Rain in a Forest" -> "rain_in_forest.mp3"
        "Rain on Window" -> "rain_on_window.mp3"
        "Thunderstorm" -> "thunderstorm.mp3"

        "Classic Fireplace" -> "classic_fireplace.mp3"

        "Fireplace during a storm" -> "thunderstorm.mp3"

        "Camping at night" -> "thunderstorm.mp3"
        "Creek" -> "thunderstorm.mp3"
        "Beach Shore" -> "thunderstorm.mp3"
        "Forest" -> "thunderstorm.mp3"

        "Busy Intersection" -> "thunderstorm.mp3"
        "A Silent Car Ride" -> "thunderstorm.mp3"
        "People talking in the other room" -> "thunderstorm.mp3"

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

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    Home(navController = rememberNavController())
}
