package com.example.playlistmaker.domain.settings.impl

import android.content.SharedPreferences
import com.example.playlistmaker.App
import com.example.playlistmaker.DARK_THEME
import com.example.playlistmaker.domain.settings.SettingsRepository
import com.example.playlistmaker.domain.settings.model.ThemeSettings

class SettingsRepositoryImpl(
    private val sharedPrefs: SharedPreferences,
    private val app: App): SettingsRepository {

    override fun getThemeSettings(): ThemeSettings {
        return ThemeSettings(sharedPrefs.getBoolean(DARK_THEME, false))
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        sharedPrefs.edit().putBoolean(DARK_THEME, settings.enableDarkTheme).apply()
        app.switchTheme(settings.enableDarkTheme)
    }
}