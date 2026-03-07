package com.example.playlistmaker.ui.media.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.media.view_model.FavouriteTracksViewModel
import com.example.playlistmaker.ui.player.activity.PlayerFragment
import com.example.playlistmaker.ui.search.TrackAdapter
import com.example.playlistmaker.ui.search.activity.SearchFragment.Companion.ITEM_CLICK_DEBOUNCE
import com.example.playlistmaker.util.debounce
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment() {

    private var _binding: FragmentFavoriteTracksBinding? = null
    private val binding get() = _binding!!
    private val viewModel : FavouriteTracksViewModel by viewModel()

    private val gson = Gson()

    private lateinit var itemClickDebounce: (Track) -> Unit
    private lateinit var adapter: TrackAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeState().observe(viewLifecycleOwner) {
            if(it.isEmpty()) showEmptyPlaceholder() else showFavoriteRecycler(it)
        }

        viewModel.getFavorites()

        itemClickDebounce = debounce<Track>(ITEM_CLICK_DEBOUNCE, viewLifecycleOwner.lifecycleScope, false) { track ->
            onItemClick(track)
        }

        adapter = TrackAdapter { track -> itemClickDebounce(track) }

        binding.favoriteRecycler.adapter = adapter
        binding.favoriteRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false )
    }

    private fun onItemClick(track: Track) {
        findNavController().navigate(
            R.id.action_mediaFragment_to_playerFragment, PlayerFragment.createArgs(
                gson.toJson(track)
            )
        )
    }

    private fun showEmptyPlaceholder() {
        binding.emptyFavoriteTv.visibility = View.VISIBLE
        binding.emptyFavoriteImg.visibility = View.VISIBLE
        binding.favoriteRecycler.visibility = View.GONE
    }

    private fun showFavoriteRecycler(favorites: List<Track>) {
        adapter.tracks = favorites.toMutableList()
        adapter.notifyDataSetChanged()
        binding.emptyFavoriteTv.visibility = View.GONE
        binding.emptyFavoriteImg.visibility = View.GONE
        binding.favoriteRecycler.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String = " ", param2: String = " ") = FavoriteTracksFragment()
    }
}