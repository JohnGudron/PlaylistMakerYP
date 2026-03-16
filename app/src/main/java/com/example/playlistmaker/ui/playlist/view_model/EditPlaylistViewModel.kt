package com.example.playlistmaker.ui.playlist.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.PlaylistInteractor
import com.example.playlistmaker.domain.media.model.Playlist
import kotlinx.coroutines.launch

class EditPlaylistViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    fun updatePlaylistInfo(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.updatePlaylist(playlist)
        }
    }

    fun saveNewPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.insertPlaylist(playlist)
        }
    }

}