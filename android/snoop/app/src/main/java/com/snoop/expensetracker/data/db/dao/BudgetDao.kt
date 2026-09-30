package com.snoop.expensetracker.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Upsert
import com.snoop.expensetracker.data.db.entities.BudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    @Upsert
    suspend fun upsert(budget: BudgetEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun replaceAll(budgets: List<BudgetEntity>)

    @Query("DELETE FROM budgets")
    suspend fun deleteAll()

    @Query("SELECT * FROM budgets ORDER BY id")
    suspend fun getAllOnce(): List<BudgetEntity>

    @Query("SELECT * FROM budgets WHERE categoryId = :categoryId LIMIT 1")
    suspend fun getForCategory(categoryId: Long): BudgetEntity?

    @Query("SELECT * FROM budgets")
    fun observeAll(): Flow<List<BudgetEntity>>

    /** One-shot snapshot (not a Flow) for background jobs like the periodic budget-check worker. */
    @Query("UPDATE budgets SET warningNotifiedPeriodStart = :periodStart WHERE categoryId = :categoryId AND (warningNotifiedPeriodStart IS NULL OR warningNotifiedPeriodStart != :periodStart)")
    suspend fun markWarningNotified(categoryId: Long, periodStart: Long): Int

    @Query("UPDATE budgets SET overNotifiedPeriodStart = :periodStart WHERE categoryId = :categoryId AND (overNotifiedPeriodStart IS NULL OR overNotifiedPeriodStart != :periodStart)")
    suspend fun markOverNotified(categoryId: Long, periodStart: Long): Int

    @Query("DELETE FROM budgets WHERE categoryId = :categoryId")
    suspend fun deleteForCategory(categoryId: Long)
}
