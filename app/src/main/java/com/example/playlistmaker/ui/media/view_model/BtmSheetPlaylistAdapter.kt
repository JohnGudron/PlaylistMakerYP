package com.example.playlistmaker.ui.media.view_model

import android.net.Uri
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemBottomSheetPlaylistBinding
import com.example.playlistmaker.domain.media.model.Playlist

class BtmSheetPlaylistAdapter(private val onItemClick: (Playlist) -> Unit) : RecyclerView.Adapter<BtmSheetPlaylistViewHolder>() {

    var playlists = mutableListOf<Playlist>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BtmSheetPlaylistViewHolder =
        BtmSheetPlaylistViewHolder(
            ItemBottomSheetPlaylistBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: BtmSheetPlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener {
            onItemClick(playlists[position])
        }
    }

    override fun getItemCount(): Int {
        return playlists.size
    }
}

class BtmSheetPlaylistViewHolder(private val binding: ItemBottomSheetPlaylistBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(playlist: Playlist) {
        Glide.with(binding.posterImg.context)
            .load(Uri.parse(playlist.posterUri))
            .placeholder(R.drawable.ic_track_placeholder)
            .centerCrop()
            .transform(
                RoundedCorners(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 8f, binding.posterImg.context.resources.displayMetrics
                    ).toInt()
                )
            )
            .into(binding.posterImg)
        binding.playlistNameTv.text = playlist.name
        binding.playlistSizeTv.text = "${playlist.playlistSize}" + " tracks"
    }
}