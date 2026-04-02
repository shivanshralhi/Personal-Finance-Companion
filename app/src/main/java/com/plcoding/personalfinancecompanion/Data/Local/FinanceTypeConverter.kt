package com.plcoding.personalfinancecompanion.Data.Local

import androidx.room.TypeConverter
import com.plcoding.personalfinancecompanion.Domain.Model.TransactionType
import java.time.LocalDate
import java.time.YearMonth

class FinanceTypeConverters {
    @TypeConverter
    fun fromLocalDate(value: String?): LocalDate? = value?.let(LocalDate::parse)

    @TypeConverter
    fun localDateToString(value: LocalDate?): String? = value?.toString()

    @TypeConverter
    fun fromYearMonth(value: String?): YearMonth? = value?.let(YearMonth::parse)

    @TypeConverter
    fun yearMonthToString(value: YearMonth?): String? = value?.toString()

    @TypeConverter
    fun fromTransactionType(value: String?): TransactionType? = value?.let(TransactionType::valueOf)

    @TypeConverter
    fun transactionTypeToString(value: TransactionType?): String? = value?.name
}