package com.example.playlistmaker.data.local

import android.icu.text.SimpleDateFormat
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track
import java.util.Locale

class SearchHistoryRepositoryImpl (private val localStorage: LocalHistoryStorage): SearchHistoryRepository {

    private val dateFormat =  SimpleDateFormat("mm:ss", Locale.getDefault())

    override fun getSearchHistory(): List<Track> {
        return localStorage.getHistory().map {
            Track(
                it.trackName,
                it.artistName,
                dateFormat.format(it.trackTimeMillis),
                it.artworkUrl100,
                it.trackId,
                it.collectionName,
                it.releaseDate,
                it.primaryGenreName,
                it.country,
                it.previewUrl,
            )
        }
    }

    override fun clearSearchHistory() {
        localStorage.clearHistory()
    }

    override fun addTrackToHistory(track: Track) {
        localStorage.addTrackToHistory(
            TrackDto(
                trackName = track.trackName,
                artistName = track.artistName,
                trackTimeMillis = dateFormat.parse(track.trackDuration).time,
                artworkUrl100 = track.artworkUrl100,
                trackId = track.trackId,
                collectionName = track.collectionName,
                releaseDate = track.releaseDate,
                primaryGenreName = track.primaryGenreName,
                country = track.country,
                previewUrl = track.previewUrl,
            )
        )
    }
}