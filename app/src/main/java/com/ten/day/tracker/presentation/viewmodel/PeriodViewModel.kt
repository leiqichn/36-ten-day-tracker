package com.ten.day.tracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ten.day.tracker.data.model.Period
import com.ten.day.tracker.data.repository.PeriodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class PeriodViewModel @Inject constructor(
    private val periodRepository: PeriodRepository
) : ViewModel() {
    
    private val _periods = MutableStateFlow<List<Period>>(emptyList())
    val periods: StateFlow<List<Period>> = _periods.asStateFlow()
    
    private val _currentPeriod = MutableStateFlow<Period?>(null)
    val currentPeriod: StateFlow<Period?> = _currentPeriod.asStateFlow()
    
    private val _selectedPeriod = MutableStateFlow<Period?>(null)
    val selectedPeriod: StateFlow<Period?> = _selectedPeriod.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadPeriods()
        loadCurrentPeriod()
    }
    
    private fun loadPeriods() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentYear = LocalDate.now().year
                periodRepository.getPeriodsByYear(currentYear).collectLatest { periodsList ->
                    if (periodsList.isEmpty()) {
                        // 如果没有数据，生成今年的周期
                        periodRepository.generateYearlyPeriods(currentYear)
                    } else {
                        _periods.value = periodsList
                    }
                }
            } catch (e: Exception) {
                _error.value = "加载周期数据失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private fun loadCurrentPeriod() {
        viewModelScope.launch {
            periodRepository.getCurrentPeriod().collectLatest { period ->
                _currentPeriod.value = period
            }
        }
    }
    
    fun selectPeriod(periodId: Int) {
        viewModelScope.launch {
            periodRepository.getPeriodById(periodId).collectLatest { period ->
                _selectedPeriod.value = period
            }
        }
    }
    
    fun refreshData() {
        loadPeriods()
        loadCurrentPeriod()
    }
    
    fun clearError() {
        _error.value = null
    }
    
    fun getPeriodProgress(period: Period): Float {
        val today = LocalDate.now()
        val start = period.startDate
        val end = period.endDate
        
        return when {
            today < start -> 0f
            today > end -> 1f
            else -> {
                val totalDays = start.until(end).days + 1
                val passedDays = start.until(today).days + 1
                passedDays.toFloat() / totalDays.toFloat()
            }
        }
    }
    
    fun getRemainingDays(period: Period): Int {
        val today = LocalDate.now()
        val end = period.endDate
        
        return when {
            today > end -> 0
            else -> today.until(end).days + 1
        }
    }
    
    fun getPeriodStats(): PeriodStats {
        val allPeriods = _periods.value
        val completed = allPeriods.count { it.status.name == "COMPLETED" }
        val inProgress = allPeriods.count { it.status.name == "IN_PROGRESS" }
        val upcoming = allPeriods.count { it.status.name == "UPCOMING" }
        val total = allPeriods.size
        
        return PeriodStats(
            total = total,
            completed = completed,
            inProgress = inProgress,
            upcoming = upcoming,
            completionRate = if (total > 0) completed.toFloat() / total.toFloat() else 0f
        )
    }
}

data class PeriodStats(
    val total: Int,
    val completed: Int,
    val inProgress: Int,
    val upcoming: Int,
    val completionRate: Float
)