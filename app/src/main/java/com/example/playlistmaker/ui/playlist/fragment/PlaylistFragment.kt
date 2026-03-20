package com.example.playlistmaker.ui.playlist.fragment

import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.signature.ObjectKey
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.domain.media.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.player.activity.PlayerFragment
import com.example.playlistmaker.ui.playlist.view_model.PlaylistViewModel
import com.example.playlistmaker.ui.search.TrackAdapter
import com.example.playlistmaker.ui.search.activity.SearchFragment.Companion.ITEM_CLICK_DEBOUNCE
import com.example.playlistmaker.util.debounce
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel : PlaylistViewModel by viewModel()
    private lateinit var playlist: Playlist
    private val gson = Gson()

    private lateinit var itemClickDebounce: (Track) -> Unit
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var playlistBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playlist = Gson().fromJson(requireArguments().getString(PLAYLIST), Playlist::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getTracksInPlaylist(playlist)
        updatePlaylistUI()

        itemClickDebounce = debounce<Track>(ITEM_CLICK_DEBOUNCE, viewLifecycleOwner.lifecycleScope, false) { track ->
            onItemClick(track)
        }

        viewModel.observeTracksinPlaylist().observe(viewLifecycleOwner) {
            trackAdapter.tracks = it.toMutableList()
            if (trackAdapter.tracks.isNotEmpty()) {
                binding.playlistRecycler.visibility = View.VISIBLE
                binding.emptyRecyclerTv.visibility = View.GONE
                trackAdapter.notifyDataSetChanged()
            } else {
                binding.emptyRecyclerTv.visibility = View.VISIBLE
                binding.playlistRecycler.visibility = View.GONE
            }
            binding.durationTv.text = "${viewModel.getPlaylistDuration()} minutes"
            binding.tracksAmountTv.text = "${it.size}" + " tracks"
        }

        binding.backBtn.setOnClickListener {
            goBack()
        }

        binding.shareBtn.setOnClickListener {
            sharePlaylist()
        }

        binding.shareTv.setOnClickListener {
            sharePlaylist()
        }

        binding.deletePlaylistTv.setOnClickListener {
            showDeletePlaylistDialog(playlist)
        }

        binding.editPlaylistTv.setOnClickListener {
            findNavController().navigate(R.id.action_playlistFragment_to_editPlaylistFragment, EditPlaylistFragment.createArgs(gson.toJson(playlist)))
        }

        binding.menuBtn.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        playlistBottomSheetBehavior = BottomSheetBehavior.from(binding.playlistBottomSheet).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
        }

        menuBottomSheetBehavior = BottomSheetBehavior.from(binding.menuBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        playlistBottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when( newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        print("")
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        print("")
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        print("")
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                        print("")
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                        print("")
                    }
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                        print("")
                    }

                }
                viewModel.getTracksInPlaylist(playlist)
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })

        menuBottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }
                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                        playlistBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = slideOffset + 1f
            }
        })

        trackAdapter = TrackAdapter ({track -> itemClickDebounce(track)}, {track -> onLongItemClick(track, playlist) })

        binding.playlistRecycler.adapter = trackAdapter
        binding.playlistRecycler.layoutManager = LinearLayoutManager(requireContext())

        setFragmentResultListener("edit_playlist_request") { requestKey, bundle ->
            playlist = gson.fromJson(bundle.getString("updated_playlist"), Playlist::class.java)
            updatePlaylistUI()
        }

    }

    private fun updatePlaylistUI() {
        // Обновляем все поля, которые показывают информацию о плейлисте
        setImage()
        setMiniImage()
        binding.playlistNameTv.text = playlist.name
        binding.nameTv.text = playlist.name
        binding.descriptionTv.text = playlist.description
        binding.tracksAmountTv.text = "${playlist.playlistSize}" + " tracks"
        binding.playlistSizeTv.text = "${playlist.playlistSize}" + " tracks"
    }

    private fun sharePlaylist() {
        if (playlist.playlistSize == 0) {
            Toast.makeText(requireContext(),
                getString(R.string.there_is_no_track_list_to_share_in_this_playlist), Toast.LENGTH_SHORT).show()
        } else {
            viewModel.sharePlaylist(requireContext(), playlist)
        }
    }

    private fun onItemClick(track: Track) {
        findNavController().navigate(
            R.id.action_playlistFragment_to_playerFragment, PlayerFragment.createArgs(
                Gson().toJson(track)
            )
        )
    }

    private fun goBack() {
        findNavController().navigateUp()
    }

    private fun onLongItemClick(track: Track, playlist: Playlist) {
        showDeleteTrackDialog(track, playlist)
    }

    private fun deleteTrackFromPlaylist(track: Track, playlist: Playlist) {
        viewModel.deleteTrackFromPlaylist(track, playlist)
        Toast.makeText(requireContext(),
            getString(R.string.track_successfully_deleted_from_playlist), Toast.LENGTH_SHORT).show()
    }

    private fun showDeleteTrackDialog(track: Track, playlist: Playlist) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Do you want to delete a track?")
            .setNegativeButton("No") { dialog, which ->
            }
            .setPositiveButton("Yes") { dialog, which ->
                deleteTrackFromPlaylist(track, playlist)
            }
            .show()
    }

    private fun showDeletePlaylistDialog(playlist: Playlist) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Do you want to delete ${playlist.name} playlist?")
            .setNegativeButton("No") { dialog, which ->
            }
            .setPositiveButton("Yes") { dialog, which ->
                viewModel.deletePlaylist(playlist)
                goBack()
            }
            .show()
    }

    private fun setImage() {
        Glide.with(binding.playlistPoster.context)
            .load(Uri.parse(playlist.posterUri))
            .placeholder(R.drawable.ic_track_placeholder)
            .signature(ObjectKey(System.currentTimeMillis()))
            .transform(
                RoundedCorners(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 8f, binding.playlistPoster.context.resources.displayMetrics
                    ).toInt()
                )
            )
            .into(binding.playlistPoster)
    }

    private fun setMiniImage() {
        Glide.with(binding.miniPosterImg.context)
            .load(Uri.parse(playlist.posterUri))
            .placeholder(R.drawable.ic_track_placeholder)
            .signature(ObjectKey(System.currentTimeMillis()))
            .into(binding.miniPosterImg)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val PLAYLIST = "playlist"

        @JvmStatic
        fun createArgs(playlist: String) =
            bundleOf(PLAYLIST to playlist)
    }
}
