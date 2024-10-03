package com.example.nlcn

// Defines a sealed class to represent different screens / Activity
sealed class Screens(val screen: String) {
    data object Home: Screens("Home")
    data object LocalFile: Screens("LocalFile")
    data object Settings: Screens("Settings")
}