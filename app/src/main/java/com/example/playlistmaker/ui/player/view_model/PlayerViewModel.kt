package com.example.playlistmaker.ui.player.view_model

import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.ui.player.util.PlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class  PlayerViewModel(
    private val url: String,
    private val mediaPlayer: MediaPlayer,
    private val dateFormat: SimpleDateFormat): ViewModel() {

    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.Default())
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    private var timeUpdaterJob: Job? = null

    init {
        preparePlayer()
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerStateLiveData.postValue(PlayerState.Prepared())
        }
        mediaPlayer.setOnCompletionListener {
            playerStateLiveData.postValue(PlayerState.Prepared())
            timeUpdaterJob?.cancel()
        }
    }

    private fun startPlaying() {
        mediaPlayer.start()
        playerStateLiveData.postValue(PlayerState.Playing(getCurrentPlayerPosition()))
        startTimer()
    }

    fun pausePlaying() {
        mediaPlayer.pause()
        timeUpdaterJob?.cancel()
        playerStateLiveData.postValue(PlayerState.Paused(getCurrentPlayerPosition()
        ))
    }

    fun playBackControl() {
        when(playerStateLiveData.value) {
            is PlayerState.Prepared, is PlayerState.Paused -> {
                startPlaying()
            }
            is PlayerState.Playing -> {
                pausePlaying()
            }
            else -> {
                Log.d("PlayerState", "playBackControl: trying play/pause in default state")}
        }
    }

    private fun startTimer() {
        timeUpdaterJob = viewModelScope.launch {
            while (mediaPlayer.isPlaying) {
                delay(DELAY)
                playerStateLiveData.postValue(PlayerState.Playing(getCurrentPlayerPosition()))
            }
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return dateFormat.format(mediaPlayer.currentPosition)
    }

    companion object {
        const val DELAY = 300L
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
    }
}