package com.example.playlistmaker.ui.settings.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel?.observeTheme()?.observe(this) {
            binding.switcher.isChecked = it.enableDarkTheme
        }

        binding.backBtn.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        binding.shareBtn.setOnClickListener {
            viewModel.shareApp(this)
        }

        binding.supportBtn.setOnClickListener {
            viewModel.openSupport(this)
        }

        binding.agreementBtn.setOnClickListener {
            viewModel.openTerms(this)
        }

        binding.switcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.switchTheme(isChecked)
        }
    }
}