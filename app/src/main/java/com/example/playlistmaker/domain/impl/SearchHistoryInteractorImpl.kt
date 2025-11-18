package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track

class SearchHistoryInteractorImpl (private val searchHistoryRepository: SearchHistoryRepository): SearchHistoryInteractor {

    override fun getSearchHistory(consumer: SearchHistoryInteractor.SearchHistoryConsumer) {
        consumer.consume(searchHistoryRepository.getSearchHistory())
    }

    override fun clearSearchHistory() {
        searchHistoryRepository.clearSearchHistory()
    }

    override fun addTrackToHistory(track: Track) {
        searchHistoryRepository.addTrackToHistory(track)
    }
}