package com.example.nlcn

/**
 * Defines a sealed class to represent different screens or Activities within the application.
 *
 * Sealed classes restrict class hierarchies, ensuring that all subclasses are known at compile time.
 * This allows for exhaustive `when` statements when handling navigation or screen logic.
 */
sealed class Screens(val screen: String) {
    data object Home: Screens("Home") // Represents the Home screen
    data object LocalFile: Screens("LocalFile") // Represents LocalFile screen
    data object Settings: Screens("Settings") // Represents Settings screen
}