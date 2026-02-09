package com.ten.day.tracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ten.day.tracker.data.repository.DailyRecordRepository
import com.ten.day.tracker.data.repository.PeriodRepository
import com.ten.day.tracker.ui.screens.comparison.DayComparisonData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DayComparisonViewModel @Inject constructor(
    private val periodRepository: PeriodRepository,
    private val dailyRecordRepository: DailyRecordRepository
) : ViewModel() {
    
    private val _comparisonData = MutableStateFlow<List<DayComparisonData>>(emptyList())
    val comparisonData: StateFlow<List<DayComparisonData>> = _comparisonData.asStateFlow()
    
    private val _selectedDay = MutableStateFlow(1)
    val selectedDay: StateFlow<Int> = _selectedDay.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadComparisonData()
    }
    
    fun selectDay(day: Int) {
        _selectedDay.value = day
        loadComparisonDataForDay(day)
    }
    
    private fun loadComparisonData() {
        loadComparisonDataForDay(_selectedDay.value)
    }
    
    private fun loadComparisonDataForDay(day: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 模拟数据 - 实际开发中从数据库获取
                val mockData = generateMockComparisonData(day)
                _comparisonData.value = mockData
            } catch (e: Exception) {
                _error.value = "加载对比数据失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private fun generateMockComparisonData(day: Int): List<DayComparisonData> {
        return (1..36).map { periodNumber ->
            // 模拟数据：根据周期和天数生成不同的表现
            val baseRating = when (periodNumber % 4) {
                1 -> 7  // 第一个周期表现较好
                2 -> 6  // 第二个周期表现一般
                3 -> 8  // 第三个周期表现优秀
                else -> 5 // 第四个周期表现较差
            }
            
            // 根据天数调整评分
            val dayAdjustment = when (day) {
                1 -> 1   // 第一天通常表现较好
                10 -> -1 // 最后一天可能疲劳
                else -> 0
            }
            
            // 添加随机波动
            val randomAdjustment = (-1..1).random()
            
            val finalRating = (baseRating + dayAdjustment + randomAdjustment)
                .coerceIn(1, 10)
            
            DayComparisonData(
                periodNumber = periodNumber,
                rating = finalRating,
                moodRating = (finalRating * 0.8).toInt().coerceIn(1, 10),
                energyLevel = (finalRating * 0.9).toInt().coerceIn(1, 10),
                completionRate = (finalRating * 10).coerceIn(10, 100),
                summary = generateMockSummary(periodNumber, day, finalRating),
                trend = when {
                    periodNumber > 1 && finalRating > (baseRating + dayAdjustment) -> 1
                    periodNumber > 1 && finalRating < (baseRating + dayAdjustment) -> -1
                    else -> 0
                }
            )
        }
    }
    
    private fun generateMockSummary(periodNumber: Int, day: Int, rating: Int): String {
        return when {
            rating >= 8 -> "第${periodNumber}期第${day}天表现优秀，完成了所有主要任务"
            rating >= 6 -> "第${periodNumber}期第${day}天表现良好，完成了大部分任务"
            rating >= 4 -> "第${periodNumber}期第${day}天表现一般，完成了一半任务"
            else -> "第${periodNumber}期第${day}天表现较差，需要改进"
        }
    }
    
    fun refreshData() {
        loadComparisonDataForDay(_selectedDay.value)
    }
    
    fun clearError() {
        _error.value = null
    }
    
    fun getDayComparisonStats(day: Int): DayComparisonStats {
        val dataForDay = _comparisonData.value
        if (dataForDay.isEmpty()) return DayComparisonStats()
        
        val ratings = dataForDay.map { it.rating }
        val moods = dataForDay.map { it.moodRating }
        val energies = dataForDay.map { it.energyLevel }
        val completions = dataForDay.map { it.completionRate }
        
        return DayComparisonStats(
            averageRating = ratings.average(),
            averageMood = moods.average(),
            averageEnergy = energies.average(),
            averageCompletion = completions.average(),
            bestPeriod = dataForDay.maxByOrNull { it.rating }?.periodNumber ?: 0,
            worstPeriod = dataForDay.minByOrNull { it.rating }?.periodNumber ?: 0,
            ratingDistribution = mapOf(
                "优秀" to ratings.count { it >= 8 },
                "良好" to ratings.count { it in 6..7 },
                "一般" to ratings.count { it in 4..5 },
                "较差" to ratings.count { it <= 3 }
            )
        )
    }
    
    fun getTrendAnalysis(day: Int): TrendAnalysis {
        val dataForDay = _comparisonData.value
        if (dataForDay.size < 2) return TrendAnalysis()
        
        val trends = dataForDay.map { it.trend }
        val positiveTrends = trends.count { it > 0 }
        val negativeTrends = trends.count { it < 0 }
        val stableTrends = trends.count { it == 0 }
        
        return TrendAnalysis(
            overallTrend = when {
                positiveTrends > negativeTrends -> "上升趋势"
                negativeTrends > positiveTrends -> "下降趋势"
                else -> "稳定趋势"
            },
            improvementRate = (positiveTrends.toFloat() / dataForDay.size) * 100,
            consistencyScore = (stableTrends.toFloat() / dataForDay.size) * 100
        )
    }
}

data class DayComparisonStats(
    val averageRating: Double = 0.0,
    val averageMood: Double = 0.0,
    val averageEnergy: Double = 0.0,
    val averageCompletion: Double = 0.0,
    val bestPeriod: Int = 0,
    val worstPeriod: Int = 0,
    val ratingDistribution: Map<String, Int> = emptyMap()
)

data class TrendAnalysis(
    val overallTrend: String = "未知",
    val improvementRate: Float = 0f,
    val consistencyScore: Float = 0f
)