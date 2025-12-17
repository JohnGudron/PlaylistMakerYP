package com.example.playlistmaker.ui.search.view_model

import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.App
import com.example.playlistmaker.PREFERENCES
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.search.TracksState
import com.example.playlistmaker.util.Creator


class SearchViewModel(private val app: App): ViewModel() {

    private val tracksInteractor = Creator.provideTracksInteractor(app)
    private val searchHistoryInteractor =
        Creator.provideSearchHistoryInteractor(
            app.getSharedPreferences(
                PREFERENCES, MODE_PRIVATE
            )
        )
    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable = Runnable { makeSearch(searchText) }
    private var searchText = ""

    private val stateLiveData = MutableLiveData<TracksState>()
    fun observeState(): LiveData<TracksState> = stateLiveData

    //private val historyLiveData = MutableLiveData<List<Track>>()
    //fun observeHistory(): LiveData<List<Track>> = historyLiveData

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
                stateLiveData.postValue(TracksState.History(searchHistory))
            }
        })
    }

    fun getSearchHistory() {
        searchHistoryInteractor.getSearchHistory(object : SearchHistoryInteractor.SearchHistoryConsumer {
            override fun consume(searchHistory: List<Track>) {
                stateLiveData.postValue(TracksState.History(searchHistory))
            }
        })
    }

    fun clearSearchHistory() {
        searchHistoryInteractor.clearSearchHistory(object : SearchHistoryInteractor.SearchHistoryConsumer {
            override fun consume(searchHistory: List<Track>) {
                stateLiveData.postValue(TracksState.History(searchHistory))
            }
        })
    }

    private fun renderState(state: TracksState) {
        stateLiveData.postValue(state)
    }

    companion object {

        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(this[APPLICATION_KEY] as App)
            }
        }

        const val SEARCH_DEBOUNCE_DELAY = 1500L
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(searchRunnable)
    }

}