package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.search.model.Track

interface SearchHistoryInteractor {

    fun getSearchHistory(consumer: SearchHistoryConsumer)

    fun clearSearchHistory(consumer: SearchHistoryConsumer)

    fun addTrackToHistory(track: Track, consumer: SearchHistoryConsumer)

    interface SearchHistoryConsumer {
        fun consume(searchHistory: List<Track>)
    }

}