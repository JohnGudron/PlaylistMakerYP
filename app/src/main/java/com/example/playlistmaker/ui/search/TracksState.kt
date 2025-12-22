package com.example.playlistmaker.ui.search

import com.example.playlistmaker.domain.search.model.Track

sealed interface TracksState {

    object Loading : TracksState

    data class Content(
        val tracks: List<Track>,
        val history: List<Track>
    ) : TracksState

    data class Error(
        val errorMessage: String
    ) : TracksState

    data class Empty(
        val message: String
    ) : TracksState
}