package com.snoop.expensetracker.data.db

import androidx.room.TypeConverter
import com.snoop.expensetracker.data.db.entities.BudgetPeriod
import com.snoop.expensetracker.data.db.entities.TransactionDirection
import com.snoop.expensetracker.data.db.entities.TransactionStatus

class Converters {
    @TypeConverter
    fun fromDirection(value: TransactionDirection): String = value.name

    @TypeConverter
    fun toDirection(value: String): TransactionDirection = TransactionDirection.valueOf(value)

    @TypeConverter
    fun fromStatus(value: TransactionStatus): String = value.name

    @TypeConverter
    fun toStatus(value: String): TransactionStatus = TransactionStatus.valueOf(value)

    @TypeConverter
    fun fromPeriod(value: BudgetPeriod): String = value.name

    @TypeConverter
    fun toPeriod(value: String): BudgetPeriod = BudgetPeriod.valueOf(value)
}
