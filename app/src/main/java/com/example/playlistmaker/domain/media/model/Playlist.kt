package com.example.playlistmaker.domain.media.model

data class Playlist(val id: Long = 0,
                    val name: String,
                    val description: String,
                    val posterUri: String,
                    val tracksIds: List<Long>,
                    val playlistSize: Int = 0)
