package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.data.db.entity.TrackEntity
@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Delete
    suspend fun deletePlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlists_table ORDER BY rowid ASC")
    suspend fun getAllPlaylists(): List<PlaylistEntity>

    @Query("SELECT id FROM playlists_table")
    suspend fun getPlaylistsIds(): List<Long>

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)

}