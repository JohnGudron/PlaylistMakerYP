package com.example.playlistmaker.ui.search.view_model

import android.content.Context
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
import com.example.playlistmaker.ui.search.activity.SearchActivity
import com.example.playlistmaker.util.Creator


class SearchViewModel(private val context: Context): ViewModel() {

    private val tracksInteractor = Creator.provideTracksInteractor(context)
    private val searchHistoryInteractor =
        Creator.provideSearchHistoryInteractor(
            context.getSharedPreferences(
                PREFERENCES, MODE_PRIVATE
            )
        )
    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable = Runnable { makeSearch(searchText) }
    private var searchText = ""
    private var lastResponse = ""

    private val stateLiveData = MutableLiveData<TracksState>()
    fun observeState(): LiveData<TracksState> = stateLiveData

    private val historyLiveData = MutableLiveData<List<Track>>()
    fun observeHistory(): LiveData<List<Track>> = historyLiveData

    /*searchHistoryInteractor.getSearchHistory(object: SearchHistoryInteractor.SearchHistoryConsumer {
        override fun consume(searchHistory: List<Track>) {
            history.addAll(searchHistory)
        }
    })*/

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
                                        errorMessage = context.getString(R.string.error_text),
                                    )
                                )
                                //showToast.postValue(errorMessage)
                            }

                            tracks.isEmpty() -> {
                                renderState(
                                    TracksState.Empty(
                                        message = context.getString(R.string.nothing_was_found),
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
        handler.postDelayed(searchRunnable, SearchActivity.SEARCH_DEBOUNCE_DELAY)
    }

    fun addTrackToHistory(track: Track) {
        searchHistoryInteractor.addTrackToHistory(track,object : SearchHistoryInteractor.SearchHistoryConsumer {
            override fun consume(searchHistory: List<Track>) {
                historyLiveData.postValue(searchHistory)
                /*history.clear()
                history.addAll(searchHistory)
                historyAdapter.notifyDataSetChanged()*/
            }
        })
    }

    fun getSearchHistory() {
        searchHistoryInteractor.getSearchHistory(object : SearchHistoryInteractor.SearchHistoryConsumer {
            override fun consume(searchHistory: List<Track>) {
                historyLiveData.postValue(searchHistory)
            }
        })
    }

    fun clearSearchHistory() {
        searchHistoryInteractor.clearSearchHistory(object : SearchHistoryInteractor.SearchHistoryConsumer {
            override fun consume(searchHistory: List<Track>) {
                historyLiveData.postValue(searchHistory)
            }
        })
    }

    private fun renderState(state: TracksState) {
        stateLiveData.postValue(state)
    }

    /*private fun itemClickDebounce(): Boolean {
        val cur = itemClickAllowed
        if (itemClickAllowed) {
            itemClickAllowed = false
            handler.postDelayed({itemClickAllowed = true}, SearchActivity.ITEM_CLICK_DEBOUNCE)
        }
        return cur
    }*/

    companion object {

        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(this[APPLICATION_KEY] as App)
            }
        }

        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val SEARCH_DEBOUNCE_DELAY = 1500L
        const val ITEM_CLICK_DEBOUNCE = 1000L
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(searchRunnable)
    }

}