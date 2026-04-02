package com.plcoding.personalfinancecompanion.presentation

import com.plcoding.personalfinancecompanion.Domain.Model.Transaction
import kotlin.collections.filter

data class FinanceUiState(
    val transactions: List<Transaction> = emptyList(),
    val searchQuery: String = "",
    val selectedTypeFilter: String = "ALL"
) {
    val totalIncome: Double
        get() = transactions.filter { it.type.name == "INCOME" }.sumOf { it.amount }

    val totalExpense: Double
        get() = transactions.filter { it.type.name == "EXPENSE" }.sumOf { it.amount }

    val currentBalance: Double
        get() = totalIncome - totalExpense
}