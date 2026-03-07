package com.example.playlistmaker.domain.db

import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {

    suspend fun insertFavorite(track: Track)

    suspend fun deleteFavorite(track: Track)

    fun getFavorites(): Flow<List<Track>>

    fun getFavoritesIds(): Flow<List<Long>>

}