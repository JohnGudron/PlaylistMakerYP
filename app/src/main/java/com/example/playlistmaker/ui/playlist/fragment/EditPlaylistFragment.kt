package com.example.playlistmaker.ui.playlist.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentEditPlaylistBinding
import com.example.playlistmaker.domain.media.model.Playlist
import com.example.playlistmaker.ui.playlist.view_model.EditPlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class EditPlaylistFragment : Fragment() {

    private var _binding:FragmentEditPlaylistBinding? = null
    private val binding get() = _binding!!

    private val playlist by lazy {
        if (this.arguments == null) null
        else Gson().fromJson(requireArguments().getString(PlaylistFragment.PLAYLIST), Playlist::class.java)
    }

    private var isPosterChanged: Boolean = false
    private lateinit var posterUri: Uri

    private val viewModel: EditPlaylistViewModel by viewModel()
    private val gson = Gson()

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->

        if (uri != null) {
            binding.playlistImg.scaleType = ImageView.ScaleType.CENTER_CROP

            Glide.with(binding.playlistImg.context)
                .load(uri)
                .placeholder(R.drawable.ic_poster_placeholder_312)
                .into(binding.playlistImg)

            isPosterChanged = true
            posterUri = uri
        } else {
            Toast.makeText(
                requireContext(),
                "You haven't selected any photo",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUi()

        binding.playlistNameInput.doAfterTextChanged { text ->
            binding.savePlaylistBtn.isEnabled = !text.isNullOrEmpty()
        }

        binding.playlistImg.setOnClickListener {
            pickPoster()
        }

        binding.backBtn.setOnClickListener {
            if (playlist != null) {
                closeCreatorScreen()
            }
            else {
                if (binding.playlistNameInput.text?.isNotEmpty() == true && isPosterChanged) showExitDialog()
            }
        }

        binding.savePlaylistBtn.setOnClickListener {
            val name = binding.playlistNameInput.text

            if (name.isNullOrEmpty()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.playlist_name_allert),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                if (playlist == null) createNewPlaylist()
                else updatePlaylistInfo()
            }
        }
    }

    private fun saveImageToPrivateStorage(uri: Uri, name: String) : Uri {
        val filePath =
            File(
                requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "playlistPosters"
            )
        if (!filePath.exists()){
            filePath.mkdirs()
        }
        val file = File(filePath, "$name.jpg")
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        return file.toUri()
    }

    private fun updatePlaylistInfo() {
        val name = binding.playlistNameInput.text
        val description = binding.playlistDescriptionInput.text.toString()
        val newPosterUri = if (isPosterChanged) {
            saveImageToPrivateStorage(posterUri, name.toString()).toString()
        } else playlist?.posterUri

        val updatedPlaylist = playlist?.copy(
            name = name.toString(),
            description = description,
            posterUri = newPosterUri!!
        )
        viewModel.updatePlaylistInfo(updatedPlaylist!!)

        val result = Bundle().apply {
            putString("updated_playlist", gson.toJson(updatedPlaylist))
        }
        setFragmentResult("edit_playlist_request", result)

        closeCreatorScreen()
    }

    private fun createNewPlaylist() {
        val name = binding.playlistNameInput.text
        val description = binding.playlistDescriptionInput.text.toString()
        val newPosterUri =
            if (isPosterChanged) {
                saveImageToPrivateStorage(posterUri!!, name.toString()).toString()
            }
            else ""

        val playlist =
            Playlist(
                0, name.toString(),
                description,
                newPosterUri,
                emptyList()
            )
        viewModel.saveNewPlaylist(playlist)
        closeCreatorScreen()
    }

    private fun showExitDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.finish_creating_playlist))
            .setMessage(getString(R.string.all_unsaved_data_will))
            .setNeutralButton(R.string.cancel) { dialog, which ->

            }
            .setPositiveButton(R.string.complete) { dialog, which ->
                createNewPlaylist()
            }
            .show()
    }

    private fun pickPoster() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun setUi() {
        if (playlist == null) {
            binding.newPlaylistTv.text = getString(R.string.new_playlist)
            binding.savePlaylistBtn.text = getString(R.string.create)
            binding.savePlaylistBtn.isEnabled = false
            setImage()
        } else {
            binding.newPlaylistTv.text = getString(R.string.edit_playlist)
            setImage()
            binding.playlistNameInput.setText(playlist?.name ?: "")
            binding.playlistDescriptionInput.setText(playlist?.description ?: "")
        }
    }

    private fun setImage() {
        if ((playlist?.posterUri ?: "").isNotEmpty()) {
            Glide.with(binding.playlistImg.context)
                .load(Uri.parse(playlist?.posterUri))
                .placeholder(R.drawable.ic_track_placeholder)
                .into(binding.playlistImg)
        }
    }

    private fun closeCreatorScreen() {
        findNavController().navigateUp()
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