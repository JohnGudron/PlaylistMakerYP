package com.example.playlistmaker.data.converters

import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.data.db.entity.PlaylistTrackEntity
import com.example.playlistmaker.domain.media.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlaylistDbConvertor(private val gson: Gson) {

    fun convertToPlaylistEntity(playlist: Playlist) = PlaylistEntity (
        playlist.id,
        playlist.name,
        playlist.description,
        playlist.posterUri,
        gson.toJson(playlist.tracksIds),
        playlist.playlistSize
    )

    fun convertToPlaylist(playlistEntity: PlaylistEntity): Playlist {
        val type = object : TypeToken<List<Long>>() {}.type

        return Playlist (
            playlistEntity.id,
            playlistEntity.name,
            playlistEntity.description,
            playlistEntity.posterUri,
            gson.fromJson(playlistEntity.tracksIds, type),
            playlistEntity.playlistSize
        )
    }

    fun convertToPlaylistTrackEntity(track: Track): PlaylistTrackEntity {
        return PlaylistTrackEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackDuration,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl
        )
    }
}