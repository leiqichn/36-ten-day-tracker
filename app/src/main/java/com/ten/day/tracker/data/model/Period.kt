package com.ten.day.tracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * 10天周期实体
 * 一年有36个10天周期
 */
@Entity(tableName = "periods")
data class Period(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    // 周期编号 (1-36)
    val periodNumber: Int,
    
    // 所属年份
    val year: Int,
    
    // 周期开始日期
    val startDate: LocalDate,
    
    // 周期结束日期
    val endDate: LocalDate,
    
    // 周期状态
    val status: PeriodStatus = PeriodStatus.UPCOMING,
    
    // 创建时间
    val createdAt: Long = System.currentTimeMillis(),
    
    // 更新时间
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * 周期状态
 */
enum class PeriodStatus {
    UPCOMING,      // 即将开始
    IN_PROGRESS,   // 进行中
    COMPLETED,     // 已完成
    SKIPPED        // 已跳过
}

/**
 * 周期目标
 */
@Entity(tableName = "period_goals")
data class PeriodGoal(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    // 所属周期ID
    val periodId: Int,
    
    // 目标内容
    val content: String,
    
    // 目标类型
    val type: GoalType,
    
    // 目标优先级
    val priority: Priority = Priority.MEDIUM,
    
    // 是否完成
    val isCompleted: Boolean = false,
    
    // 完成日期
    val completedAt: Long? = null,
    
    // 创建时间
    val createdAt: Long = System.currentTimeMillis(),
    
    // 更新时间
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * 目标类型
 */
enum class GoalType {
    WORK,          // 工作
    STUDY,         // 学习
    HEALTH,        // 健康
    PERSONAL,      // 个人成长
    FINANCE,       // 财务
    RELATIONSHIP,  // 关系
    HOBBY,         // 爱好
    OTHER          // 其他
}

/**
 * 优先级
 */
enum class Priority {
    HIGH,          // 高
    MEDIUM,        // 中
    LOW            // 低
}

/**
 * 周期结果
 */
@Entity(tableName = "period_results")
data class PeriodResult(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    // 所属周期ID
    val periodId: Int,
    
    // 结果总结
    val summary: String,
    
    // 完成度 (0-100)
    val completionRate: Int,
    
    // 关键成就
    val achievements: List<String> = emptyList(),
    
    // 遇到的挑战
    val challenges: List<String> = emptyList(),
    
    // 学到的经验
    val lessons: List<String> = emptyList(),
    
    // 下一步计划
    val nextSteps: List<String> = emptyList(),
    
    // 自我评分 (1-10)
    val selfRating: Int? = null,
    
    // 创建时间
    val createdAt: Long = System.currentTimeMillis(),
    
    // 更新时间
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * 每日记录
 */
@Entity(tableName = "daily_records")
data class DailyRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    // 所属周期ID
    val periodId: Int,
    
    // 日期 (周期内的第几天，1-10)
    val dayNumber: Int,
    
    // 记录日期
    val recordDate: LocalDate,
    
    // 今日总结
    val summary: String? = null,
    
    // 完成的任务
    val completedTasks: List<String> = emptyList(),
    
    // 心情评分 (1-10)
    val moodRating: Int? = null,
    
    // 能量水平 (1-10)
    val energyLevel: Int? = null,
    
    // 备注
    val notes: String? = null,
    
    // 创建时间
    val createdAt: Long = System.currentTimeMillis(),
    
    // 更新时间
    val updatedAt: Long = System.currentTimeMillis()
)