package com.example.playlistmaker.ui.media.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.FavoriteInteractor
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.launch

class FavouriteTracksViewModel(private val favoriteInteractor: FavoriteInteractor): ViewModel() {

    private val favoriteLiveData = MutableLiveData<List<Track>>()
    fun observeState(): LiveData<List<Track>> = favoriteLiveData

    fun getFavorites() {
        viewModelScope.launch {
            favoriteInteractor.getFavorites().collect { tracks ->
                favoriteLiveData.postValue(tracks)
            }
        }
    }
}