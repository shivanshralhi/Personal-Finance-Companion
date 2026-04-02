package com.plcoding.personalfinancecompanion.presentation

import androidx.lifecycle.ViewModel
import com.plcoding.personalfinancecompanion.Domain.Model.Transaction
import com.plcoding.personalfinancecompanion.Domain.Model.TransactionType
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FinanceViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        FinanceUiState(
            transactions = listOf(
                Transaction(amount = 2500.0, type = TransactionType.INCOME, category = "Salary", date = LocalDate.now()),
                Transaction(amount = 25.5, type = TransactionType.EXPENSE, category = "Food", date = LocalDate.now(), notes = "Lunch"),
                Transaction(amount = 60.0, type = TransactionType.EXPENSE, category = "Transport", date = LocalDate.now())
            )
        )
    )
    val uiState: StateFlow<FinanceUiState> = _uiState.asStateFlow()

    fun addTransaction(transaction: Transaction) {
        _uiState.update { it.copy(transactions = listOf(transaction) + it.transactions) }
    }

    fun deleteTransaction(id: String) {
        _uiState.update { it.copy(transactions = it.transactions.filterNot { tx -> tx.id == id }) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }
}