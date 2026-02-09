package com.ten.day.tracker.ui.screens.comparison

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ten.day.tracker.presentation.viewmodel.DayComparisonViewModel
import com.ten.day.tracker.ui.components.ComparisonChart
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayComparisonScreen(
    onBack: () -> Unit,
    viewModel: DayComparisonViewModel = hiltViewModel()
) {
    val comparisonData by viewModel.comparisonData.collectAsState()
    val selectedDay by viewModel.selectedDay.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "天数对比分析",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshData() }) {
                        Icon(Icons.Default.CompareArrows, contentDescription = "刷新")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (error != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = error ?: "未知错误",
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(onClick = { viewModel.clearError() }) {
                        Text("重试")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // 天数选择器
                DaySelector(
                    selectedDay = selectedDay,
                    onDaySelected = { day -> viewModel.selectDay(day) }
                )
                
                // 对比图表
                ComparisonChart(
                    data = comparisonData,
                    selectedDay = selectedDay,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(16.dp)
                )
                
                // 详细对比列表
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(comparisonData) { dayData ->
                        DayComparisonItem(
                            dayData = dayData,
                            selectedDay = selectedDay
                        )
                    }
                }
                
                // 统计摘要
                ComparisonSummary(
                    comparisonData = comparisonData,
                    selectedDay = selectedDay,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun DaySelector(
    selectedDay: Int,
    onDaySelected: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "选择对比天数",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                (1..10).forEach { day ->
                    DayChip(
                        day = day,
                        isSelected = day == selectedDay,
                        onClick = { onDaySelected(day) }
                    )
                }
            }
            
            Text(
                text = "对比所有周期中第${selectedDay}天的表现",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DayChip(
    day: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surface
    }
    
    val textColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    
    Surface(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f),
        shape = RoundedCornerShape(8.dp),
        color = backgroundColor,
        tonalElevation = if (isSelected) 4.dp else 2.dp,
        onClick = onClick
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "第${day}天",
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = textColor
            )
        }
    }
}

@Composable
fun DayComparisonItem(
    dayData: DayComparisonData,
    selectedDay: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 周期编号
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = when (dayData.periodNumber % 4) {
                            1 -> Color(0xFFE3F2FD)
                            2 -> Color(0xFFE8F5E9)
                            3 -> Color(0xFFFFF3E0)
                            else -> Color(0xFFF3E5F5)
                        },
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "第${dayData.periodNumber}期",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // 详细信息
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "第${selectedDay}天表现",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Text(
                        text = "评分: ${dayData.rating}/10",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = getRatingColor(dayData.rating)
                    )
                }
                
                // 进度条
                LinearProgressIndicator(
                    progress = dayData.rating / 10f,
                    modifier = Modifier.fillMaxWidth(),
                    color = getRatingColor(dayData.rating),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                
                // 详细指标
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ComparisonMetric(
                        label = "心情",
                        value = dayData.moodRating,
                        maxValue = 10
                    )
                    
                    ComparisonMetric(
                        label = "能量",
                        value = dayData.energyLevel,
                        maxValue = 10
                    )
                    
                    ComparisonMetric(
                        label = "完成度",
                        value = dayData.completionRate,
                        maxValue = 100,
                        suffix = "%"
                    )
                }
                
                // 总结
                dayData.summary?.let { summary ->
                    if (summary.isNotBlank()) {
                        Text(
                            text = summary,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
            
            // 趋势指示器
            Icon(
                imageVector = Icons.Default.TrendingUp,
                contentDescription = "趋势",
                tint = if (dayData.trend > 0) Color.Green else Color.Red,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun ComparisonMetric(
    label: String,
    value: Int,
    maxValue: Int,
    suffix: String = ""
) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "$value$suffix",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = getMetricColor(value, maxValue)
        )
    }
}

@Composable
fun ComparisonSummary(
    comparisonData: List<DayComparisonData>,
    selectedDay: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "第${selectedDay}天统计摘要",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            val avgRating = comparisonData.map { it.rating }.average()
            val avgMood = comparisonData.map { it.moodRating }.average()
            val avgEnergy = comparisonData.map { it.energyLevel }.average()
            val avgCompletion = comparisonData.map { it.completionRate }.average()
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SummaryItem(
                    label = "平均评分",
                    value = avgRating.roundToInt(),
                    maxValue = 10
                )
                
                SummaryItem(
                    label = "平均心情",
                    value = avgMood.roundToInt(),
                    maxValue = 10
                )
                
                SummaryItem(
                    label = "平均能量",
                    value = avgEnergy.roundToInt(),
                    maxValue = 10
                )
                
                SummaryItem(
                    label = "平均完成度",
                    value = avgCompletion.roundToInt(),
                    maxValue = 100,
                    suffix = "%"
                )
            }
            
            // 最佳和最差表现
            val bestDay = comparisonData.maxByOrNull { it.rating }
            val worstDay = comparisonData.minByOrNull { it.rating }
            
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                bestDay?.let {
                    Text(
                        text = "最佳表现: 第${it.periodNumber}期 (评分: ${it.rating}/10)",
                        fontSize = 14.sp,
                        color = Color.Green
                    )
                }
                
                worstDay?.let {
                    Text(
                        text = "最差表现: 第${it.periodNumber}期 (评分: ${it.rating}/10)",
                        fontSize = 14.sp,
                        color = Color.Red
                    )
                }
            }
        }
    }
}

@Composable
fun SummaryItem(
    label: String,
    value: Int,
    maxValue: Int,
    suffix: String = ""
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "$value$suffix",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = getMetricColor(value, maxValue)
        )
    }
}

fun getRatingColor(rating: Int): Color {
    return when {
        rating >= 8 -> Color(0xFF4CAF50)  // 优秀 - 绿色
        rating >= 6 -> Color(0xFF2196F3)  // 良好 - 蓝色
        rating >= 4 -> Color(0xFFFF9800)  // 一般 - 橙色
        else -> Color(0xFFF44336)         // 较差 - 红色
    }
}

fun getMetricColor(value: Int, maxValue: Int): Color {
    val percentage = value.toFloat() / maxValue.toFloat()
    return when {
        percentage >= 0.8 -> Color(0xFF4CAF50)
        percentage >= 0.6 -> Color(0xFF2196F3)
        percentage >= 0.4 -> Color(0xFFFF9800)
        else -> Color(0xFFF44336)
    }
}

data class DayComparisonData(
    val periodNumber: Int,
    val rating: Int,
    val moodRating: Int,
    val energyLevel: Int,
    val completionRate: Int,
    val summary: String?,
    val trend: Int // 与前一个周期相比的变化 (-1, 0, 1)
)