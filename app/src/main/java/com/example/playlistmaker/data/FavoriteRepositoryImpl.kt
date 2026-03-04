package com.example.playlistmaker.data

import com.example.playlistmaker.data.converters.TrackDbConvertor
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.domain.db.FavoriteRepository
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteRepositoryImpl (
    private val appDatabase: AppDatabase, private val trackDbConvertor: TrackDbConvertor
): FavoriteRepository {

    override suspend fun insertFavorite(track: Track) {
        appDatabase.trackDao().insertTrack(trackDbConvertor.map(track))
    }

    override suspend fun deleteFavorite(track: Track) {
        appDatabase.trackDao().deleteTrack(trackDbConvertor.map(track))
    }

    override fun getFavorites(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getAllTracks()
        emit(convertFromTrackEntity(tracks))
    }

    override fun getFavoritesIds(): Flow<List<Long>> = flow {
        emit(appDatabase.trackDao().getTracksIds())
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track)}
    }
}