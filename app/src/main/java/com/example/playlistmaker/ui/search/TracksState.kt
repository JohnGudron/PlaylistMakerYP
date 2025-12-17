package com.example.playlistmaker.ui.search

import com.example.playlistmaker.domain.models.Track

sealed interface TracksState {

    object Loading : TracksState

    data class Content(
        val tracks: List<Track>
    ) : TracksState

    data class Error(
        val errorMessage: String
    ) : TracksState

    data class Empty(
        val message: String
    ) : TracksState

    data class History(val history: List<Track>): TracksState
}

data class CombinedTrackState(val tracksState: TracksState, val history: List<Track>)