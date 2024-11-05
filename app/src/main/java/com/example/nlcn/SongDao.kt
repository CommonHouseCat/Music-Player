package com.example.nlcn

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    // Retrieve all songs for a specific playlist
    @Query("SELECT * FROM songs WHERE playlistId = :playlistId")
    suspend fun getSongsForPlaylist(playlistId: Int): List<SongEntity> // not suspend before

    // Insert a new song into the database
    @Insert
    suspend fun insertSong(song: SongEntity)

    // Delete a song from the database by SongEntity object
    @Delete
    suspend fun deleteSong(song: SongEntity)

    // Delete all songs from a specific playlist
    @Query("DELETE FROM songs WHERE playlistId = :playlistId")
    suspend fun deleteAllSongsFromPlaylist(playlistId: Int)

    // Edit song title by Id
    @Query("UPDATE songs SET displayName = :newDisplayName WHERE id = :songId")
    suspend fun updateSongTitle(songId: Int, newDisplayName: String)
}