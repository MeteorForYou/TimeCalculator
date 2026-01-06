package com.example.timecalculator.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.timecalculator.data.entity.ReadingSession
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: ReadingSession): Long

    @Update
    suspend fun update(session: ReadingSession)

    @Delete
    suspend fun delete(session: ReadingSession)

    @Query("DELETE FROM reading_session WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM reading_session")
    suspend fun deleteAll()

    @Query("SELECT * FROM reading_session ORDER BY createdAt DESC")
    fun getAllSessions(): Flow<List<ReadingSession>>

    @Query("SELECT * FROM reading_session ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentSessions(limit: Int): Flow<List<ReadingSession>>

    @Query("SELECT * FROM reading_session WHERE id = :id")
    suspend fun getSessionById(id: Long): ReadingSession?

    @Query("SELECT * FROM reading_session WHERE id = :id")
    fun getSessionByIdFlow(id: Long): Flow<ReadingSession?>

    @Query("SELECT COUNT(*) FROM reading_session")
    fun getSessionCount(): Flow<Int>
}

