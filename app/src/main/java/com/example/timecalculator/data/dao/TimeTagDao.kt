package com.example.timecalculator.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.timecalculator.data.entity.TimeTag
import kotlinx.coroutines.flow.Flow

@Dao
interface TimeTagDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tag: TimeTag): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tags: List<TimeTag>): List<Long>

    @Update
    suspend fun update(tag: TimeTag)

    @Delete
    suspend fun delete(tag: TimeTag)

    @Query("DELETE FROM time_tag WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM time_tag WHERE sessionId = :sessionId")
    suspend fun deleteBySessionId(sessionId: Long)

    @Query("SELECT * FROM time_tag WHERE sessionId = :sessionId ORDER BY orderIndex ASC")
    fun getTagsBySessionId(sessionId: Long): Flow<List<TimeTag>>

    @Query("SELECT * FROM time_tag WHERE sessionId = :sessionId ORDER BY orderIndex ASC")
    suspend fun getTagsBySessionIdSync(sessionId: Long): List<TimeTag>

    @Query("SELECT * FROM time_tag WHERE id = :id")
    suspend fun getTagById(id: Long): TimeTag?

    @Query("SELECT MAX(orderIndex) FROM time_tag WHERE sessionId = :sessionId")
    suspend fun getMaxOrderIndex(sessionId: Long): Int?
}

