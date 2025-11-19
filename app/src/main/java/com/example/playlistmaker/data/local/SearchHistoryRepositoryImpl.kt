package com.example.playlistmaker.data.local

import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track

class SearchHistoryRepositoryImpl (private val localStorage: LocalHistoryStorage): SearchHistoryRepository {
    override fun getSearchHistory(): List<Track> {
        return localStorage.getHistory()
    }

    override fun clearSearchHistory() {
        localStorage.clearHistory()
    }

    override fun addTrackToHistory(track: Track) {
        localStorage.addTrackToHistory(track)
    }
}