package com.example.playlistmaker.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.ItunesSearchRequest
import com.example.playlistmaker.data.dto.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class RetrofitNetworkClient(
    private val itunesService: ItunesApiService,
    private val context: Context): NetworkClient {

  override suspend fun doRequest(dto: Any): Response {

        if (!isConnected()) {
            return Response().apply { resulCode = -1 }
        }

      return if (dto is ItunesSearchRequest) {
          withContext(Dispatchers.IO) {
              try {
                  val response = itunesService.searchTracks(dto.expression)
                  response.apply { resulCode = 200 }
              } catch (e: Throwable) {
                  Response().apply { resulCode = 500 }
              }
          }
      } else {
          Response().apply { resulCode = 400 }
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

