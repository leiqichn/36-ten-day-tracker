package com.ten.day.tracker.data.local.database

import androidx.room.*
import com.ten.day.tracker.data.model.PeriodResult
import kotlinx.coroutines.flow.Flow

@Dao
interface PeriodResultDao {
    @Query("SELECT * FROM period_results WHERE periodId = :periodId")
    fun getResultByPeriod(periodId: Int): Flow<PeriodResult?>
    
    @Query("SELECT * FROM period_results WHERE id = :id")
    fun getResultById(id: Int): Flow<PeriodResult?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: PeriodResult)
    
    @Update
    suspend fun updateResult(result: PeriodResult)
    
    @Delete
    suspend fun deleteResult(result: PeriodResult)
    
    @Query("DELETE FROM period_results WHERE periodId = :periodId")
    suspend fun deleteResultByPeriod(periodId: Int)
}