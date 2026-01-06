package com.example.timecalculator.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 时间标签 - 从屏幕读取或手动添加的时间
 */
@Entity(
    tableName = "time_tag",
    foreignKeys = [
        ForeignKey(
            entity = ReadingSession::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sessionId")]
)
data class TimeTag(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // 所属读取会话ID
    val sessionId: Long,

    // 时间值（毫秒时间戳，当天的时间点）
    val timeMillis: Long,

    // 显示文本（如 "09:30" 或 "2024-01-01 09:30"）
    val displayText: String,

    // 是否为手动添加
    val isManualAdded: Boolean = false,

    // 排序索引
    val orderIndex: Int = 0,

    // 创建时间
    val createdAt: Long = System.currentTimeMillis()
)

