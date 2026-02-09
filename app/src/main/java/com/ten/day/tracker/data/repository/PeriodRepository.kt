package com.ten.day.tracker.data.repository

import com.ten.day.tracker.data.local.database.PeriodDao
import com.ten.day.tracker.data.model.Period
import com.ten.day.tracker.data.model.PeriodStatus
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class PeriodRepository @Inject constructor(
    private val periodDao: PeriodDao
) {
    fun getAllPeriods(): Flow<List<Period>> = periodDao.getAllPeriods()
    
    fun getPeriodsByYear(year: Int): Flow<List<Period>> = periodDao.getPeriodsByYear(year)
    
    fun getPeriodById(id: Int): Flow<Period?> = periodDao.getPeriodById(id)
    
    fun getCurrentPeriod(): Flow<Period?> {
        val today = LocalDate.now()
        return periodDao.getPeriodByDate(today.toString())
    }
    
    suspend fun generateYearlyPeriods(year: Int) {
        val periods = mutableListOf<Period>()
        var startDate = LocalDate.of(year, 1, 1)
        
        for (periodNumber in 1..36) {
            val endDate = startDate.plusDays(9)
            
            val period = Period(
                periodNumber = periodNumber,
                year = year,
                startDate = startDate,
                endDate = endDate,
                status = if (LocalDate.now() < startDate) {
                    PeriodStatus.UPCOMING
                } else if (LocalDate.now() > endDate) {
                    PeriodStatus.COMPLETED
                } else {
                    PeriodStatus.IN_PROGRESS
                }
            )
            
            periods.add(period)
            startDate = endDate.plusDays(1)
        }
        
        periodDao.insertAllPeriods(periods)
    }
    
    suspend fun updatePeriodStatus(periodId: Int, status: PeriodStatus) {
        val period = periodDao.getPeriodById(periodId)
        // 这里需要实现更新逻辑
    }
    
    suspend fun insertPeriod(period: Period) = periodDao.insertPeriod(period)
    
    suspend fun updatePeriod(period: Period) = periodDao.updatePeriod(period)
    
    suspend fun deletePeriod(period: Period) = periodDao.deletePeriod(period)
}