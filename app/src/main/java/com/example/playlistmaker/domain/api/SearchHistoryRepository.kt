package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface SearchHistoryRepository {

    fun getSearchHistory(): List<Track>

    fun clearSearchHistory()

    fun addTrackToHistory(track: Track)
}