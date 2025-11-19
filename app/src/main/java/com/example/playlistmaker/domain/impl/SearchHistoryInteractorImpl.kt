package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track

class SearchHistoryInteractorImpl (private val searchHistoryRepository: SearchHistoryRepository): SearchHistoryInteractor {

    override fun getSearchHistory(consumer: SearchHistoryInteractor.SearchHistoryConsumer) {
        consumer.consume(searchHistoryRepository.getSearchHistory())
    }

    override fun clearSearchHistory(consumer: SearchHistoryInteractor.SearchHistoryConsumer) {
        searchHistoryRepository.clearSearchHistory()
        consumer.consume(searchHistoryRepository.getSearchHistory())
    }

    override fun addTrackToHistory(track: Track, consumer: SearchHistoryInteractor.SearchHistoryConsumer) {
        searchHistoryRepository.addTrackToHistory(track)
        consumer.consume(searchHistoryRepository.getSearchHistory())
    }
}