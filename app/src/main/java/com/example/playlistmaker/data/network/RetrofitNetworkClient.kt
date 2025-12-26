package com.example.playlistmaker.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.ItunesSearchRequest
import com.example.playlistmaker.data.dto.Response


class RetrofitNetworkClient(
    private val itunesService: ItunesApiService,
    private val context: Context): NetworkClient {

  override fun doRequest(dto: Any): Response {

        if (!isConnected()) {
            return Response().apply { resulCode = -1 }
        }

        if (dto is ItunesSearchRequest) {
            val resp = itunesService.searchTracks(dto.expression).execute()

            val body = resp.body() ?: Response()

            return  body.apply { resulCode = resp.code() }
        } else {
            return Response().apply { resulCode = 400 }
        }
    }

    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }
}

