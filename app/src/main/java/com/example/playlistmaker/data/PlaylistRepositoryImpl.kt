package com.example.playlistmaker.data

import com.example.playlistmaker.data.converters.PlaylistDbConvertor
import com.example.playlistmaker.data.db.dao.PlaylistDao
import com.example.playlistmaker.data.db.dao.PlaylistTrackDao
import com.example.playlistmaker.domain.db.PlaylistRepository
import com.example.playlistmaker.domain.media.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val playlistDbConvertor: PlaylistDbConvertor,
    private val playlistTrackDao: PlaylistTrackDao
): PlaylistRepository {

    override suspend fun insertPlaylist(playlist: Playlist) {
        playlistDao.insertPlaylist(playlistDbConvertor.convertToPlaylistEntity(playlist))
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistDao.deletePlaylist(playlistDbConvertor.convertToPlaylistEntity(playlist))
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = playlistDao.getAllPlaylists()
        emit(playlists.map { playlistDbConvertor.convertToPlaylist(it) })
    }

    override fun getPlaylistsIds(): Flow<List<Long>> = flow {
        emit(playlistDao.getPlaylistsIds())
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistDao.updatePlaylist(playlistDbConvertor.convertToPlaylistEntity(playlist))
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        val ids = playlist.tracksIds + track.trackId
        playlistTrackDao.insertTrack(playlistDbConvertor.convertToPlaylistTrackEntity(track))
        playlistDao.updatePlaylist(playlistDbConvertor.convertToPlaylistEntity(playlist.copy(tracksIds = ids, playlistSize = ids.size)))
    }
}