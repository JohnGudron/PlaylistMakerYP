package com.example.playlistmaker.ui.settings.view_model

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.domain.settings.SettingsInteractor
import com.example.playlistmaker.domain.settings.model.ThemeSettings
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.util.Creator

class SettingsViewModel(
    app: Application
) : ViewModel() {

    private val sharingInteractor: SharingInteractor = Creator.provideSharingInteractor(app)
    private val settingsInteractor: SettingsInteractor = Creator.provideSettingsInteractor(app)

    private val darkThemeLiveData = MutableLiveData<ThemeSettings>(settingsInteractor.getThemeSettings())
    fun observeTheme(): LiveData<ThemeSettings> = darkThemeLiveData

    fun switchTheme(enableDarkTheme: Boolean) {
        val newSettings = settingsInteractor.getThemeSettings().copy(enableDarkTheme = enableDarkTheme)
        settingsInteractor.updateThemeSetting(newSettings)
        darkThemeLiveData.postValue(newSettings)
    }

    fun shareApp(context: Context) {
        sharingInteractor.shareApp(context)
    }

    fun openSupport(context: Context) {
        sharingInteractor.openSupport(context)
    }

    fun openTerms(context: Context) {
        sharingInteractor.openTerms(context)
    }

    companion object {
        fun getFactory (): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as Application)
                SettingsViewModel(app)
            }
        }
    }
}