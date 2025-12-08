package com.example.playlistmaker.ui.settings.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.App
import com.example.playlistmaker.Creator
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            SettingsViewModel.getFactory(
                Creator.provideSharingInteractor(this),
                Creator.provideSettingsInteractor()
            )
        ).get(SettingsViewModel::class.java)

        viewModel?.observeTheme()?.observe(this) {
            binding.switcher.isChecked = it.enableDarkTheme
        }

        binding.backBtn.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        //binding.switcher.isChecked = getSharedPreferences(PREFERENCES, MODE_PRIVATE).getBoolean(DARK_THEME, false)

        binding.shareBtn.setOnClickListener {
            viewModel.shareApp()
        }

        binding.supportBtn.setOnClickListener {
            viewModel.openSupport()
        }

        binding.agreementBtn.setOnClickListener {
            viewModel.openTerms()
            //startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.offer_link))))
        }

        binding.switcher.setOnCheckedChangeListener { _, isChecked ->
            (applicationContext as App).switchTheme(isChecked)
        }
    }
}