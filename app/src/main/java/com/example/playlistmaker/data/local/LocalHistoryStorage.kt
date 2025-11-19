package com.example.playlistmaker.data.local

import com.example.playlistmaker.data.dto.TrackDto

interface LocalHistoryStorage {

    fun getHistory(): List<TrackDto>

    fun clearHistory()

    fun addTrackToHistory(track: TrackDto)

}