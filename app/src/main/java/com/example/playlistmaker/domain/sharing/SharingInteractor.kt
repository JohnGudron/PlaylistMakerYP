package com.example.playlistmaker.domain.sharing

import android.content.Context

interface SharingInteractor {

    fun shareApp(context: Context)

    fun sharePlaylist(context: Context, message: String)

    fun openTerms(context: Context)

    fun openSupport(context: Context)
}