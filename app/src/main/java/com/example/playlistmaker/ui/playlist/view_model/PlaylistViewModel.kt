package com.example.playlistmaker.ui.playlist.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.PlaylistInteractor
import com.example.playlistmaker.domain.media.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.domain.sharing.SharingInteractor
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    private val tracksInPlaylistLiveData = MutableLiveData<List<Track>>()
    fun observeTracksinPlaylist(): LiveData<List<Track>> = tracksInPlaylistLiveData

    fun getTracksInPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.getPlaylistTracks(playlist).collect { tracks ->
                tracksInPlaylistLiveData.postValue(tracks)
            }
        }
    }

    fun getPlaylistDuration(): Int {
        val list = tracksInPlaylistLiveData.value.map {
            val parts = it.trackDuration.split(":").map { it.toInt() }
            parts[0] * 60 + parts[1]
        }
        return list.sum() / 60
    }

    fun deleteTrackFromPlaylist(track: Track, playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.deleteTrackFromPlaylist(track, playlist)
            getTracksInPlaylist(playlist)
        }
    }

    private fun makeMessage(playlist: Playlist, tracks: List<Track>): String {
        val message = StringBuilder()

        message.append(playlist.name)
        message.append("\n")
        message.append(playlist.description)
        message.append("\n")
        message.append("${tracks.size} tracks")

        if (tracks.isNotEmpty()) {
            tracks.forEachIndexed { index, track ->
                message.append("\n${index + 1}. ${track.artistName} - ${track.trackName} (${track.trackDuration})")
            }
        }

        return message.toString()
    }

    fun sharePlaylist(context: Context, playlist: Playlist) {
        sharingInteractor.sharePlaylist(context, makeMessage(playlist, tracksInPlaylistLiveData.value))
    }

    fun deletePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.deletePlaylist(playlist)
        }
    }
}