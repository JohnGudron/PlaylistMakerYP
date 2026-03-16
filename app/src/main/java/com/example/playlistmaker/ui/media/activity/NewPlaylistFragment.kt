package com.example.playlistmaker.ui.media.activity

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
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.domain.media.model.Playlist
import com.example.playlistmaker.ui.media.view_model.NewPlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class NewPlaylistFragment : Fragment() {

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!

    private var posterIsEmpty = true
    private var posterUri: Uri? = null
    private val viewModel: NewPlaylistViewModel by viewModel()

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            binding.playlistImg.scaleType = ImageView.ScaleType.CENTER_CROP

            Glide.with(binding.playlistImg.context)
                .load(uri)
                .placeholder(R.drawable.ic_poster_placeholder_312)
                .into(binding.playlistImg)
            //.centerCrop()
                /*.transform(
                    RoundedCorners(
                        200)
                )*/
            //binding.playlistImg.setImageURI(uri)

            posterIsEmpty = false
            posterUri = uri
        } else {
            Toast.makeText(
                requireContext(),
                "You haven't selected any photo",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check if necessary input filled, and set button enable/disable
        binding.playlistNameInput.doAfterTextChanged { text ->
            binding.createPlaylistBtn.isEnabled = !text.isNullOrEmpty()
        }

        binding.playlistImg.setOnClickListener {
            pickPoster()
        }

        binding.backBtn.setOnClickListener {
            if (!posterIsEmpty && binding.playlistNameInput.text?.isNotEmpty() == true) {
                showExitDialog()
            } else closeCreatorScreen()
        }

        binding.createPlaylistBtn.setOnClickListener {
            val name = binding.playlistNameInput.text

            if (name.isNullOrEmpty()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.playlist_name_allert),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                createNewPlaylist()
            }
        }
    }

    private fun saveImageToPrivateStorage(uri: Uri, name: String) :Uri {
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

    private fun createNewPlaylist() {
        val name = binding.playlistNameInput.text
        val description = binding.playlistDescriptionInput.text.toString()
        val newPosterUri =
            if (!posterIsEmpty) {
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

    private fun pickPoster() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
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

    private fun closeCreatorScreen() {
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}