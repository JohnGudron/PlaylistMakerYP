package com.example.playlistmaker.domain.sharing.impl

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.sharing.ExternalNavigator
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.model.EmailData

class SharingInteractorImpl (
    private val externalNavigator: ExternalNavigator,
    private val context: Context
) : SharingInteractor {
    override fun shareApp(context: Context) {
        externalNavigator.shareLink(context, getShareAppLink())
    }

    override fun openTerms(context: Context) {
        externalNavigator.openLink(context, getTermsLink())
    }

    override fun openSupport(context: Context) {
        externalNavigator.openEmail(context, getSupportEmailData())
    }

    private fun getShareAppLink(): String {
        return context.getString(R.string.share_link)
    }

    private fun getSupportEmailData(): EmailData {
        return EmailData(context.getString(R.string.support_email), context.getString(R.string.support_subject), context.getString(R.string.support_msg))
    }

    private fun getTermsLink(): String {
        return context.getString(R.string.offer_link)
    }
}