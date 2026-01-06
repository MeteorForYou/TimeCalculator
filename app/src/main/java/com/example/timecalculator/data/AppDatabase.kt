package com.example.timecalculator.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.timecalculator.data.dao.CalculationDao
import com.example.timecalculator.data.dao.ReadingSessionDao
import com.example.timecalculator.data.dao.TimeGroupDao
import com.example.timecalculator.data.dao.TimeTagDao
import com.example.timecalculator.data.entity.Calculation
import com.example.timecalculator.data.entity.ReadingSession
import com.example.timecalculator.data.entity.TimeGroup
import com.example.timecalculator.data.entity.TimeTag

@Database(
    entities = [
        ReadingSession::class,
        TimeTag::class,
        Calculation::class,
        TimeGroup::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun readingSessionDao(): ReadingSessionDao
    abstract fun timeTagDao(): TimeTagDao
    abstract fun calculationDao(): CalculationDao
    abstract fun timeGroupDao(): TimeGroupDao

    companion object {
        const val DATABASE_NAME = "time_calculator_db"
    }
}

