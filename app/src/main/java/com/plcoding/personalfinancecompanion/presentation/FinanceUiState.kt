package com.plcoding.personalfinancecompanion.presentation

import com.plcoding.personalfinancecompanion.Domain.Model.Transaction
import kotlin.collections.filter
import com.plcoding.personalfinancecompanion.Domain.Model.TransactionType
import java.time.LocalDate
import com.plcoding.personalfinancecompanion.Domain.Model.SavingsGoal
import java.time.DayOfWeek
import java.time.temporal.TemporalAdjusters
import kotlin.math.roundToInt
data class FinanceUiState(
    val transactions: List<Transaction> = emptyList(),
    val searchQuery: String = "",
    val selectedTypeFilter: String = "ALL",
    val savingsGoal: SavingsGoal? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
) {
    val visibleTransactions: List<Transaction>
        get() = transactions.filter { transaction ->
            val matchesSearch = searchQuery.isBlank() ||
                    transaction.category.contains(searchQuery, ignoreCase = true) ||
                    transaction.notes.contains(searchQuery, ignoreCase = true)

            val matchesType = selectedTypeFilter == "ALL" || transaction.type.name == selectedTypeFilter
            matchesSearch && matchesType
        }
    val totalIncome: Double
        get() = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }

    val totalExpense: Double
        get() = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

    val currentBalance: Double
        get() = totalIncome - totalExpense
    val currentMonthSavings: Double
        get() {
            val now = LocalDate.now()
            val currentMonthTransactions = transactions.filter { transaction ->
                transaction.date.month == now.month && transaction.date.year == now.year
            }
            val monthlyIncome = currentMonthTransactions
                .filter { it.type == TransactionType.INCOME }
                .sumOf { it.amount }
            val monthlyExpense = currentMonthTransactions
                .filter { it.type == TransactionType.EXPENSE }
                .sumOf { it.amount }
            return monthlyIncome - monthlyExpense
        }

    val savingsProgress: Float
        get() {
            val goal = savingsGoal ?: return 0f
            if (goal.targetAmount <= 0.0) return 0f
            return (currentMonthSavings / goal.targetAmount).toFloat().coerceIn(0f, 1f)
        }

    val savingsProgressPercent: Int
        get() = (savingsProgress * 100).roundToInt()

    val hasReachedSavingsGoal: Boolean
        get() = savingsGoal != null && currentMonthSavings >= savingsGoal.targetAmount

    val nextSavingsMilestone: Int?
        get() {
            if (savingsGoal == null || hasReachedSavingsGoal) return null
            val milestones = listOf(25, 50, 75, 100)
            return milestones.firstOrNull { savingsProgressPercent < it }
        }
    private val expenseTransactions: List<Transaction>
        get() = transactions.filter { it.type == TransactionType.EXPENSE }

    val highestSpendingCategory: String?
        get() = expenseTransactions
            .groupBy { it.category }
            .maxByOrNull { (_, txs) -> txs.sumOf { it.amount } }
            ?.key

    val highestSpendingCategoryAmount: Double
        get() = expenseTransactions
            .groupBy { it.category }
            .maxByOrNull { (_, txs) -> txs.sumOf { it.amount } }
            ?.value
            ?.sumOf { it.amount }
            ?: 0.0

    val thisWeekExpense: Double
        get() {
            val now = LocalDate.now()
            val startOfThisWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val endOfThisWeek = startOfThisWeek.plusDays(6)
            return expenseTransactions
                .filter { it.date in startOfThisWeek..endOfThisWeek }
                .sumOf { it.amount }
        }

    val lastWeekExpense: Double
        get() {
            val now = LocalDate.now()
            val startOfThisWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val startOfLastWeek = startOfThisWeek.minusWeeks(1)
            val endOfLastWeek = startOfLastWeek.plusDays(6)
            return expenseTransactions
                .filter { it.date in startOfLastWeek..endOfLastWeek }
                .sumOf { it.amount }
        }

    val frequentCategory: String?
        get() = transactions
            .groupingBy { it.category }
            .eachCount()
            .maxByOrNull { it.value }
            ?.key

    val frequentTransactionType: TransactionType?
        get() = transactions
            .groupingBy { it.type }
            .eachCount()
            .maxByOrNull { it.value }
            ?.key

    val hasEnoughInsightData: Boolean
        get() = transactions.size >= 3 && expenseTransactions.size >= 2
}
