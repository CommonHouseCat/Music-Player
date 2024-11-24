package com.example.nlcn

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Defines the Room database, specifying entities and version.
@Database(entities = [PlaylistEntity::class, SongEntity::class], version = 4)
abstract class AppDatabase : RoomDatabase() {
    // Provides access to the PlaylistDao and SongDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun songDao(): SongDao

    companion object {
        @Volatile // Marks INSTANCE as volatile to ensure visibility across threads.
        private var INSTANCE: AppDatabase? = null

        // Provides a singleton instance of the database.
        fun getDatabase(context: Context): AppDatabase {
            // If INSTANCE is not null, return it.
            return INSTANCE ?: synchronized(this) {
                // If INSTANCE is null, create a new instance.
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    // Uses destructive migration if a schema mismatch occurs.
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance // Assigns the new instance to INSTANCE.
                instance // Returns the new instance.
            }
        }
    }
}