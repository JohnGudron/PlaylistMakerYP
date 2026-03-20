package com.example.playlistmaker.domain.media.impl

import com.example.playlistmaker.domain.db.PlaylistInteractor
import com.example.playlistmaker.domain.db.PlaylistRepository
import com.example.playlistmaker.domain.media.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository): PlaylistInteractor {

    override suspend fun insertPlaylist(playlist: Playlist) {
        playlistRepository.insertPlaylist(playlist)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistRepository.deletePlaylist(playlist)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getAllPlaylists()
    }

    override fun getPlaylistsIds(): Flow<List<Long>> {
        return playlistRepository.getPlaylistsIds()
    }

    override fun getPlaylistTracks(playlist: Playlist): Flow<List<Track>> {
        return playlistRepository.getPlaylistTracks(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistRepository.updatePlaylist(playlist)
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        playlistRepository.addTrackToPlaylist(track, playlist)
    }

    override suspend fun deleteTrackFromPlaylist(track: Track, playlist: Playlist) {
        playlistRepository.deleteTrackFromPlaylist(track, playlist)
    }
}