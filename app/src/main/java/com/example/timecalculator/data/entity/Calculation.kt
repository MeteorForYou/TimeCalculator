package com.example.timecalculator.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 计算记录 - 每次计算对应一条记录
 */
@Entity(
    tableName = "calculation",
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
data class Calculation(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // 所属读取会话ID
    val sessionId: Long,

    // 计算结果（总时长，分钟）
    val resultMinutes: Long,

    // 结果显示文本
    val resultText: String,

    // 创建时间
    val createdAt: Long = System.currentTimeMillis(),

    // 备注（可选）
    val note: String? = null
)

