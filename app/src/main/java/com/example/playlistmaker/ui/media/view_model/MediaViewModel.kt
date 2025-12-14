package com.example.playlistmaker.ui.media.view_model

import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import java.util.Locale

class MediaViewModel(private val url: String): ViewModel() {

    private val playerStateLiveData = MutableLiveData(STATE_DEFAULT)
    fun observePlayerState(): LiveData<Int> = playerStateLiveData

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
            playerStateLiveData.postValue(STATE_PREPARED)
        }
        mediaPlayer.setOnCompletionListener {
            playerStateLiveData.postValue(STATE_PREPARED)
            handler.removeCallbacks(timeUpdater)
        }
    }

    private fun startPlaying() {
        mediaPlayer.start()
        playerStateLiveData.postValue(STATE_PLAYING)
        handler.post(timeUpdater)
    }

    fun pausePlaying() {
        mediaPlayer.pause()
        playerStateLiveData.postValue(STATE_PAUSED)
        handler.removeCallbacks(timeUpdater)
    }

    fun playBackControl() {
        when(playerStateLiveData.value) {
            STATE_PREPARED, STATE_PAUSED -> {
                startPlaying()
            }
            STATE_PLAYING -> {
                pausePlaying()
            }
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

        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
        const val DELAY_HALF_SECOND = 500L
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
        handler.removeCallbacks(timeUpdater)
    }
}