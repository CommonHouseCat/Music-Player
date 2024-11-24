package com.example.nlcn.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.nlcn.PreferenceDataStore

private val DarkColorScheme = darkColorScheme(
    primary = Color.Black,
    secondary = Color.Black,
    tertiary = Color.LightGray,
    background = Color.Black,
    surface = Color.Black,
    onPrimary = Color.White,
    onSecondary = Color.Gray,
    onTertiary = Color.DarkGray,
    onBackground = Color.White,
    onSurface = Color.Black,
    primaryContainer = Color.DarkGray,
    secondaryContainer = Color(0xFFFF4500),
    tertiaryContainer = Color(0xFF8031A7)
)

private val LightColorScheme = lightColorScheme(
    primary = Color.White,
    secondary = Color.White,
    tertiary = Color.Black,
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.LightGray,
    onBackground = Color.Black,
    onSurface = Color.White,
    primaryContainer = Color.LightGray,
    secondaryContainer = Color.Red,
    tertiaryContainer = Color(0xFFFFA500)
)

@Composable
fun NLCNTheme(
    // dDtaStore instance to access preferences, defaults to one using LocalContext
    dataStore: PreferenceDataStore = PreferenceDataStore(LocalContext.current),
    content: @Composable () -> Unit
) {
    // Set currentTheme default to dark
    val currentTheme = dataStore.getTheme.collectAsState(initial = "dark")
    // Retrieves the current context using LocalContext
    val context = LocalContext.current

    // Determines the color scheme based on the current theme preference
    val colorScheme = when (currentTheme.value) {
        "light" -> LightColorScheme
        "dark" -> DarkColorScheme
        else -> DarkColorScheme
    }


    val view = LocalView.current // Retrieves the current view using LocalView
    // Applies side effects only when not in preview mode
    if (!view.isInEditMode) {
        SideEffect {
            val window = (context as Activity).window  // Gets the window from the current activity
            // Sets the status bar color & appearance based on color scheme
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = currentTheme.value == "light"
        }
    }

    // Applies the MaterialTheme with the determined color scheme and typography
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}