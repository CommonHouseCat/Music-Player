# Sleep Assistance Sound Player
<p align="center">
  <a href="https://android-arsenal.com/api?level=33"><img alt="API" src="https://img.shields.io/badge/API-33%2B-brightgreen"/></a>
  <a href="https://github.com/CommonHouseCat"><img alt="Profile" src="https://commonhousecat.github.io/badges/profile-badge.svg"/></a>
</p>

A simple sound player to help users fall asleep created with Jetpack Compose and MediaPlayer

## Preview

| ![home](previews/Home-dark.png) | ![player](previews/playsong-dark.png) | ![home](previews/Home-light.png) | ![player](previews/playsong-light.png) |
|---------------------------------|:-------------------------------------:|:--------------------------------:|:--------------------------------------:|

## Tech stack & Libraries

- [Kotlin](https://kotlinlang.org/) - Modern native Android programming language.
- [Jetpack](https://developer.android.com/jetpack)
  - [Compose](https://developer.android.com/jetpack/compose) - Modern native UI toolkit.
  - [Navigation](https://developer.android.com/jetpack/compose/navigation) - Handle in-app navigation.
  - [DataStore](https://developer.android.com/topic/libraries/architecture/datastore)- For storing theme and language preferences.
- [MediaPlayer](https://developer.android.com/guide/topics/media/mediaplayer) - Android's built-in media playback framework.
- [Runtime Permissions](https://developer.android.com/training/permissions/requesting) - Handle runtime permissions for accessing device resources.
- [Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) - For asynchronous operations and concurrency.
- [Room Database](https://developer.android.com/training/data-storage/room) - Persistence library providing an abstraction layer over SQLite.

## Features

* **Navigation:**
  * Bottom navigation bar for easy access to different sections of the app.
  * Navigation between screens for preloaded sounds,local files, playlists, settings, and about.
* **Sound Playback:**
  * Preloaded audio files.
  * Local audio files from the user's device, organized into playlists.
  * Background playback support.
* **PlaylistManagement:**
  * Create and manage playlists to organize local audio files.
  * Add audio files to playlists from the user's device storage.
  * Shuffle playback mode for playlists.
* **Audio Controls:**
  * Play, pause, skip, and repeat audio files.
  * Set a timer to stop playback after a specified duration.
* **Settings:**
  * Change the app's theme (light or dark).
  * Change the app's language (English or Vietnamese.
  * Access the about page for app information.
* **About:**
  * View app information, such as version, developer, and credits.
