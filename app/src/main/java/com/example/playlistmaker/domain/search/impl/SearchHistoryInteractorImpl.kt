package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.domain.search.SearchHistoryInteractor
import com.example.playlistmaker.domain.search.SearchHistoryRepository
import com.example.playlistmaker.domain.search.model.Track

class SearchHistoryInteractorImpl (private val searchHistoryRepository: SearchHistoryRepository):
    SearchHistoryInteractor {

    override suspend fun getSearchHistory(consumer: SearchHistoryInteractor.SearchHistoryConsumer) {
        consumer.consume(searchHistoryRepository.getSearchHistory())
    }

    override suspend fun clearSearchHistory(consumer: SearchHistoryInteractor.SearchHistoryConsumer) {
        searchHistoryRepository.clearSearchHistory()
        consumer.consume(searchHistoryRepository.getSearchHistory())
    }

    override suspend fun addTrackToHistory(track: Track, consumer: SearchHistoryInteractor.SearchHistoryConsumer) {
        searchHistoryRepository.addTrackToHistory(track)
        consumer.consume(searchHistoryRepository.getSearchHistory())
    }
}