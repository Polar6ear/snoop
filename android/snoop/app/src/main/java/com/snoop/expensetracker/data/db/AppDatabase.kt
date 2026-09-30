package com.snoop.expensetracker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.snoop.expensetracker.data.db.dao.BudgetDao
import com.snoop.expensetracker.data.db.dao.BalanceDao
import com.snoop.expensetracker.data.db.dao.CategoryDao
import com.snoop.expensetracker.data.db.dao.MerchantMappingDao
import com.snoop.expensetracker.data.db.dao.ParserVersionDao
import com.snoop.expensetracker.data.db.dao.FallbackPendingDao
import com.snoop.expensetracker.data.db.dao.TransactionDao
import com.snoop.expensetracker.data.db.entities.BudgetEntity
import com.snoop.expensetracker.data.db.entities.BalanceAnchorEntity
import com.snoop.expensetracker.data.db.entities.FallbackPendingEntity
import com.snoop.expensetracker.data.db.entities.CategoryEntity
import com.snoop.expensetracker.data.db.entities.MerchantMappingEntity
import com.snoop.expensetracker.data.db.entities.ParserVersionEntity
import com.snoop.expensetracker.data.db.entities.TransactionEntity

/**
 * Local-first source of truth (spec §6/§7). No backend Postgres exists in Phase 1 — see the
 * open decision in spec §13, which this project defaults to "stateless, no accounts" per the
 * spec's own recommendation. If that decision changes later, only Phase-2 sync code is added;
 * this schema doesn't need to change shape, just gain a userId dimension server-side.
 */
@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        MerchantMappingEntity::class,
        BudgetEntity::class,
        ParserVersionEntity::class,
        FallbackPendingEntity::class,
        BalanceAnchorEntity::class
    ],
    version = 7,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun merchantMappingDao(): MerchantMappingDao
    abstract fun budgetDao(): BudgetDao
    abstract fun balanceDao(): BalanceDao
    abstract fun parserVersionDao(): ParserVersionDao
    abstract fun fallbackPendingDao(): FallbackPendingDao

    companion object {
        const val DATABASE_NAME = "snoop.db"
    }
}
