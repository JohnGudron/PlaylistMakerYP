package com.example.playlistmaker.ui.search

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track

class TrackAdapter(private val onItemClick: (Track) -> Unit) : RecyclerView.Adapter<TrackViewHolder>() {

    var tracks = mutableListOf<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false))
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            onItemClick(tracks[position])
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }
}

class TrackViewHolder(view:View): RecyclerView.ViewHolder(view) {
    private val trackImg = view.findViewById<ImageView>(R.id.track_img)
    private val trackName = view.findViewById<TextView>(R.id.track_name)
    private val trackArtist = view.findViewById<TextView>(R.id.track_artist)
    private val trackDuration = view.findViewById<TextView>(R.id.track_duration)

    fun bind(track: Track) {
        Glide.with(trackImg.context)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.ic_track_placeholder)
            .centerCrop()
            .transform(RoundedCorners(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 8f, trackImg.context.resources.displayMetrics
                ).toInt()
            ))
            .into(trackImg)
        trackName.text = track.trackName
        trackArtist.text = track.artistName
        trackDuration.text = track.trackDuration
    }
}