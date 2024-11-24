@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.nlcn

import android.content.Context
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    navController: NavController, // NavController for navigation between screens
    context: Context
){
    // Accessing the data store to retrieve the current language preference.
    val dataStore = remember { PreferenceDataStore(context) }
    val currentLanguage = dataStore.getLanguage.collectAsState(initial = "en")

    // Creating an updated context with the current language applied.
    val updatedContext = remember(currentLanguage.value) {
        val locale = Locale(currentLanguage.value)
        Locale.setDefault(locale)

        val configuration = context.resources.configuration
        configuration.setLocale(locale)

        context.createConfigurationContext(configuration)
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)) {
        TopAppBar(
            title = {
                Row {
                    Icon(imageVector = Icons.Default.Home,
                        contentDescription = "Home Icon",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        with(updatedContext) { getString(R.string.home) },
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.headlineMedium)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        // Lazy Column to display the categories and sounds
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.secondary)
                .padding(8.dp)
        ) {
            // item function used to separate title and rows of sounds
            item { CategoryTitle(title = with(updatedContext) { getString(R.string.rainTitle) }) }
            item {
                CategoryRow(
                    sounds = listOf(SoundItem(with(updatedContext) { getString(R.string.rainOnWindow) }, R.drawable.rain_window),
                        SoundItem(with(updatedContext) { getString(R.string.thunderstorm) }, R.drawable.thunderstorm),
                        SoundItem(with(updatedContext) { getString(R.string.rainInForest) }, R.drawable.rain_in_forest)
                    ),
                    navController = navController
                )
            }

            item { CategoryTitle(title = with(updatedContext) { getString(R.string.fireplaceTitle) }) }
            item {
                CategoryRow(
                    sounds = listOf(
                        SoundItem(with(updatedContext) { getString(R.string.classicFireplace) }, R.drawable.classic_fireplace),
                        SoundItem(with(updatedContext) { getString(R.string.fireplaceThunderstorm) }, R.drawable.fireplace_thunderstorm)
                    ),
                    navController = navController
                )
            }

            item { CategoryTitle(title = with(updatedContext) { getString(R.string.natureTitle) }) }
            item {
                CategoryRow(
                    sounds = listOf(
                        SoundItem(with(updatedContext) { getString(R.string.campingAtNight) }, R.drawable.camp_place_night),
                        SoundItem(with(updatedContext) { getString(R.string.creek) }, R.drawable.creek),
                        SoundItem(with(updatedContext) { getString(R.string.beachShore) }, R.drawable.beach_shore),
                        SoundItem(with(updatedContext) { getString(R.string.forest) }, R.drawable.forest)
                    ),
                    navController = navController
                )
            }

            item { CategoryTitle(title = with(updatedContext) { getString(R.string.backgroundNoiseTitle) }) }
            item {
                CategoryRow(
                    sounds = listOf(
                        SoundItem(with(updatedContext) { getString(R.string.silentCarRide) }, R.drawable.car_ride),
                        SoundItem(with(updatedContext) { getString(R.string.peopleTalkingInTheOtherRoom) }, R.drawable.iaminyourwall)
                    ),
                    navController = navController
                )
            }
        }
    }
}

// Composable function to display a category title.
@Composable
fun CategoryTitle(title: String) {
    Text(
        text = title,
        fontSize = 24.sp,
        color =  MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier.padding(8.dp)
    )
}

// Composable function to for a LazyRow to display a list of sounds in a horizontal scroll
@Composable
fun CategoryRow(sounds: List<SoundItem>, navController: NavController) {
    LazyRow {
        items(sounds.size) { index ->
            val sound = sounds[index]
            SoundCard(sound, navController)
        }
    }
}

// Composable function to display a sound card with image and name.
@Composable
fun SoundCard(sound: SoundItem, navController: NavController) {
    // Mapping for different sound names to sound files
    val soundFileName = when (sound.name) {
        "Rain on Window" -> "rain_on_window.mp3"
        "Thunderstorm" -> "thunderstorm.mp3"
        "Rain in a Forest" -> "rain_in_forest.mp3"
        "Classic Fireplace" -> "classic_fireplace.mp3"
        "Fireplace during a storm" -> "fireplace_thunderstorm.mp3"
        "Camping at night" -> "night_at_camp.mp3"
        "Creek" -> "creek.mp3"
        "Beach Shore" -> "beach_shore.mp3"
        "Forest" -> "forest.mp3"
        "A Silent Car Ride" -> "silent_car_ride.mp3"
        "People talking in the other room" -> "people_taking_in_the_other_room.mp3"


        "Mưa rơi trên cửa sổ" -> "rain_on_window.mp3"
        "Giông bão" -> "thunderstorm.mp3"
        "Mưa trong rừng" -> "rain_in_forest.mp3"
        "Lò sưởi cổ điển" -> "classic_fireplace.mp3"
        "Lò sưởi trong cơn bão" -> "fireplace_thunderstorm.mp3"
        "Cắm trại đêm khuya" -> "night_at_camp.mp3"
        "Suối" -> "creek.mp3"
        "Bờ biển" -> "beach_shore.mp3"
        "Rừng" -> "forest.mp3"
        "Chuyến xe yên tĩnh" -> "silent_car_ride.mp3"
        "Tiếng trò chuyện phòng bên" -> "people_taking_in_the_other_room.mp3"
        else -> ""
    }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .size(150.dp)
            .clickable {
                if (soundFileName.isNotEmpty()) {
                    // Navigating to the sound playback screen when the card is clicked.
                    navController.navigate("play_preloaded_sound/$soundFileName/${sound.name}")
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

// Data class representing a sound item with its name and image resource.
data class SoundItem(val name: String, val imageRes: Int)

