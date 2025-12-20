package com.example.playlistmaker.domain.sharing

import android.content.Context
import com.example.playlistmaker.domain.sharing.model.EmailData

interface ExternalNavigator {

    fun shareLink(context: Context,link:String)

    fun openLink(context: Context, link: String)

    fun openEmail(context: Context, emailData: EmailData)
}