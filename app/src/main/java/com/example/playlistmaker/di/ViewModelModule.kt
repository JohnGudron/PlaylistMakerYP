package com.example.playlistmaker.di

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.example.playlistmaker.App
import com.example.playlistmaker.ui.media.view_model.MediaViewModel
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    factory {
        MediaPlayer()
    }

    factory {
        Handler(Looper.getMainLooper())
    }

    viewModel { (url: String) ->
        MediaViewModel(url, get(),get(), get())
    }

    viewModel {
        SearchViewModel(get(),get(),get(),androidApplication() as App)
    }

    viewModel {
        SettingsViewModel(get(),get())
    }
}