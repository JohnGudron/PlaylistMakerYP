package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface SearchHistoryInteractor {

    fun getSearchHistory(consumer: SearchHistoryConsumer)

    fun clearSearchHistory(consumer: SearchHistoryConsumer)

    fun addTrackToHistory(track: Track, consumer: SearchHistoryConsumer)

    interface SearchHistoryConsumer {
        fun consume(searchHistory: List<Track>)
    }

}