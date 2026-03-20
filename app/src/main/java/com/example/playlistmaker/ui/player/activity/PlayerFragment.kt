package com.example.playlistmaker.ui.player.activity

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.domain.media.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.media.view_model.BtmSheetPlaylistAdapter
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import com.example.playlistmaker.ui.search.activity.SearchFragment.Companion.ITEM_CLICK_DEBOUNCE
import com.example.playlistmaker.util.debounce
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private val track by lazy {
        Gson().fromJson(requireArguments().getString(TRACK), Track::class.java)
    }
    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(track.previewUrl)
    }

    private lateinit var itemClickDebounce: (Playlist) -> Unit
    private lateinit var btmSheetPlaylistAdapter: BtmSheetPlaylistAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        itemClickDebounce = debounce<Playlist>(ITEM_CLICK_DEBOUNCE, viewLifecycleOwner.lifecycleScope, false) { playlist ->
            onItemClick(track, playlist )
        }

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                viewModel.getPlaylists()

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }
                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = slideOffset + 1f
            }
        })

        btmSheetPlaylistAdapter = BtmSheetPlaylistAdapter { playlist -> itemClickDebounce(playlist) }

        binding.playlistRecycler.adapter = btmSheetPlaylistAdapter
        binding.playlistRecycler.layoutManager = LinearLayoutManager(requireContext())

        viewModel.checkFavorite(track)

        viewModel.getPlaylists()

        viewModel.observePlaylists().observe(viewLifecycleOwner) {
            btmSheetPlaylistAdapter.playlists = it.toMutableList()
            btmSheetPlaylistAdapter.notifyDataSetChanged()
        }

        viewModel.observePlayerState().observe(viewLifecycleOwner) {
            binding.playBtn.setImageResource(it.buttonImage)
            binding.playBtn.isClickable = (it.isButtonPlayEnabled)
            binding.timeTv.text = it.progress
        }

        viewModel.observeIsFavorite().observe(viewLifecycleOwner) {
            track.isFavorite = it
            setFavoriteImage()
        }

        viewModel.observeIsTrackAdded().observe(viewLifecycleOwner) {
            Toast.makeText(
                requireContext(),
                if (it) "Track added to playlist!" else "Track already added to playlist",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.durationContentTv.text = track.trackDuration
        binding.albumContentTv.text = track.collectionName
        binding.yearContentTv.text = track.releaseDate
        binding.genreContentTv.text = track.primaryGenreName
        binding.countryContentTv.text = track.country
        binding.nameTv.text = track.trackName
        binding.artistTv.text = track.artistName
        setFavoriteImage()

        Glide.with(binding.songPoster.context)
            .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .placeholder(R.drawable.ic_poster_placeholder_312)
            .centerCrop()
            .transform(
                RoundedCorners(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 8f, binding.songPoster.context.resources.displayMetrics
                    ).toInt()
                )
            )
            .into(binding.songPoster)

        binding.playBtn.setOnClickListener {
            viewModel.playBackControl()
        }

        binding.favoriteBtn.setOnClickListener {
            track.isFavorite = viewModel.handleFavorite(track)
            setFavoriteImage()
        }

        binding.playlistBtn.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.newPlaylistBtn.setOnClickListener {
            findNavController().navigate(R.id.action_playerFragment_to_editPlaylistFragment)
        }

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setFavoriteImage() {
        binding.favoriteBtn.setImageResource(
            if (track.isFavorite) R.drawable.ic_favorite_btn_true_51
            else R.drawable.ic_favorite_btn_false_51
        )
    }

    private fun onItemClick(track: Track, playlist: Playlist) {
        viewModel.addTrackToPlaylist(track, playlist)
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlaying()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        const val TRACK = "track"

        @JvmStatic
        fun createArgs(track: String) =
                bundleOf(TRACK to track)
    }
}