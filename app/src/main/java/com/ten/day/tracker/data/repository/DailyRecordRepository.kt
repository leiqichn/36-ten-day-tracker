package com.ten.day.tracker.data.repository

import com.ten.day.tracker.data.local.database.DailyRecordDao
import com.ten.day.tracker.data.model.DailyRecord
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DailyRecordRepository @Inject constructor(
    private val dailyRecordDao: DailyRecordDao
) {
    fun getRecordsByPeriod(periodId: Int): Flow<List<DailyRecord>> = 
        dailyRecordDao.getRecordsByPeriod(periodId)
    
    fun getRecordByPeriodAndDay(periodId: Int, dayNumber: Int): Flow<DailyRecord?> = 
        dailyRecordDao.getRecordByPeriodAndDay(periodId, dayNumber)
    
    fun getRecordsByDayNumber(dayNumber: Int): Flow<List<DailyRecord>> = 
        dailyRecordDao.getRecordsByDayNumber(dayNumber)
    
    suspend fun insertRecord(record: DailyRecord) = dailyRecordDao.insertRecord(record)
    
    suspend fun updateRecord(record: DailyRecord) = dailyRecordDao.updateRecord(record)
    
    suspend fun deleteRecord(record: DailyRecord) = dailyRecordDao.deleteRecord(record)
    
    suspend fun getDayComparisonData(dayNumber: Int): List<DayComparisonData> {
        // 这里实现从数据库获取对比数据的逻辑
        // 实际开发中需要连接多个表查询
        return emptyList()
    }
}

data class DayComparisonData(
    val periodNumber: Int,
    val dayNumber: Int,
    val moodRating: Int,
    val energyLevel: Int,
    val completionRate: Int,
    val summary: String?
)