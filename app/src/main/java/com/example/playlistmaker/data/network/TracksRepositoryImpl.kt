package com.example.playlistmaker.data.network

import android.icu.text.SimpleDateFormat
import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.ItunesSearchRequest
import com.example.playlistmaker.data.dto.ItunesSearchResponse
import com.example.playlistmaker.domain.search.TracksRepository
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl (
    private val dateFormat: SimpleDateFormat,
    private val networkClient: NetworkClient): TracksRepository {

    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(ItunesSearchRequest(expression))
        when (response.resulCode) {
            -1 -> {
                emit(Resource.Error("Connection error"))
            }
            200 -> {
                with(response as ItunesSearchResponse) {
                    val data = response.results.map {
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
                    emit (Resource.Success(data))
                }
            }
            else -> {
                emit(Resource.Error("Server error"))
            }
        }
    }
}