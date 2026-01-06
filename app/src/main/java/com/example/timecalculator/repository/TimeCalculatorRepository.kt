package com.example.timecalculator.repository

import com.example.timecalculator.data.dao.CalculationDao
import com.example.timecalculator.data.dao.ReadingSessionDao
import com.example.timecalculator.data.dao.TimeGroupDao
import com.example.timecalculator.data.dao.TimeTagDao
import com.example.timecalculator.data.entity.Calculation
import com.example.timecalculator.data.entity.ReadingSession
import com.example.timecalculator.data.entity.TimeGroup
import com.example.timecalculator.data.entity.TimeTag
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimeCalculatorRepository @Inject constructor(
    private val readingSessionDao: ReadingSessionDao,
    private val timeTagDao: TimeTagDao,
    private val calculationDao: CalculationDao,
    private val timeGroupDao: TimeGroupDao
) {
    // ==================== ReadingSession 操作 ====================

    fun getAllSessions(): Flow<List<ReadingSession>> = readingSessionDao.getAllSessions()

    fun getRecentSessions(limit: Int = 20): Flow<List<ReadingSession>> =
        readingSessionDao.getRecentSessions(limit)

    fun getSessionByIdFlow(id: Long): Flow<ReadingSession?> =
        readingSessionDao.getSessionByIdFlow(id)

    suspend fun getSessionById(id: Long): ReadingSession? =
        readingSessionDao.getSessionById(id)

    fun getSessionCount(): Flow<Int> = readingSessionDao.getSessionCount()

    suspend fun createSession(rawText: String, note: String? = null): Long {
        val session = ReadingSession(rawText = rawText, note = note)
        return readingSessionDao.insert(session)
    }

    suspend fun updateSession(session: ReadingSession) = readingSessionDao.update(session)

    suspend fun deleteSession(session: ReadingSession) = readingSessionDao.delete(session)

    suspend fun deleteSessionById(id: Long) = readingSessionDao.deleteById(id)

    suspend fun deleteAllSessions() = readingSessionDao.deleteAll()

    // ==================== TimeTag 操作 ====================

    fun getTagsBySessionId(sessionId: Long): Flow<List<TimeTag>> =
        timeTagDao.getTagsBySessionId(sessionId)

    suspend fun getTagsBySessionIdSync(sessionId: Long): List<TimeTag> =
        timeTagDao.getTagsBySessionIdSync(sessionId)

    suspend fun getTagById(id: Long): TimeTag? = timeTagDao.getTagById(id)

    suspend fun createTag(
        sessionId: Long,
        timeMillis: Long,
        displayText: String,
        isManualAdded: Boolean = false
    ): Long {
        val maxOrder = timeTagDao.getMaxOrderIndex(sessionId) ?: -1
        val tag = TimeTag(
            sessionId = sessionId,
            timeMillis = timeMillis,
            displayText = displayText,
            isManualAdded = isManualAdded,
            orderIndex = maxOrder + 1
        )
        return timeTagDao.insert(tag)
    }

    suspend fun createTags(tags: List<TimeTag>): List<Long> = timeTagDao.insertAll(tags)

    suspend fun updateTag(tag: TimeTag) = timeTagDao.update(tag)

    suspend fun deleteTag(tag: TimeTag) = timeTagDao.delete(tag)

    suspend fun deleteTagById(id: Long) = timeTagDao.deleteById(id)

    // ==================== Calculation 操作 ====================

    fun getCalculationsBySessionId(sessionId: Long): Flow<List<Calculation>> =
        calculationDao.getCalculationsBySessionId(sessionId)

    suspend fun getCalculationsBySessionIdSync(sessionId: Long): List<Calculation> =
        calculationDao.getCalculationsBySessionIdSync(sessionId)

    suspend fun getCalculationById(id: Long): Calculation? =
        calculationDao.getCalculationById(id)

    fun getAllCalculations(): Flow<List<Calculation>> = calculationDao.getAllCalculations()

    fun getCalculationCountBySession(sessionId: Long): Flow<Int> =
        calculationDao.getCalculationCountBySession(sessionId)

    suspend fun createCalculation(
        sessionId: Long,
        resultMinutes: Long,
        resultText: String,
        note: String? = null
    ): Long {
        val calculation = Calculation(
            sessionId = sessionId,
            resultMinutes = resultMinutes,
            resultText = resultText,
            note = note
        )
        return calculationDao.insert(calculation)
    }

    suspend fun updateCalculation(calculation: Calculation) = calculationDao.update(calculation)

    suspend fun deleteCalculation(calculation: Calculation) = calculationDao.delete(calculation)

    suspend fun deleteCalculationById(id: Long) = calculationDao.deleteById(id)

    // ==================== TimeGroup 操作 ====================

    fun getGroupsByCalculationId(calculationId: Long): Flow<List<TimeGroup>> =
        timeGroupDao.getGroupsByCalculationId(calculationId)

    suspend fun getGroupsByCalculationIdSync(calculationId: Long): List<TimeGroup> =
        timeGroupDao.getGroupsByCalculationIdSync(calculationId)

    suspend fun getGroupById(id: Long): TimeGroup? = timeGroupDao.getGroupById(id)

    suspend fun createGroup(
        calculationId: Long,
        startTimeTagId: Long?,
        endTimeTagId: Long?,
        durationMinutes: Long = 0
    ): Long {
        val maxOrder = timeGroupDao.getMaxOrderIndex(calculationId) ?: -1
        val group = TimeGroup(
            calculationId = calculationId,
            startTimeTagId = startTimeTagId,
            endTimeTagId = endTimeTagId,
            durationMinutes = durationMinutes,
            orderIndex = maxOrder + 1
        )
        return timeGroupDao.insert(group)
    }

    suspend fun createGroups(groups: List<TimeGroup>): List<Long> = timeGroupDao.insertAll(groups)

    suspend fun updateGroup(group: TimeGroup) = timeGroupDao.update(group)

    suspend fun deleteGroup(group: TimeGroup) = timeGroupDao.delete(group)

    suspend fun deleteGroupById(id: Long) = timeGroupDao.deleteById(id)

    // ==================== 复合操作 ====================

    /**
     * 创建读取会话并批量添加时间标签
     */
    suspend fun createSessionWithTags(
        rawText: String,
        timeDataList: List<Pair<Long, String>>, // Pair<timeMillis, displayText>
        note: String? = null
    ): Long {
        val sessionId = createSession(rawText, note)
        val tags = timeDataList.mapIndexed { index, (timeMillis, displayText) ->
            TimeTag(
                sessionId = sessionId,
                timeMillis = timeMillis,
                displayText = displayText,
                orderIndex = index
            )
        }
        createTags(tags)
        return sessionId
    }

    /**
     * 创建计算记录并批量添加时间组
     */
    suspend fun createCalculationWithGroups(
        sessionId: Long,
        groups: List<Triple<Long?, Long?, Long>>, // Triple<startTagId, endTagId, durationMinutes>
        resultMinutes: Long,
        resultText: String,
        note: String? = null
    ): Long {
        val calculationId = createCalculation(sessionId, resultMinutes, resultText, note)
        val timeGroups = groups.mapIndexed { index, (startId, endId, duration) ->
            TimeGroup(
                calculationId = calculationId,
                startTimeTagId = startId,
                endTimeTagId = endId,
                durationMinutes = duration,
                orderIndex = index
            )
        }
        createGroups(timeGroups)
        return calculationId
    }
}

