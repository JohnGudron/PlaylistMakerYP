package com.example.playlistmaker.ui.player.activity

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observePlayerState().observe(viewLifecycleOwner) {
            binding.playBtn.setImageResource(it.buttonImage)
            binding.playBtn.isClickable = (it.isButtonPlayEnabled)
            binding.timeTv.text = it.progress
        }


        binding.durationContentTv.text = track.trackDuration
        binding.albumContentTv.text = track.collectionName
        binding.yearContentTv.text = track.releaseDate
        binding.genreContentTv.text = track.primaryGenreName
        binding.countryContentTv.text = track.country
        binding.nameTv.text = track.trackName
        binding.artistTv.text = track.artistName

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