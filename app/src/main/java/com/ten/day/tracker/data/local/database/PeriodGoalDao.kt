package com.ten.day.tracker.data.local.database

import androidx.room.*
import com.ten.day.tracker.data.model.PeriodGoal
import kotlinx.coroutines.flow.Flow

@Dao
interface PeriodGoalDao {
    @Query("SELECT * FROM period_goals WHERE periodId = :periodId ORDER BY priority DESC, createdAt ASC")
    fun getGoalsByPeriod(periodId: Int): Flow<List<PeriodGoal>>
    
    @Query("SELECT * FROM period_goals WHERE id = :id")
    fun getGoalById(id: Int): Flow<PeriodGoal?>
    
    @Query("SELECT * FROM period_goals WHERE isCompleted = :completed")
    fun getGoalsByCompletion(completed: Boolean): Flow<List<PeriodGoal>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: PeriodGoal)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllGoals(goals: List<PeriodGoal>)
    
    @Update
    suspend fun updateGoal(goal: PeriodGoal)
    
    @Delete
    suspend fun deleteGoal(goal: PeriodGoal)
    
    @Query("DELETE FROM period_goals WHERE periodId = :periodId")
    suspend fun deleteGoalsByPeriod(periodId: Int)
}