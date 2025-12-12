package com.example.playlistmaker.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.databinding.ActivityMainBinding
import com.example.playlistmaker.ui.media.MediaActivity
import com.example.playlistmaker.ui.search.activity.SearchActivity
import com.example.playlistmaker.ui.settings.activity.SettingsActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val searchBtnOnClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(p0: View?) {
                startActivity(Intent(this@MainActivity, SearchActivity::class.java))
            }
        }

        binding.searchBtn.setOnClickListener (searchBtnOnClickListener)

        binding.mediaBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, MediaActivity::class.java))
        }

        binding.settingsBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
        }
    }
}