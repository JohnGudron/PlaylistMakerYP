package com.example.playlistmaker.ui.media.view_model

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemPlaylistBinding
import com.example.playlistmaker.domain.media.model.Playlist

class PlaylistAdapter(private val onItemClick: (Playlist) -> Unit) : RecyclerView.Adapter<PlaylistViewHolder>() {

    var playlists = mutableListOf<Playlist>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder =
        PlaylistViewHolder(
            ItemPlaylistBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener {
            onItemClick(playlists[position])
        }
    }

    override fun getItemCount(): Int {
        return playlists.size
    }
}

class PlaylistViewHolder(private val binding: ItemPlaylistBinding, /*private val gson: Gson*/): RecyclerView.ViewHolder(binding.root) {

    fun bind(playlist: Playlist) {
        Glide.with(binding.posterImg.context)
            .load(Uri.parse(playlist.posterUri))
            .placeholder(R.drawable.ic_track_placeholder)
            .into(binding.posterImg)
        binding.playlistNameTv.text = playlist.name
        binding.playlistSizeTv.text = "${playlist.playlistSize}" + " tracks"
    }
}