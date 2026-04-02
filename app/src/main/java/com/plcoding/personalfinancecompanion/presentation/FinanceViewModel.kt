package com.plcoding.personalfinancecompanion.presentation

import androidx.lifecycle.ViewModel
import com.plcoding.personalfinancecompanion.Domain.Model.Transaction
import com.plcoding.personalfinancecompanion.Domain.Model.TransactionType
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.plcoding.personalfinancecompanion.Domain.Model.SavingsGoal
import kotlinx.coroutines.flow.combine
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

import com.plcoding.personalfinancecompanion.Data.Repository.FinanceRepository
import java.time.YearMonth
class FinanceViewModel(
    private val repository: FinanceRepository
) : ViewModel() {

    private val filtersState = MutableStateFlow(
        FiltersState(
            searchQuery = "",
            selectedTypeFilter = "ALL"
        )
    )

    private val _uiState = MutableStateFlow(FinanceUiState())
    val uiState: StateFlow<FinanceUiState> = _uiState.asStateFlow()

    init {
        observeFinanceData()
    }

    private fun observeFinanceData() {
        viewModelScope.launch {
            combine(
                repository.observeTransactions(),
                repository.observeSavingsGoal(YearMonth.now()),
                filtersState
            ) { transactions, goal, filters ->
                FinanceUiState(
                    transactions = transactions,
                    savingsGoal = goal,
                    searchQuery = filters.searchQuery,
                    selectedTypeFilter = filters.selectedTypeFilter,
                    isLoading = false,
                    errorMessage = null
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun addTransaction(transaction: Transaction) {
        performDbAction {
            repository.upsertTransaction(transaction)
        }
    }

    fun updateTransaction(updatedTransaction: Transaction) {
        performDbAction {
            repository.upsertTransaction(updatedTransaction)
        }
    }


    fun deleteTransaction(id: String) {
        performDbAction {
            repository.deleteTransaction(id)
        }
    }

    fun updateTypeFilter(filter: String) {
        filtersState.update { it.copy(selectedTypeFilter = filter) }
    }

    fun updateSearchQuery(query: String) {
        filtersState.update { it.copy(searchQuery = query) }
    }

    fun upsertMonthlySavingsGoal(targetAmount: Double) {
        performDbAction {
            repository.upsertSavingsGoal(
                SavingsGoal(
                    targetAmount = targetAmount,
                    month = YearMonth.now()
                )
            )
        }
    }

    fun clearSavingsGoal() {
        performDbAction {
            repository.deleteSavingsGoal(YearMonth.now())
        }
    }
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun performDbAction(action: suspend () -> Unit) {
        viewModelScope.launch {
            runCatching { action() }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(errorMessage = throwable.message ?: "Something went wrong. Please try again.")
                    }
                }
        }
    }

    data class FiltersState(
        val searchQuery: String,
        val selectedTypeFilter: String
    )

    companion object {
        fun factory(repository: FinanceRepository): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(FinanceViewModel::class.java)) {
                        return FinanceViewModel(repository) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                }
            }
        }
    }

}