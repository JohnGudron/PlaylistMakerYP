package com.example.playlistmaker

import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.model.Track
import com.google.gson.Gson
import java.util.Locale

class MediaActivity : AppCompatActivity() {

    private lateinit var timeUpdater: Runnable
    private lateinit var poster: ImageView
    private lateinit var name: TextView
    private lateinit var artist: TextView
    private lateinit var playlistBtn: ImageView
    private lateinit var playBtn: ImageView
    private lateinit var favoriteBtn: ImageView
    private lateinit var duration: TextView
    private lateinit var album: TextView
    private lateinit var year: TextView
    private lateinit var genre: TextView
    private lateinit var country: TextView
    private lateinit var track: Track
    private lateinit var currentTimeTv: TextView
    private val mediaPlayer = MediaPlayer()
    private var mediaPlayerState = STATE_DEFAULT
    private val handler = Handler(Looper.getMainLooper())

    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        val backBtn = findViewById<ImageView>(R.id.back_btn)
        backBtn.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        timeUpdater = updateDurationTv()
        poster = findViewById(R.id.song_poster)
        duration = findViewById(R.id.duration_content_tv)
        album = findViewById(R.id.album_content_tv)
        year = findViewById(R.id.year_content_tv)
        genre = findViewById(R.id.genre_content_tv)
        country = findViewById(R.id.country_content_tv)
        name = findViewById(R.id.name_tv)
        artist = findViewById(R.id.artist_tv)
        currentTimeTv = findViewById(R.id.time_tv)
        playBtn = findViewById(R.id.play_btn)


        track = Gson().fromJson(intent.getStringExtra(TRACK), Track::class.java)

        duration.text = dateFormat.format(track.trackTimeMillis)
        album.text = track.collectionName
        year.text = track.releaseDate
        genre.text = track.primaryGenreName
        country.text = track.country
        name.text = track.trackName
        artist.text = track.artistName

        Glide.with(poster.context)
            .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .placeholder(R.drawable.ic_poster_placeholder_312)
            .centerCrop()
            .transform(RoundedCorners(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 8f, poster.context.resources.displayMetrics
                    ).toInt()
                ))
            .into(poster)

        playBtn.setOnClickListener {
            playBackControl()

        }
        preparePlayer()
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            mediaPlayerState = STATE_PREPARED
            playBtn.isClickable = true
        }
        mediaPlayer.setOnCompletionListener {
            mediaPlayerState = STATE_PREPARED
            handler.removeCallbacks(timeUpdater)
            playBtn.setImageResource(R.drawable.ic_play_btn_100)
            currentTimeTv.text = getString(R.string.time_placeholder)
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
        playBtn.setImageResource(R.drawable.ic_pause_btn_100)
        handler.post(timeUpdater)
    }

    private fun pausePlaying() {
        mediaPlayer.pause()
        mediaPlayerState = STATE_PAUSED
        playBtn.setImageResource(R.drawable.ic_play_btn_100)
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
                currentTimeTv.text = dateFormat.format(mediaPlayer.currentPosition)
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