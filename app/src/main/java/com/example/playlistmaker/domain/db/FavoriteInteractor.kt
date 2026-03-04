package com.example.playlistmaker.domain.db

import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteInteractor {

    suspend fun insertFavorite(track: Track)

    suspend fun deleteFavorite(track: Track)

    suspend fun getFavoriteIds(): Flow<List<Long>>

    fun getFavorites(): Flow<List<Track>>

}