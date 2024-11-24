package com.example.nlcn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nlcn.ui.theme.NLCNTheme
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween

class MainActivity : ComponentActivity() {
    // Lazy initialization of the  state for showing the bottom bar, only created when accessed the first time
    private val showBottomBarState = lazy { mutableStateOf(true) }
    // Get and set  value for showBottomBarState
    var showBottomBar: Boolean
        get() = showBottomBarState.value.value
        set(value) { showBottomBarState.value.value = value }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NLCNTheme { // Custom theme
                Surface( // Provides a background surface for  UI.
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    MyBottomNavBar()
                }
            }
        }
    }
}

@Composable
fun MyBottomNavBar() {
    val navController = rememberNavController() // Creates a NavController for navigation between screens.
    val selected = remember { mutableStateOf(Icons.Default.Home) } // Stores the currently selected icon.
    val mainActivity = LocalContext.current as MainActivity // Get MainActivity instance

    Scaffold( // Main UI structure
        bottomBar = {
            if(mainActivity.showBottomBar) {// Conditionally displays the bottom bar.
                Box( // A box to store bottom bar and give it background
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .background(MaterialTheme.colorScheme.secondary)
                ) {
                    BottomAppBar( // The actual bottom bar.
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .align(Alignment.BottomCenter)
                            .height(56.dp),
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        // Icon button for Home Screen
                        IconButton(
                            onClick = {
                                selected.value = Icons.Default.Home // Updates the selected icon.
                                navController.navigate(Screens.Home.screen) { popUpTo(Screens.Home.screen) } // Navigate back to Home Screen on back-pressed (<)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Home screen",
                                modifier = Modifier.size(26.dp),
                                tint = if (selected.value == Icons.Default.Home) Color.White else Color.Black
                            )
                        }

                        // Icon button for LocalFile Screen
                        IconButton(
                            onClick = {
                                selected.value = Icons.Default.Folder
                                navController.navigate(Screens.LocalFile.screen) { popUpTo(Screens.Home.screen) }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = "Local File screen",
                                modifier = Modifier.size(26.dp),
                                tint = if (selected.value == Icons.Default.Folder) Color.White else Color.Black
                            )
                        }

                        // Icon button for Settings Screen
                        IconButton(
                            onClick = {
                                selected.value = Icons.Default.Settings
                                navController.navigate(Screens.Settings.screen) { popUpTo(Screens.Home.screen) }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings screen",
                                modifier = Modifier.size(26.dp),
                                tint = if (selected.value == Icons.Default.Settings) Color.White else Color.Black
                            )
                        }
                    }
                }
            }

        }
    ) { paddingValues ->

        NavHost( // Manages navigation between screens.
            navController = navController, // The navigation controller
            startDestination = Screens.Home.screen, // The default screen is set to Home Screen
            modifier = Modifier.padding(paddingValues)
        ) {
            // Defines the routes and composable for Home Screen.
            composable(
                Screens.Home.screen,
                // Enter transition
                enterTransition = {
                    when (initialState.destination.route) {  // Conditionally app listener transitions based on the route of the previous screen.
                        Screens.LocalFile.screen, Screens.Settings.screen ->
                            slideIntoContainer( // Use slideIntoContainer for a sliding animation.
                                towards = AnimatedContentTransitionScope.SlideDirection.Right, // Slide in from the left.
                                animationSpec = tween(720) // Use a tween animation with a duration of 720ms.
                            )
                        else -> null
                    }
                },
                // Exit transition
                exitTransition = {
                    when (targetState.destination.route) {
                        Screens.LocalFile.screen, Screens.Settings.screen ->
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(720)
                            )
                        else -> null
                    }
                }
            ) {
                Home(navController, LocalContext.current) // The Home Screen is displayed.
            }

            // Defines the routes and composable for LocalFile Screen.
            composable(
                Screens.LocalFile.screen,
                enterTransition = {
                    when (initialState.destination.route) {
                        Screens.Home.screen ->
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(720)
                            )
                        Screens.Settings.screen ->
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(720)
                            )
                        else -> null
                    }
                },
                exitTransition = {
                    when (targetState.destination.route) {
                        Screens.Home.screen ->
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(720)
                            )
                        Screens.Settings.screen ->
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(720)
                            )
                        else -> null
                    }
                }
            ) {
                LocalFile()
            }

            // Defines the routes and composable for Home Screen.
            composable(
                Screens.Settings.screen,
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(720)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(720)
                    )
                }
            ) {
                Settings()
            }


            // A route to PlayPreLoadedSound for Home Screen
            composable("play_preloaded_sound/{soundFileName}/{displayName}")  { backStackEntry ->
                // Get the sound file name and display name from the back stack entry arguments
                val soundFileName = backStackEntry.arguments?.getString("soundFileName") ?: return@composable
                val displayName = backStackEntry.arguments?.getString("displayName") ?: return@composable
                // Navigate to the PlayPreLoadedSoundScreen with the provided arguments
                PlayPreLoadedSoundScreen(context = LocalContext.current, soundFileName = soundFileName, displayName = displayName)
            }
        }
    }
}
