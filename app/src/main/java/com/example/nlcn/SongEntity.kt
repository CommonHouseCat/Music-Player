package com.example.nlcn

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

// ForeignKey is used to establish a relationship between
// PlaylistEntity and SongEntity tables
@Entity(
    tableName = "songs",
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["id"],
            childColumns = ["playlistId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
// Song table Entity
data class SongEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val playlistId: Int,
    val displayName: String,
    val contentUri: String,
)