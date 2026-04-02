package com.plcoding.personalfinancecompanion.Data.Local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.plcoding.personalfinancecompanion.Domain.Model.TransactionType
import java.time.LocalDate

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val amount: Double,
    val type: TransactionType,
    val category: String,
    val date: LocalDate,
    val notes: String
)