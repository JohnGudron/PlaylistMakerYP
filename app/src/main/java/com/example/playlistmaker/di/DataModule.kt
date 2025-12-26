package com.example.playlistmaker.di

import android.content.Context
import android.icu.text.SimpleDateFormat
import com.example.playlistmaker.PREFERENCES
import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.local.LocalHistoryStorage
import com.example.playlistmaker.data.local.SharedPrefsStorage
import com.example.playlistmaker.data.network.ItunesApiService
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale

const val ITUNES_BASE_URL = "https://itunes.apple.com"

val dataModule = module {

    // general
    single {
        SimpleDateFormat("mm:ss", Locale.getDefault())
    }

    single {
        androidContext()
            .getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
    }

    factory { Gson() }

    // network
    single<ItunesApiService> {
        Retrofit.Builder()
            .baseUrl(ITUNES_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApiService::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get(), androidContext())
    }

    // local
    single<LocalHistoryStorage> {
        SharedPrefsStorage(get(), get())
    }
}