package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.search.model.Track

interface SearchHistoryRepository {

    suspend fun getSearchHistory(): List<Track>

    fun clearSearchHistory()

    fun addTrackToHistory(track: Track)
}