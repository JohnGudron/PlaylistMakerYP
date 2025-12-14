package com.example.playlistmaker.ui.settings.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, SettingsViewModel.getFactory()).get(SettingsViewModel::class.java)

        viewModel?.observeTheme()?.observe(this) {
            binding.switcher.isChecked = it.enableDarkTheme
        }

        binding.backBtn.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        binding.shareBtn.setOnClickListener {
            viewModel.shareApp()
        }

        binding.supportBtn.setOnClickListener {
            viewModel.openSupport()
        }

        binding.agreementBtn.setOnClickListener {
            viewModel.openTerms()
        }

        binding.switcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.switchTheme(isChecked)
        }
    }
}