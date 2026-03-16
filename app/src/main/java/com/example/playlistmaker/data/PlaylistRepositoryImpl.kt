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

        val tracksInPlaylistIds = playlistDao.getAllPlaylists().map {
            playlistDbConvertor.convertToPlaylist(it).tracksIds }
        // проверяем есть ли треки из удаляемого плейлиста в других плейлистах
        playlist.tracksIds.forEach { trackId ->
            var counter = 0
            tracksInPlaylistIds.forEach { tracksInPlaylistIds ->
                if (trackId in tracksInPlaylistIds) counter++
            }
            // если счетчик меньше либо равен 1, значит трек есть только в этом плейлисте и его можно удалять из БД
            if (counter<=1) playlistTrackDao.deleteTrackById(trackId)
        }
        playlistDao.deletePlaylist(playlistDbConvertor.convertToPlaylistEntity(playlist))
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = playlistDao.getAllPlaylists()
        emit(playlists.map { playlistDbConvertor.convertToPlaylist(it) })
    }

    override fun getPlaylistsIds(): Flow<List<Long>> = flow {
        emit(playlistDao.getPlaylistsIds())
    }

    override fun getPlaylistTracks(playlist: Playlist): Flow<List<Track>> = flow {
        val tracks = playlistTrackDao.getAllTracks().filter { it.id in playlist.tracksIds }.map { playlistDbConvertor.convertToTrack(it) }
        emit(tracks)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistDao.updatePlaylist(playlistDbConvertor.convertToPlaylistEntity(playlist))
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        val ids = playlist.tracksIds + track.trackId
        playlistTrackDao.insertTrack(playlistDbConvertor.convertToPlaylistTrackEntity(track))
        playlistDao.updatePlaylist(playlistDbConvertor.convertToPlaylistEntity(playlist.copy(tracksIds = ids, playlistSize = ids.size)))
    }

    override suspend fun deleteTrackFromPlaylist(track: Track, playlist: Playlist) {
        val updatedPlaylist =
            playlist.copy(
                tracksIds = playlist.tracksIds.filter { it != track.trackId },
                playlistSize = playlist.playlistSize - 1)
        val tracksInPlaylistList = playlistDao.getAllPlaylists().map {
            playlistDbConvertor.convertStringToListOfLong(it.tracksIds)
        }.filter { track.trackId in it }

        if (tracksInPlaylistList.size <= 1) { // размер должен быть минимум 1, т.к. еще не обновили плейлист, в котором был трек
            playlistTrackDao.deleteTrack(playlistDbConvertor.convertToPlaylistTrackEntity(track))
        }
        playlistDao.updatePlaylist(playlistDbConvertor.convertToPlaylistEntity(updatedPlaylist))
    }
}