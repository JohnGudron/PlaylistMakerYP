package com.example.playlistmaker

import android.content.SharedPreferences
import com.example.playlistmaker.data.local.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.local.SharedPrefsStorage
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.network.TracksRepositoryImpl
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.impl.TracksInteractorImpl

object Creator {
    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    private fun getSearchHistoryRepository(sharedPrefs: SharedPreferences): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(SharedPrefsStorage(sharedPrefs))
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    fun provideSearchHistoryInteractor(sharedPrefs: SharedPreferences): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository(sharedPrefs))
    }
}