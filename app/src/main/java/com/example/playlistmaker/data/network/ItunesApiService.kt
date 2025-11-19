package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.dto.ItunesSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApiService {
    @GET("search?entity=song")
    fun searchTracks(
        @Query("term") searchTerm: String
    ): Call<ItunesSearchResponse>
}

