package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.domain.db.FavoriteInteractor
import com.example.playlistmaker.domain.db.FavoriteRepository
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

class FavoriteInteractorImpl(private val favoriteRepository: FavoriteRepository): FavoriteInteractor {
    override suspend fun insertFavorite(track: Track) {
        favoriteRepository.insertFavorite(track)
    }

    override suspend fun deleteFavorite(track: Track) {
        favoriteRepository.deleteFavorite(track)
    }

    override fun getFavorites(): Flow<List<Track>> {
        return favoriteRepository.getFavorites()
    }

    override suspend fun getFavoriteIds(): Flow<List<Long>> {
        return favoriteRepository.getFavoritesIds()
    }
}