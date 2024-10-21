package com.example.nlcn

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val createTime: Long = System.currentTimeMillis()
)