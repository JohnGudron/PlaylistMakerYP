package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface SearchHistoryInteractor {

    fun getSearchHistory(consumer: SearchHistoryConsumer)

    fun clearSearchHistory()

    fun addTrackToHistory(track: Track)

    interface SearchHistoryConsumer {
        fun consume(searchHistory: List<Track>)
    }

}