package com.plcoding.personalfinancecompanion.Domain.Model


import java.time.LocalDate
import java.util.UUID

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val amount: Double,
    val type: TransactionType,
    val category: String,
    val date: LocalDate,
    val notes: String = ""
)