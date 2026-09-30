package com.snoop.expensetracker.data.db.dao

import com.snoop.expensetracker.data.db.entities.TransactionDirection
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.snoop.expensetracker.data.db.entities.TransactionEntity
import com.snoop.expensetracker.data.db.entities.TransactionStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(transaction: TransactionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<TransactionEntity>)

    @Query("DELETE FROM transactions")
    suspend fun deleteAll()

    @Query("SELECT * FROM transactions ORDER BY id")
    suspend fun getAllOnce(): List<TransactionEntity>

    @Query("SELECT id FROM transactions WHERE dedupeKey = :dedupeKey LIMIT 1")
    suspend fun findIdByDedupeKey(dedupeKey: String): Long?

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE status = :status AND deletedAt IS NULL ORDER BY createdAt DESC")
    fun observeByStatus(status: TransactionStatus): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE deletedAt IS NULL ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE deletedAt IS NOT NULL ORDER BY createdAt DESC")
    fun observeDeleted(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    fun observeById(id: Long): Flow<TransactionEntity?>

    @Query("""
        SELECT * FROM transactions
        WHERE deletedAt IS NULL
          AND (:direction IS NULL OR direction = :direction)
          AND (:query = '' OR merchantRaw LIKE '%' || :query || '%' OR merchantResolved LIKE '%' || :query || '%')
        ORDER BY createdAt DESC
    """)
    fun observeFiltered(direction: TransactionDirection?, query: String): Flow<List<TransactionEntity>>

    @Query("""
        SELECT COALESCE(SUM(amountMinor),0) FROM transactions
        WHERE deletedAt IS NULL AND status = 'CONFIRMED' AND direction = 'DEBIT' AND createdAt >= :start
    """)
    fun observeExpenseSince(start: Long): Flow<Long>

    @Query("""
        SELECT COALESCE(SUM(amountMinor),0) FROM transactions
        WHERE deletedAt IS NULL AND status = 'CONFIRMED' AND direction = 'CREDIT' AND createdAt >= :start
    """)
    fun observeIncomeSince(start: Long): Flow<Long>

    @Query("""
        SELECT COALESCE(SUM(amountMinor),0) FROM transactions
        WHERE deletedAt IS NULL AND status = 'CONFIRMED' AND direction = 'DEBIT' AND createdAt >= :start AND createdAt < :end
    """)
    fun observeExpenseBetween(start: Long, end: Long): Flow<Long>

    @Query("SELECT * FROM transactions WHERE deletedAt IS NULL ORDER BY createdAt DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<TransactionEntity>>

    @Query("""
        SELECT COALESCE(
            SUM(CASE
                WHEN direction = 'CREDIT' AND status = 'CONFIRMED' THEN amountMinor
                WHEN direction = 'DEBIT' AND status = 'CONFIRMED' THEN -amountMinor
                ELSE 0
            END),
            0
        )
        FROM transactions
        WHERE deletedAt IS NULL AND createdAt >= :since
    """)
    fun observeNetChangeSince(since: Long): Flow<Long>

    @Query("UPDATE transactions SET deletedAt = :at WHERE id = :id")
    suspend fun softDelete(id: Long, at: Long = System.currentTimeMillis())

    @Query("UPDATE transactions SET deletedAt = NULL WHERE id = :id")
    suspend fun restore(id: Long)

    @Query("""
        UPDATE transactions SET amountMinor = :amountMinor, direction = :direction,
        merchantRaw = :merchant, merchantResolved = :merchant, categoryId = :categoryId, note = :note, suggestedCategory = NULL, suggestedCategoryConfidence = 0
        WHERE id = :id AND deletedAt IS NULL
    """)
    suspend fun updateUserFields(id: Long, amountMinor: Long, direction: TransactionDirection, merchant: String, categoryId: Long, note: String?)


    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: Long): TransactionEntity?

    /**
     * Confirms a pending transaction: attaches the chosen category, resolves the merchant
     * display name, and flips status to CONFIRMED. Called after the user taps a category
     * on the fallback popup (spec §5).
     */
    @Query(
        """
        UPDATE transactions
        SET categoryId = :categoryId, merchantResolved = :merchantResolved, status = 'CONFIRMED', suggestedCategory = NULL, suggestedCategoryConfidence = 0
        WHERE id = :id
        """
    )
    suspend fun confirm(id: Long, categoryId: Long, merchantResolved: String)

    /**
     * Sum of DEBIT spend in a category within [periodStart, now), used by the budget engine
     * to check "is this category's monthly spend near/over its limit?" (spec §5).
     * Only CONFIRMED transactions count toward budget usage — a still-pending guess shouldn't
     * trigger an overspend warning.
     */
    @Query(
        """
        SELECT COALESCE(SUM(amountMinor), 0) FROM transactions
        WHERE categoryId = :categoryId
          AND direction = 'DEBIT'
          AND status = 'CONFIRMED'
          AND createdAt >= :periodStart
        """
    )
    suspend fun sumDebitForCategorySince(categoryId: Long, periodStart: Long): Long

    @Query("SELECT COUNT(*) FROM transactions WHERE status = 'PENDING' AND deletedAt IS NULL")
    fun observePendingCount(): Flow<Int>

    /**
     * Same aggregation as [sumDebitForCategorySince] but grouped across all categories and as a
     * Flow, so the budgets screen (Chunk 8) updates live as transactions are confirmed — Room
     * auto-invalidates this Flow on any write to the transactions table, `periodStart` doesn't
     * need to be re-passed manually for that to work.
     */
    @Query(
        """
        SELECT categoryId, COALESCE(SUM(amountMinor), 0) AS spentMinor FROM transactions
        WHERE direction = 'DEBIT' AND status = 'CONFIRMED' AND createdAt >= :periodStart AND categoryId IS NOT NULL
        GROUP BY categoryId
        """
    )
    fun observeSpendByCategorySince(periodStart: Long): Flow<List<CategorySpend>>
}

data class CategorySpend(
    val categoryId: Long,
    val spentMinor: Long
)
