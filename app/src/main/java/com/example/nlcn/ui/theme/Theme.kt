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
    secondaryContainer = Color(0xFFFF4500),
    tertiaryContainer = Color(0xFF8031A7)
)

private val LightColorScheme = lightColorScheme(
    primary = Color.White,
    secondary = Color.White,
    tertiary = Color.Black,
    background = Color.Black,
    surface = Color.White,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.LightGray,
    onBackground = Color.Black,
    onSurface = Color.White,
    secondaryContainer = Color.Red,
    tertiaryContainer = Color(0xFFFFA500)
)

@Composable
fun NLCNTheme(
    dataStore: PreferenceDataStore = PreferenceDataStore(LocalContext.current),
    content: @Composable () -> Unit
) {
    val currentTheme = dataStore.getTheme.collectAsState(initial = "dark")
    val context = LocalContext.current

    val colorScheme = when (currentTheme.value) {
        "light" -> LightColorScheme
        "dark" -> DarkColorScheme
        else -> DarkColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                currentTheme.value == "light"
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}