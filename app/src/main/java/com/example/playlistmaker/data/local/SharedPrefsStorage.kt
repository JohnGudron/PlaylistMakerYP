package com.example.playlistmaker.data.local

import android.content.SharedPreferences
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson

const val HISTORY = "history"

class SharedPrefsStorage (private val sharedPrefs: SharedPreferences): LocalHistoryStorage {

    private val history = ArrayDeque<Track>()
    init {
        history.addAll(readFromPrefs())
    }

    override fun getHistory(): List<Track> = history

    override fun clearHistory() {
        sharedPrefs.edit().remove(HISTORY).apply()
        history.clear()
    }

    override fun addTrackToHistory(track: Track) {
        if (track in history) {
            history.remove(track)
            history.addFirst(track)
        } else {
            if (history.size<10) {
                history.addFirst(track)
            } else {
                history.addFirst(track)
                history.removeLast()
            }
        }
        writeToPrefs(history.toTypedArray())
    }

    fun writeToPrefs(tracks: Array<Track>) {
        val json = Gson().toJson(tracks)
        sharedPrefs.edit()
            .putString(HISTORY, json)
            .apply()
    }

    fun readFromPrefs(): Array<Track> {
        val json = sharedPrefs.getString(HISTORY, null) ?: return emptyArray()
        return Gson().fromJson(json, Array<Track>::class.java)
    }
}