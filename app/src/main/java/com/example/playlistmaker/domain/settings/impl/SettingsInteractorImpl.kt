package com.example.playlistmaker.domain.settings.impl

import android.app.Application
import android.content.Context.MODE_PRIVATE
import com.example.playlistmaker.App
import com.example.playlistmaker.DARK_THEME
import com.example.playlistmaker.PREFERENCES
import com.example.playlistmaker.domain.settings.SettingsInteractor
import com.example.playlistmaker.domain.settings.model.ThemeSettings

class SettingsInteractorImpl (private val app: Application): SettingsInteractor {
    override fun getThemeSettings(): ThemeSettings {
        return ThemeSettings(app.getSharedPreferences(PREFERENCES, MODE_PRIVATE).getBoolean(DARK_THEME, false))
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        app.getSharedPreferences(PREFERENCES, MODE_PRIVATE).edit()
            .putBoolean(DARK_THEME, settings.enableDarkTheme).apply()
        (app as App).switchTheme(settings.enableDarkTheme)
    }
}