package com.example.playlistmaker

import android.icu.text.SimpleDateFormat
import android.os.Bundle
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        val backBtn = findViewById<ImageView>(R.id.back_btn)
        backBtn.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        poster = findViewById(R.id.song_poster)
        duration = findViewById(R.id.duration_content_tv)
        album = findViewById(R.id.album_content_tv)
        year = findViewById(R.id.year_content_tv)
        genre = findViewById(R.id.genre_content_tv)
        country = findViewById(R.id.country_content_tv)
        name = findViewById(R.id.name_tv)
        artist = findViewById(R.id.artist_tv)
        currentTimeTv = findViewById(R.id.time_tv)


        track = Gson().fromJson(intent.getStringExtra(TRACK), Track::class.java)

        duration.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
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


    }
}