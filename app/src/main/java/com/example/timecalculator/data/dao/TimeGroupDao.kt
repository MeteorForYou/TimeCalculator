package com.example.timecalculator.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.timecalculator.data.entity.TimeGroup
import kotlinx.coroutines.flow.Flow

@Dao
interface TimeGroupDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(group: TimeGroup): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(groups: List<TimeGroup>): List<Long>

    @Update
    suspend fun update(group: TimeGroup)

    @Delete
    suspend fun delete(group: TimeGroup)

    @Query("DELETE FROM time_group WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM time_group WHERE calculationId = :calculationId")
    suspend fun deleteByCalculationId(calculationId: Long)

    @Query("SELECT * FROM time_group WHERE calculationId = :calculationId ORDER BY orderIndex ASC")
    fun getGroupsByCalculationId(calculationId: Long): Flow<List<TimeGroup>>

    @Query("SELECT * FROM time_group WHERE calculationId = :calculationId ORDER BY orderIndex ASC")
    suspend fun getGroupsByCalculationIdSync(calculationId: Long): List<TimeGroup>

    @Query("SELECT * FROM time_group WHERE id = :id")
    suspend fun getGroupById(id: Long): TimeGroup?

    @Query("SELECT MAX(orderIndex) FROM time_group WHERE calculationId = :calculationId")
    suspend fun getMaxOrderIndex(calculationId: Long): Int?
}

