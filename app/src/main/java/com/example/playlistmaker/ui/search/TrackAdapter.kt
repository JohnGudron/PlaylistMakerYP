package com.example.playlistmaker.ui.search

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemTrackBinding
import com.example.playlistmaker.domain.search.model.Track

class TrackAdapter(private val onItemClick: (Track) -> Unit) : RecyclerView.Adapter<TrackViewHolder>() {

    var tracks = mutableListOf<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder =
        TrackViewHolder(
            ItemTrackBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

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

class TrackViewHolder(private val binding: ItemTrackBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(track: Track) {
        Glide.with(binding.trackImg.context)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.ic_track_placeholder)
            .centerCrop()
            .transform(RoundedCorners(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 8f, binding.trackImg.context.resources.displayMetrics
                ).toInt()
            ))
            .into(binding.trackImg)
        binding.trackName.text = track.trackName
        binding.trackArtist.text = track.artistName
        binding.trackDuration.text = track.trackDuration
    }
}