package com.example.timecalculator.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.timecalculator.data.entity.Calculation
import kotlinx.coroutines.flow.Flow

@Dao
interface CalculationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(calculation: Calculation): Long

    @Update
    suspend fun update(calculation: Calculation)

    @Delete
    suspend fun delete(calculation: Calculation)

    @Query("DELETE FROM calculation WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM calculation WHERE sessionId = :sessionId")
    suspend fun deleteBySessionId(sessionId: Long)

    @Query("SELECT * FROM calculation WHERE sessionId = :sessionId ORDER BY createdAt DESC")
    fun getCalculationsBySessionId(sessionId: Long): Flow<List<Calculation>>

    @Query("SELECT * FROM calculation WHERE sessionId = :sessionId ORDER BY createdAt DESC")
    suspend fun getCalculationsBySessionIdSync(sessionId: Long): List<Calculation>

    @Query("SELECT * FROM calculation WHERE id = :id")
    suspend fun getCalculationById(id: Long): Calculation?

    @Query("SELECT * FROM calculation ORDER BY createdAt DESC")
    fun getAllCalculations(): Flow<List<Calculation>>

    @Query("SELECT COUNT(*) FROM calculation WHERE sessionId = :sessionId")
    fun getCalculationCountBySession(sessionId: Long): Flow<Int>
}

