package com.ten.day.tracker.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ten.day.tracker.data.model.DailyRecord
import com.ten.day.tracker.data.model.Period
import com.ten.day.tracker.data.model.PeriodGoal
import com.ten.day.tracker.data.model.PeriodResult

@Database(
    entities = [
        Period::class,
        PeriodGoal::class,
        PeriodResult::class,
        DailyRecord::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TenDayDatabase : RoomDatabase() {
    abstract fun periodDao(): PeriodDao
    abstract fun periodGoalDao(): PeriodGoalDao
    abstract fun periodResultDao(): PeriodResultDao
    abstract fun dailyRecordDao(): DailyRecordDao
}