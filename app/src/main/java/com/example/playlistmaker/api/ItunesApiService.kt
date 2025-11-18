package com.example.playlistmaker.api

import com.example.playlistmaker.model.Track
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApiService {
    @GET("search?entity=song")
    fun searchTracks(
        @Query("term") searchTerm: String
    ): Call<ItunesSearchResponse>
}

class ItunesSearchResponse (val resultCount: Int, val results: List<Track>)