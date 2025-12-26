package com.example.playlistmaker.ui.search.view_model

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.search.SearchHistoryInteractor
import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.search.TracksState


class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor,
    private val handler: Handler,
    private val app: App): ViewModel() {

    private var searchRunnable: Runnable = Runnable { makeSearch(searchText) }
    private var searchText = ""

    private val stateLiveData = MutableLiveData<TracksState>()
    fun observeState(): LiveData<TracksState> = stateLiveData

    fun makeSearch(str: String) {
        if (str.isNotEmpty()) {
            renderState(
                TracksState.Loading
            )

            tracksInteractor.searchTracks(str, object : TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                    handler.post {
                        val tracks = mutableListOf<Track>()
                        if (foundTracks != null) {
                            tracks.addAll(foundTracks)
                        }

                        when {
                            errorMessage != null -> {
                                renderState(
                                    TracksState.Error(
                                        errorMessage = app.getString(R.string.error_text),
                                    )
                                )
                            }

                            tracks.isEmpty() -> {
                                renderState(
                                    TracksState.Empty(
                                        message = app.getString(R.string.nothing_was_found),
                                    )
                                )
                            }

                            else -> {
                                renderState(
                                    TracksState.Content(
                                        tracks = tracks,
                                        history = emptyList()
                                    )
                                )
                            }
                        }

                    }
                }
            })
        }
    }

    fun searchDebounce(str: String) {
        searchText = str
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    fun addTrackToHistory(track: Track) {
        searchHistoryInteractor.addTrackToHistory(track,object : SearchHistoryInteractor.SearchHistoryConsumer {
            override fun consume(searchHistory: List<Track>) {
                if (stateLiveData.value is TracksState.Content) {
                    stateLiveData.postValue(TracksState.Content((stateLiveData.value as TracksState.Content).tracks, searchHistory))
                } else {
                    stateLiveData.postValue(TracksState.Content(emptyList(), searchHistory))
                }
            }
        })
    }

    fun getSearchHistory() {
        searchHistoryInteractor.getSearchHistory(object : SearchHistoryInteractor.SearchHistoryConsumer {
            override fun consume(searchHistory: List<Track>) {
                if (stateLiveData.value is TracksState.Content) {
                    stateLiveData.postValue(TracksState.Content((stateLiveData.value as TracksState.Content).tracks, searchHistory))
                } else {
                    stateLiveData.postValue(TracksState.Content(emptyList(), searchHistory))
                }
            }
        })
    }

    fun clearSearchHistory() {
        searchHistoryInteractor.clearSearchHistory(object : SearchHistoryInteractor.SearchHistoryConsumer {
            override fun consume(searchHistory: List<Track>) {
                if (stateLiveData.value is TracksState.Content) {
                    stateLiveData.postValue(TracksState.Content((stateLiveData.value as TracksState.Content).tracks, searchHistory))
                } else {
                    stateLiveData.postValue(TracksState.Content(emptyList(), searchHistory))
                }
            }
        })
    }

    private fun renderState(state: TracksState) {
        stateLiveData.postValue(state)
    }

    companion object {

        const val SEARCH_DEBOUNCE_DELAY = 1500L
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(searchRunnable)
    }
}