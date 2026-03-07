package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.search.model.Track

interface SearchHistoryInteractor {

    suspend fun getSearchHistory(consumer: SearchHistoryConsumer)

    suspend fun clearSearchHistory(consumer: SearchHistoryConsumer)

    suspend fun addTrackToHistory(track: Track, consumer: SearchHistoryConsumer)

    interface SearchHistoryConsumer {
        fun consume(searchHistory: List<Track>)
    }

}