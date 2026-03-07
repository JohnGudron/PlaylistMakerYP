package com.example.playlistmaker.ui.search.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.search.SearchHistoryInteractor
import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.search.TracksState
import com.example.playlistmaker.util.debounce
import kotlinx.coroutines.launch


class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor,
    private val app: App): ViewModel() {

    private var searchText = ""

    private val trackSearchDebounce = debounce<String>(SEARCH_DEBOUNCE_DELAY, viewModelScope, true) { str->
        makeSearch(str)
    }
    private val stateLiveData = MutableLiveData<TracksState>()
    fun observeState(): LiveData<TracksState> = stateLiveData

    fun makeSearch(str: String) {
        if (str.isNotEmpty()) {
            renderState(TracksState.Loading)

            viewModelScope.launch {
                tracksInteractor
                    .searchTracks(str)
                    .collect { pair->
                        processResult(pair.first, pair.second)
                    }
            }
        }
    }

    private fun processResult(foundTracks: List<Track>?, errorMessage: String?) {
            val tracks = mutableListOf<Track>()
            if (foundTracks != null) {
                tracks.addAll(foundTracks)
            }

            when {
                errorMessage != null -> {
                    renderState(TracksState.Error(errorMessage = app.getString(R.string.error_text)))
                }

                tracks.isEmpty() -> {
                    renderState(TracksState.Empty(message = app.getString(R.string.nothing_was_found)))
                }

                else -> {
                    renderState(TracksState.Content(tracks = tracks, history = emptyList()))
                }
            }
    }

    fun searchDebounce(str: String) {
        if (searchText != str) {
            searchText = str
            trackSearchDebounce(str)
        }
    }

    fun addTrackToHistory(track: Track) {
        viewModelScope.launch { searchHistoryInteractor.addTrackToHistory(track,object : SearchHistoryInteractor.SearchHistoryConsumer {
            override fun consume(searchHistory: List<Track>) {
                if (stateLiveData.value is TracksState.Content) {
                    stateLiveData.postValue(TracksState.Content((stateLiveData.value as TracksState.Content).tracks, searchHistory))
                } else {
                    stateLiveData.postValue(TracksState.Content(emptyList(), searchHistory))
                }
            }
        })}
    }

    fun getSearchHistory() {
        viewModelScope.launch { searchHistoryInteractor.getSearchHistory(object : SearchHistoryInteractor.SearchHistoryConsumer {
            override fun consume(searchHistory: List<Track>) {
                if (stateLiveData.value is TracksState.Content) {
                    stateLiveData.postValue(TracksState.Content((stateLiveData.value as TracksState.Content).tracks, searchHistory))
                } else {
                    stateLiveData.postValue(TracksState.Content(emptyList(), searchHistory))
                }
            }
        }) }
    }

    fun clearSearchHistory() {
        viewModelScope.launch { searchHistoryInteractor.clearSearchHistory(object : SearchHistoryInteractor.SearchHistoryConsumer {
            override fun consume(searchHistory: List<Track>) {
                if (stateLiveData.value is TracksState.Content) {
                    stateLiveData.postValue(TracksState.Content((stateLiveData.value as TracksState.Content).tracks, searchHistory))
                } else {
                    stateLiveData.postValue(TracksState.Content(emptyList(), searchHistory))
                }
            }
        }) }
    }

    private fun renderState(state: TracksState) {
        stateLiveData.postValue(state)
    }

    companion object {
        const val SEARCH_DEBOUNCE_DELAY = 1500L
    }

}