package com.example.playlistmaker.ui.player.util

import com.example.playlistmaker.R

sealed class PlayerState(
    val isButtonPlayEnabled: Boolean,
    val buttonImage: Int,
    val progress: String
) {
    class Default : PlayerState(false, R.drawable.ic_play_btn_100, "00:00")
    class Prepared : PlayerState(true, R.drawable.ic_play_btn_100, "00:00")
    class Playing(progress: String) : PlayerState(true, R.drawable.ic_pause_btn_100, progress)
    class Paused(progress: String) : PlayerState(true, R.drawable.ic_play_btn_100, progress)
}