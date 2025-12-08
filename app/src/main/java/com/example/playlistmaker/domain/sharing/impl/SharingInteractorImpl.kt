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
    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
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