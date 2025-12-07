package com.example.playlistmaker.ui.settings.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.domain.settings.SettingsInteractor
import com.example.playlistmaker.domain.settings.model.ThemeSettings
import com.example.playlistmaker.domain.sharing.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
) : ViewModel() {

    private val darkThemeLiveData = MutableLiveData<ThemeSettings>()
    fun observeTheme(): LiveData<ThemeSettings> = darkThemeLiveData

    fun switchTheme(enableDarkTheme: Boolean) {
                val newSettings = settingsInteractor.getThemeSettings().copy(enableDarkTheme = enableDarkTheme)
        settingsInteractor.updateThemeSetting(newSettings)
        darkThemeLiveData.postValue(newSettings)
    }

    companion object {
        fun getFactory (sharingInteractor: SharingInteractor, settingsInteractor: SettingsInteractor): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(sharingInteractor, settingsInteractor)
            }
        }
    }
}