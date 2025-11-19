package com.example.playlistmaker.data.network

import android.icu.text.SimpleDateFormat
import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.ItunesSearchRequest
import com.example.playlistmaker.data.dto.ItunesSearchResponse
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track
import java.util.Locale

class TracksRepositoryImpl (private val networkClient: NetworkClient): TracksRepository {

    private val dateFormat =  SimpleDateFormat("mm:ss", Locale.getDefault())

    override fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(ItunesSearchRequest(expression))
        if (response.resulCode == 200) {

            return (response as ItunesSearchResponse).results.map {
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
        } else {
            return emptyList()
        }
    }
}