package com.ten.day.tracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ten.day.tracker.data.model.Period
import java.time.format.DateTimeFormatter

@Composable
fun PeriodCard(
    period: Period,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val backgroundColor = when (period.status) {
        com.ten.day.tracker.data.model.PeriodStatus.COMPLETED -> Color(0xFFE8F5E9)
        com.ten.day.tracker.data.model.PeriodStatus.IN_PROGRESS -> Color(0xFFE3F2FD)
        com.ten.day.tracker.data.model.PeriodStatus.UPCOMING -> Color(0xFFF5F5F5)
        else -> Color(0xFFF5F5F5)
    }
    
    val borderColor = when (period.status) {
        com.ten.day.tracker.data.model.PeriodStatus.COMPLETED -> Color(0xFF4CAF50)
        com.ten.day.tracker.data.model.PeriodStatus.IN_PROGRESS -> Color(0xFF2196F3)
        com.ten.day.tracker.data.model.PeriodStatus.UPCOMING -> Color(0xFF9E9E9E)
        else -> Color(0xFF9E9E9E)
    }
    
    val textColor = when (period.status) {
        com.ten.day.tracker.data.model.PeriodStatus.COMPLETED -> Color(0xFF2E7D32)
        com.ten.day.tracker.data.model.PeriodStatus.IN_PROGRESS -> Color(0xFF1565C0)
        com.ten.day.tracker.data.model.PeriodStatus.UPCOMING -> Color(0xFF616161)
        else -> Color(0xFF616161)
    }
    
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "第${period.periodNumber}期",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center
            )
            
            val formatter = DateTimeFormatter.ofPattern("MM/dd")
            Text(
                text = "${period.startDate.format(formatter)}\n${period.endDate.format(formatter)}",
                fontSize = 12.sp,
                color = textColor,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )
            
            Text(
                text = when (period.status) {
                    com.ten.day.tracker.data.model.PeriodStatus.COMPLETED -> "已完成"
                    com.ten.day.tracker.data.model.PeriodStatus.IN_PROGRESS -> "进行中"
                    com.ten.day.tracker.data.model.PeriodStatus.UPCOMING -> "即将开始"
                    else -> "未知"
                },
                fontSize = 11.sp,
                color = textColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun StatsCard(stats: com.ten.day.tracker.presentation.viewmodel.PeriodStats) {
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
            Text(
                text = "年度统计",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatItem(
                    title = "总周期",
                    value = stats.total.toString(),
                    color = MaterialTheme.colorScheme.primary
                )
                
                StatItem(
                    title = "已完成",
                    value = stats.completed.toString(),
                    color = Color(0xFF4CAF50)
                )
                
                StatItem(
                    title = "进行中",
                    value = stats.inProgress.toString(),
                    color = Color(0xFF2196F3)
                )
                
                StatItem(
                    title = "完成率",
                    value = "${(stats.completionRate * 100).toInt()}%",
                    color = Color(0xFFFF9800)
                )
            }
        }
    }
}

@Composable
fun StatItem(
    title: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = title,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}