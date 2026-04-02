package com.plcoding.personalfinancecompanion.Data.Repository

import com.plcoding.personalfinancecompanion.Domain.Model.SavingsGoal
import com.plcoding.personalfinancecompanion.Domain.Model.Transaction
import java.time.YearMonth
import kotlinx.coroutines.flow.Flow

interface FinanceRepository {
    fun observeTransactions(): Flow<List<Transaction>>
    fun observeSavingsGoal(month: YearMonth): Flow<SavingsGoal?>

    suspend fun upsertTransaction(transaction: Transaction)
    suspend fun deleteTransaction(id: String)
    suspend fun upsertSavingsGoal(goal: SavingsGoal)
    suspend fun deleteSavingsGoal(month: YearMonth)
}