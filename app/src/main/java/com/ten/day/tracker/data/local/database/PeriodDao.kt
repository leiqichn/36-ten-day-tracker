package com.ten.day.tracker.data.local.database

import androidx.room.*
import com.ten.day.tracker.data.model.Period
import kotlinx.coroutines.flow.Flow

@Dao
interface PeriodDao {
    @Query("SELECT * FROM periods ORDER BY startDate ASC")
    fun getAllPeriods(): Flow<List<Period>>
    
    @Query("SELECT * FROM periods WHERE year = :year ORDER BY periodNumber ASC")
    fun getPeriodsByYear(year: Int): Flow<List<Period>>
    
    @Query("SELECT * FROM periods WHERE id = :id")
    fun getPeriodById(id: Int): Flow<Period?>
    
    @Query("SELECT * FROM periods WHERE periodNumber = :periodNumber AND year = :year")
    fun getPeriodByNumberAndYear(periodNumber: Int, year: Int): Flow<Period?>
    
    @Query("SELECT * FROM periods WHERE :date BETWEEN startDate AND endDate")
    fun getPeriodByDate(date: String): Flow<Period?>
    
    @Query("SELECT * FROM periods WHERE status = :status ORDER BY startDate ASC")
    fun getPeriodsByStatus(status: String): Flow<List<Period>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPeriod(period: Period)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPeriods(periods: List<Period>)
    
    @Update
    suspend fun updatePeriod(period: Period)
    
    @Delete
    suspend fun deletePeriod(period: Period)
    
    @Query("DELETE FROM periods")
    suspend fun deleteAllPeriods()
}