package com.example.playlistmaker.ui.media.view_model

import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Handler
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.ui.media.PlayerState

class MediaViewModel(
    private val url: String,
    private val mediaPlayer: MediaPlayer,
    private val handler: Handler,
    private val dateFormat: SimpleDateFormat): ViewModel() {

    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.Default("00:00"))
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    private var timeUpdater: Runnable

    init {
        timeUpdater = updateDurationTv()
        preparePlayer()
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerStateLiveData.postValue(PlayerState.Prepared("00:00"))
        }
        mediaPlayer.setOnCompletionListener {
            playerStateLiveData.postValue(PlayerState.Prepared("00:00"))
            handler.removeCallbacks(timeUpdater)
        }
    }

    private fun startPlaying() {
        mediaPlayer.start()
        playerStateLiveData.postValue(PlayerState.Playing(playerStateLiveData.value?.progress ?: "00:00"))
        handler.post(timeUpdater)
    }

    fun pausePlaying() {
        mediaPlayer.pause()
        playerStateLiveData.postValue(PlayerState.Paused(playerStateLiveData.value?.progress ?: "00:00"))
        handler.removeCallbacks(timeUpdater)
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

    private fun updateDurationTv(): Runnable {
        return object : Runnable {
            override fun run() {
                val progress = dateFormat.format(mediaPlayer.currentPosition)
                when (val cur = playerStateLiveData.value) {
                    is PlayerState.Playing -> playerStateLiveData.postValue(cur.copy(progress))
                    is PlayerState.Paused -> playerStateLiveData.postValue(cur.copy(progress))
                    else -> {}
                }
                handler.postDelayed(this, DELAY_HALF_SECOND)
            }
        }
    }

    companion object {
        const val DELAY_HALF_SECOND = 500L
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
        handler.removeCallbacks(timeUpdater)
    }
}