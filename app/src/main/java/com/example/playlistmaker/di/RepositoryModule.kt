package com.example.playlistmaker.di

import com.example.playlistmaker.App
import com.example.playlistmaker.data.local.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.network.TracksRepositoryImpl
import com.example.playlistmaker.domain.search.SearchHistoryRepository
import com.example.playlistmaker.domain.search.TracksRepository
import com.example.playlistmaker.domain.settings.SettingsRepository
import com.example.playlistmaker.domain.settings.impl.SettingsRepositoryImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val repositoryModule = module {

    single<TracksRepository> {
        TracksRepositoryImpl(get(), get())
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get(), get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get(), androidApplication() as App)
    }
}
