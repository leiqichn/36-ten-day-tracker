package com.ten.day.tracker.data.local.database

import androidx.room.*
import com.ten.day.tracker.data.model.DailyRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyRecordDao {
    @Query("SELECT * FROM daily_records WHERE periodId = :periodId ORDER BY dayNumber ASC")
    fun getRecordsByPeriod(periodId: Int): Flow<List<DailyRecord>>
    
    @Query("SELECT * FROM daily_records WHERE periodId = :periodId AND dayNumber = :dayNumber")
    fun getRecordByPeriodAndDay(periodId: Int, dayNumber: Int): Flow<DailyRecord?>
    
    @Query("SELECT * FROM daily_records WHERE dayNumber = :dayNumber ORDER BY periodId ASC")
    fun getRecordsByDayNumber(dayNumber: Int): Flow<List<DailyRecord>>
    
    @Query("SELECT * FROM daily_records WHERE recordDate = :date")
    fun getRecordByDate(date: String): Flow<DailyRecord?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: DailyRecord)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRecords(records: List<DailyRecord>)
    
    @Update
    suspend fun updateRecord(record: DailyRecord)
    
    @Delete
    suspend fun deleteRecord(record: DailyRecord)
    
    @Query("DELETE FROM daily_records WHERE periodId = :periodId")
    suspend fun deleteRecordsByPeriod(periodId: Int)
    
    @Query("DELETE FROM daily_records")
    suspend fun deleteAllRecords()
    
    // 用于对比分析的查询
    @Query("""
        SELECT dr.*, p.periodNumber 
        FROM daily_records dr
        INNER JOIN periods p ON dr.periodId = p.id
        WHERE dr.dayNumber = :dayNumber
        ORDER BY p.periodNumber ASC
    """)
    fun getDayComparisonRecords(dayNumber: Int): Flow<List<DailyRecordWithPeriod>>
}

data class DailyRecordWithPeriod(
    val id: Int,
    val periodId: Int,
    val periodNumber: Int,
    val dayNumber: Int,
    val recordDate: String,
    val summary: String?,
    val completedTasks: List<String>,
    val moodRating: Int?,
    val energyLevel: Int?,
    val notes: String?,
    val createdAt: Long,
    val updatedAt: Long
)