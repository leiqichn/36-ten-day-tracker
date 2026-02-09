package com.ten.day.tracker.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ten.day.tracker.ui.screens.comparison.DayComparisonData
import kotlin.math.roundToInt

@Composable
fun ComparisonChart(
    data: List<DayComparisonData>,
    selectedDay: Int,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "第${selectedDay}天表现趋势",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.Transparent)
            ) {
                if (data.isNotEmpty()) {
                    LineChart(
                        data = data,
                        textMeasurer = textMeasurer,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "暂无数据",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            ChartLegend(modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun LineChart(
    data: List<DayComparisonData>,
    textMeasurer: TextMeasurer,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val padding = 40.dp.toPx()
        val chartWidth = size.width - 2 * padding
        val chartHeight = size.height - 2 * padding
        
        if (chartWidth <= 0 || chartHeight <= 0) return@Canvas
        
        // 绘制坐标轴
        drawAxis(padding, chartWidth, chartHeight)
        
        // 绘制网格线
        drawGridLines(padding, chartWidth, chartHeight)
        
        // 绘制数据点
        val points = calculatePoints(data, padding, chartWidth, chartHeight)
        
        // 绘制连线
        drawLine(points)
        
        // 绘制数据点
        drawDataPoints(points, data)
        
        // 绘制X轴标签
        drawXAxisLabels(data, padding, chartWidth, chartHeight, textMeasurer)
        
        // 绘制Y轴标签
        drawYAxisLabels(padding, chartHeight, textMeasurer)
    }
}

private fun DrawScope.drawAxis(
    padding: Float,
    chartWidth: Float,
    chartHeight: Float
) {
    // X轴
    drawLine(
        color = Color.Gray,
        start = Offset(padding, size.height - padding),
        end = Offset(size.width - padding, size.height - padding),
        strokeWidth = 2.dp.toPx()
    )
    
    // Y轴
    drawLine(
        color = Color.Gray,
        start = Offset(padding, padding),
        end = Offset(padding, size.height - padding),
        strokeWidth = 2.dp.toPx()
    )
}

private fun DrawScope.drawGridLines(
    padding: Float,
    chartWidth: Float,
    chartHeight: Float
) {
    val gridColor = Color.LightGray.copy(alpha = 0.3f)
    val gridStrokeWidth = 1.dp.toPx()
    
    // 水平网格线
    for (i in 0..5) {
        val y = padding + (chartHeight * i / 5)
        drawLine(
            color = gridColor,
            start = Offset(padding, y),
            end = Offset(size.width - padding, y),
            strokeWidth = gridStrokeWidth
        )
    }
    
    // 垂直网格线（每5个周期一条）
    val dataCount = 36 // 总周期数
    for (i in 0..(dataCount / 5)) {
        val x = padding + (chartWidth * i * 5 / dataCount)
        if (x <= size.width - padding) {
            drawLine(
                color = gridColor,
                start = Offset(x, padding),
                end = Offset(x, size.height - padding),
                strokeWidth = gridStrokeWidth
            )
        }
    }
}

private fun DrawScope.calculatePoints(
    data: List<DayComparisonData>,
    padding: Float,
    chartWidth: Float,
    chartHeight: Float
): List<Offset> {
    return data.mapIndexed { index, dayData ->
        val x = padding + (chartWidth * index / (data.size - 1).coerceAtLeast(1))
        // 评分范围 0-10，映射到图表高度
        val normalizedValue = dayData.rating / 10f
        val y = size.height - padding - (chartHeight * normalizedValue)
        Offset(x, y)
    }
}

private fun DrawScope.drawLine(points: List<Offset>) {
    if (points.size < 2) return
    
    val path = Path().apply {
        moveTo(points.first().x, points.first().y)
        points.drop(1).forEach { point ->
            lineTo(point.x, point.y)
        }
    }
    
    drawPath(
        path = path,
        color = MaterialTheme.colorScheme.primary,
        style = Stroke(
            width = 3.dp.toPx(),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )
    
    // 填充区域
    val fillPath = path.apply {
        lineTo(points.last().x, size.height - 40.dp.toPx())
        lineTo(points.first().x, size.height - 40.dp.toPx())
        close()
    }
    
    drawPath(
        path = fillPath,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
    )
}

private fun DrawScope.drawDataPoints(
    points: List<Offset>,
    data: List<DayComparisonData>
) {
    points.forEachIndexed { index, point ->
        val dayData = data[index]
        
        // 绘制数据点
        drawCircle(
            color = getRatingColor(dayData.rating),
            center = point,
            radius = 6.dp.toPx()
        )
        
        drawCircle(
            color = Color.White,
            center = point,
            radius = 3.dp.toPx()
        )
        
        // 在点上显示评分
        val ratingText = "${dayData.rating}"
        val textLayoutResult = androidx.compose.ui.text.TextMeasurer().measure(
            text = androidx.compose.ui.text.AnnotatedString(ratingText),
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 10.sp,
                color = Color.Black
            )
        )
        
        drawText(
            textLayoutResult = textLayoutResult,
            topLeft = Offset(
                point.x - textLayoutResult.size.width / 2,
                point.y - 20.dp.toPx()
            )
        )
    }
}

private fun DrawScope.drawXAxisLabels(
    data: List<DayComparisonData>,
    padding: Float,
    chartWidth: Float,
    chartHeight: Float,
    textMeasurer: TextMeasurer
) {
    // 每5个周期显示一个标签
    data.forEachIndexed { index, dayData ->
        if (index % 5 == 0 || index == data.size - 1) {
            val x = padding + (chartWidth * index / (data.size - 1).coerceAtLeast(1))
            val label = "第${dayData.periodNumber}期"
            
            val textLayoutResult = textMeasurer.measure(
                text = androidx.compose.ui.text.AnnotatedString(label),
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            )
            
            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(
                    x - textLayoutResult.size.width / 2,
                    size.height - padding + 5.dp.toPx()
                )
            )
        }
    }
}

private fun DrawScope.drawYAxisLabels(
    padding: Float,
    chartHeight: Float,
    textMeasurer: TextMeasurer
) {
    // Y轴标签 (0-10分)
    for (i in 0..10 step 2) {
        val y = size.height - padding - (chartHeight * i / 10)
        val label = i.toString()
        
        val textLayoutResult = textMeasurer.measure(
            text = androidx.compose.ui.text.AnnotatedString(label),
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 10.sp,
                color = Color.Gray
            )
        )
        
        drawText(
            textLayoutResult = textLayoutResult,
            topLeft = Offset(
                padding - textLayoutResult.size.width - 5.dp.toPx(),
                y - textLayoutResult.size.height / 2
            )
        )
    }
}

@Composable
fun ChartLegend(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LegendItem(
            color = Color(0xFF4CAF50),
            label = "优秀 (8-10分)"
        )
        
        LegendItem(
            color = Color(0xFF2196F3),
            label = "良好 (6-7分)"
        )
        
        LegendItem(
            color = Color(0xFFFF9800),
            label = "一般 (4-5分)"
        )
        
        LegendItem(
            color = Color(0xFFF44336),
            label = "较差 (0-3分)"
        )
    }
}

@Composable
fun LegendItem(
    color: Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, shape = androidx.compose.foundation.shape.CircleShape)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun getRatingColor(rating: Int): Color {
    return when {
        rating >= 8 -> Color(0xFF4CAF50)
        rating >= 6 -> Color(0xFF2196F3)
        rating >= 4 -> Color(0xFFFF9800)
        else -> Color(0xFFF44336)
    }
}