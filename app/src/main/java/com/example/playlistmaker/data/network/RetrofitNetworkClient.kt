package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.ItunesSearchRequest
import com.example.playlistmaker.data.dto.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val ITUNES_BASE_URL = "https://itunes.apple.com"

class RetrofitNetworkClient: NetworkClient {

    private val retrofit = Retrofit.Builder()
        .baseUrl(ITUNES_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ItunesApiService::class.java)

    override fun doRequest(dto: Any): Response {

        if (dto is ItunesSearchRequest) {
            val resp = itunesService.searchTracks(dto.expression).execute()

            val body = resp.body() ?: Response()

            return  body.apply { resulCode = resp.code() }
        } else {
            return Response().apply { resulCode = 400 }
        }
    }
}

