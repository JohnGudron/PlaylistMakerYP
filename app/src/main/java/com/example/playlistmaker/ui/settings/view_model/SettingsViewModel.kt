package com.example.playlistmaker.ui.settings.view_model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.domain.settings.SettingsInteractor
import com.example.playlistmaker.domain.settings.impl.SettingsInteractorImpl
import com.example.playlistmaker.domain.settings.model.ThemeSettings
import com.example.playlistmaker.domain.sharing.ExternalNavigatorImpl
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.impl.SharingInteractorImpl

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
) : ViewModel() {

    private val darkThemeLiveData = MutableLiveData<ThemeSettings>(settingsInteractor.getThemeSettings())
    fun observeTheme(): LiveData<ThemeSettings> = darkThemeLiveData

    fun switchTheme(enableDarkTheme: Boolean) {
        val newSettings = settingsInteractor.getThemeSettings().copy(enableDarkTheme = enableDarkTheme)
        settingsInteractor.updateThemeSetting(newSettings)
        darkThemeLiveData.postValue(newSettings)
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun openSupport() {
        sharingInteractor.openSupport()
    }

    fun openTerms() {
        sharingInteractor.openTerms()
    }

    companion object {
        fun getFactory (): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as Application)
                SettingsViewModel(SharingInteractorImpl(ExternalNavigatorImpl(app),app), SettingsInteractorImpl(app))
            }
        }
    }
}