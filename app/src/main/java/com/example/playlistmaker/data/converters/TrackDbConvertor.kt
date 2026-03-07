package com.example.playlistmaker.data.converters

import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.domain.search.model.Track

class TrackDbConvertor {

    fun map(track: Track): TrackEntity =
        TrackEntity(
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

    fun map(track: TrackEntity): Track =
        Track(
            track.trackName,
            track.artistName,
            track.trackDuration,
            track.artworkUrl100,
            track.id,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            isFavorite = true
        )
}