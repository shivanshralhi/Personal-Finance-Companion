package com.plcoding.personalfinancecompanion.Domain.Model

import java.time.YearMonth

data class SavingsGoal(
    val targetAmount: Double,
    val month: YearMonth = YearMonth.now()
)