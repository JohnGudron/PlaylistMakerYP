package com.example.playlistmaker.ui.media.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.domain.media.model.Playlist
import com.example.playlistmaker.ui.media.view_model.PlaylistAdapter
import com.example.playlistmaker.ui.media.view_model.PlaylistsViewModel
import com.example.playlistmaker.ui.playlist.fragment.PlaylistFragment
import com.example.playlistmaker.ui.search.activity.SearchFragment.Companion.ITEM_CLICK_DEBOUNCE
import com.example.playlistmaker.util.debounce
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistSFragment : Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!
    private val viewModel : PlaylistsViewModel by viewModel()
    private val gson = Gson()

    private lateinit var itemClickDebounce: (Playlist) -> Unit
    private lateinit var playlistAdapter: PlaylistAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getPlaylists()

        viewModel.observePlaylists().observe(viewLifecycleOwner) {
            renderScreen(it)
        }

        itemClickDebounce = debounce<Playlist>(ITEM_CLICK_DEBOUNCE, viewLifecycleOwner.lifecycleScope, false) { playlist ->
            onItemClick(playlist)
        }

        playlistAdapter = PlaylistAdapter { playlist -> itemClickDebounce(playlist) }

        binding.recyclerView.adapter = playlistAdapter
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        binding.newPlaylistBtn.setOnClickListener {
            findNavController().navigate(
                R.id.action_mediaFragment_to_editPlaylistFragment
            )
        }
    }

    private fun renderScreen(playlists: List<Playlist>) {
        if (playlists.isEmpty()) showEmptyScreen() else showPlaylists(playlists)
    }

    private fun showPlaylists(playlists: List<Playlist>) {
        playlistAdapter.playlists = playlists.toMutableList()
        playlistAdapter.notifyDataSetChanged()
        binding.placeholderImg.visibility = View.GONE
        binding.placeholderTv.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
    }

    private fun showEmptyScreen() {
        binding.placeholderImg.visibility = View.VISIBLE
        binding.placeholderTv.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
    }

    private fun onItemClick(playlist: Playlist) {
        findNavController().navigate(
            R.id.action_mediaFragment_to_playlistFragment, PlaylistFragment.createArgs(gson.toJson(playlist))
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            PlaylistSFragment()
    }
}