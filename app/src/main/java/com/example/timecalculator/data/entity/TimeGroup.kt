package com.example.timecalculator.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 时间组 - 每组包含起始时间和结束时间
 */
@Entity(
    tableName = "time_group",
    foreignKeys = [
        ForeignKey(
            entity = Calculation::class,
            parentColumns = ["id"],
            childColumns = ["calculationId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TimeTag::class,
            parentColumns = ["id"],
            childColumns = ["startTimeTagId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = TimeTag::class,
            parentColumns = ["id"],
            childColumns = ["endTimeTagId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("calculationId"),
        Index("startTimeTagId"),
        Index("endTimeTagId")
    ]
)
data class TimeGroup(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // 所属计算记录ID
    val calculationId: Long,

    // 起始时间标签ID
    val startTimeTagId: Long?,

    // 结束时间标签ID
    val endTimeTagId: Long?,

    // 该组计算结果（分钟）
    val durationMinutes: Long = 0,

    // 排序索引
    val orderIndex: Int = 0
)

