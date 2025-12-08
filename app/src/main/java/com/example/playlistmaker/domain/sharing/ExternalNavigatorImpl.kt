package com.example.playlistmaker.domain.sharing

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.domain.sharing.model.EmailData

class ExternalNavigatorImpl(private val context: Context): ExternalNavigator {
    override fun shareLink(link: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, link)
            type = "text/plain"
        }
        context.startActivity(sendIntent)
    }

    override fun openLink(link: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
    }

    override fun openEmail(emailData: EmailData) {
        val shareIntent = Intent(Intent.ACTION_SENDTO)
        shareIntent.data = Uri.parse("mailto:")
        shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(emailData.email))
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, emailData.subject)
        shareIntent.putExtra(Intent.EXTRA_TEXT, emailData.text)
        context.startActivity(shareIntent)
    }
}