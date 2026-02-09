package com.ten.day.tracker.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ten.day.tracker.presentation.viewmodel.PeriodViewModel
import com.ten.day.tracker.ui.components.PeriodCard
import com.ten.day.tracker.ui.components.StatsCard
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onPeriodClick: (Int) -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: PeriodViewModel = hiltViewModel()
) {
    val periods by viewModel.periods.collectAsState()
    val currentPeriod by viewModel.currentPeriod.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val stats by remember { derivedStateOf { viewModel.getPeriodStats() } }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "36个10天周期追踪器",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshData() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "刷新")
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "设置")
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                // 当前周期卡片
                item {
                    currentPeriod?.let { period ->
                        CurrentPeriodCard(
                            period = period,
                            onViewDetails = { onPeriodClick(period.id) },
                            viewModel = viewModel
                        )
                    }
                }
                
                // 统计卡片
                item {
                    StatsCard(stats = stats)
                }
                
                // 所有周期列表
                items(periods.chunked(3)) { rowPeriods ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowPeriods.forEach { period ->
                            PeriodCard(
                                period = period,
                                modifier = Modifier.weight(1f),
                                onClick = { onPeriodClick(period.id) }
                            )
                        }
                        // 填充空位
                        repeat(3 - rowPeriods.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CurrentPeriodCard(
    period: com.ten.day.tracker.data.model.Period,
    onViewDetails: () -> Unit,
    viewModel: PeriodViewModel
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "当前周期",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Badge(
                    containerColor = when (period.status) {
                        com.ten.day.tracker.data.model.PeriodStatus.IN_PROGRESS -> Color.Green
                        com.ten.day.tracker.data.model.PeriodStatus.COMPLETED -> Color.Blue
                        com.ten.day.tracker.data.model.PeriodStatus.UPCOMING -> Color.Gray
                        else -> Color.Gray
                    }
                ) {
                    Text(
                        text = when (period.status) {
                            com.ten.day.tracker.data.model.PeriodStatus.IN_PROGRESS -> "进行中"
                            com.ten.day.tracker.data.model.PeriodStatus.COMPLETED -> "已完成"
                            com.ten.day.tracker.data.model.PeriodStatus.UPCOMING -> "即将开始"
                            else -> "未知"
                        },
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }
            
            Text(
                text = "第${period.periodNumber}个10天周期",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            
            val formatter = DateTimeFormatter.ofPattern("MM月dd日")
            Text(
                text = "${period.startDate.format(formatter)} - ${period.endDate.format(formatter)}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // 进度条
            val progress = viewModel.getPeriodProgress(period)
            val remainingDays = viewModel.getRemainingDays(period)
            
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "进度",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = "剩余天数: $remainingDays 天",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Button(
                onClick = onViewDetails,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("查看详情")
            }
        }
    }
}