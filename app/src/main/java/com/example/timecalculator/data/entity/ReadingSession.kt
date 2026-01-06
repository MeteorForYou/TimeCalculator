package com.example.timecalculator.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 读取会话 - 每次通过无障碍服务读取屏幕对应一条记录
 */
@Entity(tableName = "reading_session")
data class ReadingSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // 原始读取的文本内容
    val rawText: String,

    // 创建时间
    val createdAt: Long = System.currentTimeMillis(),

    // 备注（可选）
    val note: String? = null
)

