package com.example.playlistmaker.ui.settings.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.settings.SettingsInteractor
import com.example.playlistmaker.domain.settings.model.ThemeSettings
import com.example.playlistmaker.domain.sharing.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor
) : ViewModel() {

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
}