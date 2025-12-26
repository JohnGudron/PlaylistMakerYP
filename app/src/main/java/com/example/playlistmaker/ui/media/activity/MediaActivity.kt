package com.example.playlistmaker.ui.media.activity

import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMediaBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.media.PlayerState
import com.example.playlistmaker.ui.media.view_model.MediaViewModel
import com.example.playlistmaker.ui.search.activity.TRACK
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MediaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaBinding
    private val track by lazy {
        Gson().fromJson(intent.getStringExtra(TRACK), Track::class.java)
    }
    private val viewModel: MediaViewModel by viewModel {
        parametersOf(track.previewUrl)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.observePlayerState().observe(this) {
            binding.playBtn.setImageResource(if (it is PlayerState.Playing) R.drawable.ic_pause_btn_100 else R.drawable.ic_play_btn_100)
            binding.playBtn.isClickable = (it !is PlayerState.Default)
            binding.timeTv.text = it.progress
        }

        binding.backBtn.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        binding.durationContentTv.text = track.trackDuration
        binding.albumContentTv.text = track.collectionName
        binding.yearContentTv.text = track.releaseDate
        binding.genreContentTv.text = track.primaryGenreName
        binding.countryContentTv.text = track.country
        binding.nameTv.text = track.trackName
        binding.artistTv.text = track.artistName

        Glide.with(binding.songPoster.context)
            .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .placeholder(R.drawable.ic_poster_placeholder_312)
            .centerCrop()
            .transform(RoundedCorners(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 8f, binding.songPoster.context.resources.displayMetrics
                    ).toInt()
                ))
            .into(binding.songPoster)

        binding.playBtn.setOnClickListener {
            viewModel.playBackControl()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlaying()
    }
}