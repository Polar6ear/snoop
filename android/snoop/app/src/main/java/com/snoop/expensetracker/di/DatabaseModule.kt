package com.snoop.expensetracker.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.snoop.expensetracker.data.db.AppDatabase
import com.snoop.expensetracker.data.db.dao.BudgetDao
import com.snoop.expensetracker.data.db.dao.BalanceDao
import com.snoop.expensetracker.data.db.dao.FallbackPendingDao
import com.snoop.expensetracker.data.db.dao.CategoryDao
import com.snoop.expensetracker.data.db.dao.MerchantMappingDao
import com.snoop.expensetracker.data.db.dao.ParserVersionDao
import com.snoop.expensetracker.data.db.dao.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
            // Local-first, single-device database. Every shipped schema change must have an
            // explicit migration so an app upgrade does not silently erase transaction history.
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7)
            .build()

    @Provides
    fun provideTransactionDao(db: AppDatabase): TransactionDao = db.transactionDao()

    @Provides
    fun provideCategoryDao(db: AppDatabase): CategoryDao = db.categoryDao()

    @Provides
    fun provideMerchantMappingDao(db: AppDatabase): MerchantMappingDao = db.merchantMappingDao()

    @Provides
    fun provideBudgetDao(db: AppDatabase): BudgetDao = db.budgetDao()

    @Provides
    fun provideBalanceDao(db: AppDatabase): BalanceDao = db.balanceDao()

    @Provides
    fun provideParserVersionDao(db: AppDatabase): ParserVersionDao = db.parserVersionDao()

    @Provides
    fun provideFallbackPendingDao(db: AppDatabase): FallbackPendingDao = db.fallbackPendingDao()

    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE transactions ADD COLUMN deletedAt INTEGER")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_transactions_deletedAt ON transactions(deletedAt)")
        }
    }

    private val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("CREATE INDEX IF NOT EXISTS index_transactions_deletedAt ON transactions(deletedAt)")
        }
    }


    private val MIGRATION_6_7 = object : Migration(6, 7) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE transactions ADD COLUMN suggestedCategory TEXT")
            db.execSQL("ALTER TABLE transactions ADD COLUMN suggestedCategoryConfidence REAL NOT NULL DEFAULT 0")
        }
    }

    private val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE transactions ADD COLUMN note TEXT")
        }
    }

    private val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("CREATE TABLE IF NOT EXISTS balance_anchor (id INTEGER NOT NULL, balanceMinor INTEGER NOT NULL, anchoredAt INTEGER NOT NULL, PRIMARY KEY(id))")
        }
    }

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE transactions ADD COLUMN dedupeKey TEXT")
            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_transactions_dedupeKey ON transactions(dedupeKey)")
            db.execSQL("CREATE TABLE IF NOT EXISTS fallback_pending (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, dedupeKey TEXT NOT NULL, packageName TEXT NOT NULL, text TEXT NOT NULL, createdAt INTEGER NOT NULL, attempts INTEGER NOT NULL DEFAULT 0)")
            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_fallback_pending_dedupeKey ON fallback_pending(dedupeKey)")
            db.execSQL("ALTER TABLE budgets ADD COLUMN warningNotifiedPeriodStart INTEGER")
            db.execSQL("ALTER TABLE budgets ADD COLUMN overNotifiedPeriodStart INTEGER")
        }
    }
}
