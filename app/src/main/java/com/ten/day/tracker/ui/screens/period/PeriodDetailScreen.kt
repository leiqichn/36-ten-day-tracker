package com.ten.day.tracker.ui.screens.period

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Target
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
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodDetailScreen(
    periodId: Int,
    onBack: () -> Unit,
    viewModel: PeriodViewModel = hiltViewModel()
) {
    LaunchedEffect(periodId) {
        viewModel.selectPeriod(periodId)
    }
    
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = selectedPeriod?.let { "第${it.periodNumber}个10天周期" } ?: "周期详情",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: 编辑周期 */ }) {
                        Icon(Icons.Default.Edit, contentDescription = "编辑")
                    }
                }
            )
        }
    ) { paddingValues ->
        selectedPeriod?.let { period ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                // 周期概览
                item {
                    PeriodOverviewCard(period = period, viewModel = viewModel)
                }
                
                // 目标部分
                item {
                    SectionHeader(
                        title = "周期目标",
                        icon = Icons.Default.Target,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                // 目标列表
                items(listOf("目标1", "目标2", "目标3")) { goal ->
                    GoalItem(goal = goal)
                }
                
                item {
                    Button(
                        onClick = { /* TODO: 添加目标 */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("添加目标")
                    }
                }
                
                // 结果部分
                item {
                    SectionHeader(
                        title = "周期结果",
                        icon = Icons.Default.CheckCircle,
                        color = Color(0xFF4CAF50)
                    )
                }
                
                // 结果内容
                item {
                    ResultContent(period = period)
                }
                
                // 每日记录
                item {
                    SectionHeader(
                        title = "每日记录",
                        icon = Icons.Default.Edit,
                        color = Color(0xFFFF9800)
                    )
                }
                
                // 每日记录列表
                items((1..10).toList()) { day ->
                    DailyRecordItem(day = day, period = period)
                }
            }
        } ?: run {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun PeriodOverviewCard(
    period: com.ten.day.tracker.data.model.Period,
    viewModel: PeriodViewModel
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
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
                    text = "第${period.periodNumber}个10天周期",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
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
            
            val formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日")
            Text(
                text = "${period.startDate.format(formatter)} - ${period.endDate.format(formatter)}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // 进度信息
            val progress = viewModel.getPeriodProgress(period)
            val remainingDays = viewModel.getRemainingDays(period)
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "已过天数: ${(progress * 10).toInt()}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "剩余天数: $remainingDays",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color
        )
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun GoalItem(goal: String) {
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
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = goal,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
            
            Checkbox(
                checked = false,
                onCheckedChange = { /* TODO: 更新目标状态 */ }
            )
        }
    }
}

@Composable
fun ResultContent(period: com.ten.day.tracker.data.model.Period) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (period.status == com.ten.day.tracker.data.model.PeriodStatus.COMPLETED) {
                // 显示结果
                Text(
                    text = "完成度: 85%",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "关键成就: 完成了3个主要目标",
                    fontSize = 14.sp
                )
                Text(
                    text = "自我评分: 8/10",
                    fontSize = 14.sp
                )
            } else {
                // 提示记录结果
                Text(
                    text = "周期结束后记录结果",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
                Button(
                    onClick = { /* TODO: 记录结果 */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("记录结果")
                }
            }
        }
    }
}

@Composable
fun DailyRecordItem(day: Int, period: com.ten.day.tracker.data.model.Period) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "第${day}天",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "2024年1月${day}日",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Text(
                text = "未记录",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
            
            IconButton(
                onClick = { /* TODO: 编辑记录 */ },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "编辑记录",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}