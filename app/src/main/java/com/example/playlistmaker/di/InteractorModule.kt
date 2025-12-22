package com.example.playlistmaker.di

import com.example.playlistmaker.domain.search.SearchHistoryInteractor
import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.domain.search.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.search.impl.TracksInteractorImpl
import com.example.playlistmaker.domain.settings.SettingsInteractor
import com.example.playlistmaker.domain.settings.impl.SettingsInteractorImpl
import com.example.playlistmaker.domain.sharing.ExternalNavigator
import com.example.playlistmaker.domain.sharing.ExternalNavigatorImpl
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.impl.SharingInteractorImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import java.util.concurrent.Executors

val interactorModule = module {

    single<ExternalNavigator> {
        ExternalNavigatorImpl()
    }

    factory{
        Executors.newCachedThreadPool()
    }

    single<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(get())
    }

    single<TracksInteractor>{
        TracksInteractorImpl(get(), get())
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }


    single<SharingInteractor> {
        SharingInteractorImpl(get(), androidContext())
    }

}