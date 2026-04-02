package com.plcoding.personalfinancecompanion.Data.Local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.YearMonth

@Entity(tableName = "savings_goals")
data class SavingsGoalEntity(
    @PrimaryKey val month: YearMonth,
    val targetAmount: Double
)