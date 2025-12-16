package com.example.playlistmaker.ui.media.view_model

import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.ui.media.PlayerState
import java.util.Locale

class MediaViewModel(private val url: String): ViewModel() {

    private val playerStateLiveData = MutableLiveData(PlayerState.STATE_DEFAULT)
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    private val progressTimeLiveData = MutableLiveData("00:00")
    fun observeProgressTime(): LiveData<String> = progressTimeLiveData

    private val mediaPlayer = MediaPlayer()
    private val handler = Handler(Looper.getMainLooper())

    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private var timeUpdater: Runnable

    init {
        timeUpdater = updateDurationTv()
        preparePlayer()
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerStateLiveData.postValue(PlayerState.STATE_PREPARED)
        }
        mediaPlayer.setOnCompletionListener {
            playerStateLiveData.postValue(PlayerState.STATE_PREPARED)
            handler.removeCallbacks(timeUpdater)
        }
    }

    private fun startPlaying() {
        mediaPlayer.start()
        playerStateLiveData.postValue(PlayerState.STATE_PLAYING)
        handler.post(timeUpdater)
    }

    fun pausePlaying() {
        mediaPlayer.pause()
        playerStateLiveData.postValue(PlayerState.STATE_PAUSED)
        handler.removeCallbacks(timeUpdater)
    }

    fun playBackControl() {
        when(playerStateLiveData.value) {
            PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED -> {
                startPlaying()
            }
            PlayerState.STATE_PLAYING -> {
                pausePlaying()
            }
            else -> {
                Log.d("PlayerState", "playBackControl: trying play/pause in default state")}
        }
    }

    private fun updateDurationTv(): Runnable {
        return object : Runnable {
            override fun run() {
                progressTimeLiveData.postValue(dateFormat.format(mediaPlayer.currentPosition))
                handler.postDelayed(this, DELAY_HALF_SECOND)
            }
        }
    }

    companion object {

        fun getFactory(url: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                MediaViewModel(url)
            }
        }

        const val DELAY_HALF_SECOND = 500L
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
        handler.removeCallbacks(timeUpdater)
    }
}