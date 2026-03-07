package com.example.playlistmaker.data.local

import android.icu.text.SimpleDateFormat
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.domain.search.SearchHistoryRepository
import com.example.playlistmaker.domain.search.model.Track

class SearchHistoryRepositoryImpl (
    private val dateFormat:  SimpleDateFormat,
    private val localStorage: LocalHistoryStorage,
    private val appDatabase: AppDatabase): SearchHistoryRepository {

    override suspend fun getSearchHistory(): List<Track> {

        val favorite = appDatabase.trackDao().getTracksIds()

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
                it.trackId in favorite
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