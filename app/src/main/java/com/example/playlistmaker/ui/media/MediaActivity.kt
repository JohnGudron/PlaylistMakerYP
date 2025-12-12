package com.example.playlistmaker.ui.media

import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMediaBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.search.activity.TRACK
import com.google.gson.Gson
import java.util.Locale

class MediaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaBinding
    private lateinit var timeUpdater: Runnable
    private lateinit var track: Track
    private val mediaPlayer = MediaPlayer()
    private var mediaPlayerState = STATE_DEFAULT
    private val handler = Handler(Looper.getMainLooper())

    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        timeUpdater = updateDurationTv()

        // TODO exception below when navigation from main activity
        track = Gson().fromJson(intent.getStringExtra(TRACK), Track::class.java)

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
            playBackControl()
        }
        preparePlayer()
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            mediaPlayerState = STATE_PREPARED
            binding.playBtn.isClickable = true
        }
        mediaPlayer.setOnCompletionListener {
            mediaPlayerState = STATE_PREPARED
            handler.removeCallbacks(timeUpdater)
            binding.playBtn.setImageResource(R.drawable.ic_play_btn_100)
            binding.timeTv.text = getString(R.string.time_placeholder)
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlaying()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handler.removeCallbacks(timeUpdater)
    }

    private fun startPlaying() {
        mediaPlayer.start()
        mediaPlayerState = STATE_PLAYING
        binding.playBtn.setImageResource(R.drawable.ic_pause_btn_100)
        handler.post(timeUpdater)
    }

    private fun pausePlaying() {
        mediaPlayer.pause()
        mediaPlayerState = STATE_PAUSED
        binding.playBtn.setImageResource(R.drawable.ic_play_btn_100)
        handler.removeCallbacks(timeUpdater)
    }

    private fun playBackControl() {
        when(mediaPlayerState) {
            STATE_PREPARED, STATE_PAUSED -> {
                startPlaying()
            }
            STATE_PLAYING -> {
                pausePlaying()
            }
        }
    }

    private fun updateDurationTv(): Runnable {
        return object : Runnable {
            override fun run() {
                binding.timeTv.text = dateFormat.format(mediaPlayer.currentPosition)
                handler.postDelayed(this, DELAY_HALF_SECOND)
            }
        }
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DELAY_HALF_SECOND = 500L
        private const val FRAGMENT_TIME = 29500L
    }
}