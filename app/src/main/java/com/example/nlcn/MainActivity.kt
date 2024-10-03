package com.example.nlcn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nlcn.ui.theme.Grey
import com.example.nlcn.ui.theme.NLCNTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NLCNTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), // Occupies the entire available space
                    color = Color.White // Sets the background color to white
                ) {
                    MyBottomNavBar()
                }
            }
        }
    }
}

@Composable
fun MyBottomNavBar(){
    val navigationController = rememberNavController() // Creates a navigation controller
    val selected = remember { mutableStateOf(Icons.Default.Home) } // State to track selected icon

    // Get system bar insets
    val systemBars = WindowInsets.systemBars

    val navigationBarHeight = if (systemBars.getBottom(LocalDensity.current) > 0) {
        1.dp
    } else {
        systemBars.getBottom(LocalDensity.current).dp
    }

    Scaffold( // Provides basic Material Design layout structure
        bottomBar = {
            BottomAppBar(
                containerColor = Grey
            ) {
                // Home Icon button
                IconButton(onClick = {
                    selected.value = Icons.Default.Home // Update selected icon state
                    navigationController.navigate(Screens.Home.screen){  // Navigate to Home screen
                        popUpTo(Screens.Home.screen) // Got the Home screen when pressed back button
                    }
                },
                modifier = Modifier.weight(1f)){ // Each button takes equal width
                    // Icon appearance changes based on selection
                    Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(26.dp),
                        tint = if(selected.value == Icons.Default.Home) Color.White else Color.DarkGray)
                }


                // Local File Icon button
                IconButton(onClick = {
                    selected.value = Icons.Default.Folder
                    navigationController.navigate(Screens.LocalFile.screen){
                        popUpTo(Screens.Home.screen)
                    }
                },
                    modifier = Modifier.weight(1f)){
                    Icon(Icons.Default.Folder, contentDescription = null, modifier = Modifier.size(26.dp),
                        tint = if(selected.value == Icons.Default.Folder) Color.White else Color.DarkGray)
                }


                // Settings Icon button
                IconButton(onClick = {
                    selected.value = Icons.Default.Settings
                    navigationController.navigate(Screens.Settings.screen){
                        popUpTo(Screens.Home.screen)
                    }
                },
                    modifier = Modifier.weight(1f)){
                    Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(26.dp),
                        tint = if(selected.value == Icons.Default.Settings) Color.White else Color.DarkGray)
                }
            }
        }
//        modifier = Modifier.padding(WindowInsets.navigationBars.asPaddingValues())
    ) {paddingValues -> // Content within theScaffold, with padding from the bottom bar
        NavHost(navController = navigationController, // Sets up navigation
            startDestination = Screens.Home.screen, // Initial screen is Home
            modifier = Modifier.padding(paddingValues)){  // Applies padding
            composable(Screens.Home.screen){ Home(navigationController) } // Defines route for Home screen
            composable(Screens.LocalFile.screen){ LocalFile(navigationController) } // Defines route for Local File screen
            composable(Screens.Settings.screen){ Settings() } // Defines route for Settings screen

            composable("detail_screen/{soundFileName}") { backStackEntry ->
                val soundFileName = backStackEntry.arguments?.getString("soundFileName") ?: return@composable
                PlayPreLoadedSoundScreen(context = LocalContext.current, soundFileName = soundFileName)
            }

        }
    }
}

@Preview (showBackground = true)
@Composable
fun GreetingPreview() {
    NLCNTheme {
        MyBottomNavBar()
    }
}