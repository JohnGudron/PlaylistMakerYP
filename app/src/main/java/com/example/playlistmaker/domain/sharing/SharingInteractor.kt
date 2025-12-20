package com.example.playlistmaker.domain.sharing

import android.content.Context

interface SharingInteractor {
    fun shareApp(context: Context)

    fun openTerms(context: Context)

    fun openSupport(context: Context)
}