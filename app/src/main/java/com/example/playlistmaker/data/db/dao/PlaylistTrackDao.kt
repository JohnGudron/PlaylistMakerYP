package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.PlaylistTrackEntity

@Dao
interface PlaylistTrackDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: PlaylistTrackEntity)

    @Query("SELECT * FROM track_in_playlist_table")
    suspend fun getAllTracks(): List<PlaylistTrackEntity>

    @Delete
    suspend fun deleteTrack(track: PlaylistTrackEntity)

    @Query("DELETE FROM track_in_playlist_table WHERE id = :trackId")
    suspend fun deleteTrackById(trackId: Long)
}