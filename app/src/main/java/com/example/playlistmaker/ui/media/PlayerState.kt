package com.example.playlistmaker.ui.media

sealed class PlayerState(open val progress: String) {
    data class Default(override val progress: String) : PlayerState(progress)
    data class Prepared(override val progress: String) : PlayerState(progress)
    data class Playing(override val progress: String) : PlayerState(progress)
    data class Paused(override val progress: String) : PlayerState(progress)
}