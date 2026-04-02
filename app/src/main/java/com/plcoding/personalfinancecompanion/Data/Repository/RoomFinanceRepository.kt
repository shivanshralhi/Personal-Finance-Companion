package com.plcoding.personalfinancecompanion.Data.Repository

import com.plcoding.personalfinancecompanion.Data.Local.SavingsGoalEntity
import com.plcoding.personalfinancecompanion.Data.Local.TransactionEntity
import com.plcoding.personalfinancecompanion.Domain.Model.SavingsGoal
import com.plcoding.personalfinancecompanion.Domain.Model.Transaction
import com.plcoding.personalfinancecompanion.Data.Local.FinanceDao

import com.plcoding.personalfinancecompanion.Data.Repository.FinanceRepository
import java.time.YearMonth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomFinanceRepository(
    private val dao: FinanceDao
) : FinanceRepository {

    override fun observeTransactions(): Flow<List<Transaction>> {
        return dao.observeTransactions().map { entities -> entities.map(TransactionEntity::toDomain) }
    }

    override fun observeSavingsGoal(month: YearMonth): Flow<SavingsGoal?> {
        return dao.observeSavingsGoal(month).map { it?.toDomain() }
    }

    override suspend fun upsertTransaction(transaction: Transaction) {
        dao.upsertTransaction(transaction.toEntity())
    }

    override suspend fun deleteTransaction(id: String) {
        dao.deleteTransaction(id)
    }

    override suspend fun upsertSavingsGoal(goal: SavingsGoal) {
        dao.upsertSavingsGoal(goal.toEntity())
    }

    override suspend fun deleteSavingsGoal(month: YearMonth) {
        dao.deleteSavingsGoal(month)
    }
}

private fun TransactionEntity.toDomain() = Transaction(
    id = id,
    amount = amount,
    type = type,
    category = category,
    date = date,
    notes = notes
)

private fun Transaction.toEntity() = TransactionEntity(
    id = id,
    amount = amount,
    type = type,
    category = category,
    date = date,
    notes = notes
)

private fun SavingsGoalEntity.toDomain() = SavingsGoal(
    targetAmount = targetAmount,
    month = month
)

private fun SavingsGoal.toEntity() = SavingsGoalEntity(
    month = month,
    targetAmount = targetAmount
)