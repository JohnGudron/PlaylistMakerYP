package com.example.playlistmaker.data.local

import com.example.playlistmaker.domain.models.Track

interface LocalHistoryStorage {

    fun getHistory(): List<Track>

    fun clearHistory()

    fun addTrackToHistory(track: Track)

}