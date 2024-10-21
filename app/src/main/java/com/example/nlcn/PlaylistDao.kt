package com.example.nlcn

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PlaylistDao {
    // Retrieves all Playlist from database
    @Query("SELECT * FROM playlists")
    suspend fun getAllPlaylists(): List<PlaylistEntity>

    // Insert a Playlist into database
    @Insert
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    //  Delete a Playlist from database by PlaylistEntity object
    @Delete
    suspend fun deletePlaylist(playlist: PlaylistEntity)

    // Delete a Playlist by its ID
    @Query("DELETE FROM playlists WHERE id = :playlistId")
    suspend fun deletePlaylistById(playlistId: Int)
}