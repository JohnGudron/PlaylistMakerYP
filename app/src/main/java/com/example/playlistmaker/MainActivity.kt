package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchBtn = findViewById<Button>(R.id.searchBtn)
        val mediaBtn = findViewById<Button>(R.id.mediaBtn)
        val settingsBtn = findViewById<Button>(R.id.settingsBtn)

        val searchBtnOnClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(p0: View?) {
                startActivity(Intent(this@MainActivity, SearchActivity::class.java))
            }
        }

        searchBtn.setOnClickListener (searchBtnOnClickListener)

        mediaBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, MediaActivity::class.java))
        }

        settingsBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
        }
    }
}