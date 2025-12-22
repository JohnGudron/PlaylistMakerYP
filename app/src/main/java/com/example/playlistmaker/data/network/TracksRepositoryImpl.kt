package com.example.playlistmaker.data.network

import android.icu.text.SimpleDateFormat
import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.ItunesSearchRequest
import com.example.playlistmaker.data.dto.ItunesSearchResponse
import com.example.playlistmaker.domain.search.TracksRepository
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.util.Resource

class TracksRepositoryImpl (
    private val dateFormat: SimpleDateFormat,
    private val networkClient: NetworkClient): TracksRepository {

    override fun searchTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(ItunesSearchRequest(expression))
        return when (response.resulCode) {
            -1 -> {
                Resource.Error("Connection error")
            }
            200 -> {
                return Resource.Success((response as ItunesSearchResponse).results.map {
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
                })
            }
            else -> {
                return Resource.Error("Server error")
            }
        }
    }
}