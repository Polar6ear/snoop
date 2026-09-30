package com.snoop.expensetracker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.snoop.expensetracker.data.db.entities.FallbackPendingEntity

@Dao
interface FallbackPendingDao {
    companion object {
        /** AUDIT FIX: bounded retry. A notification that fails the AI fallback this many times in
         * a row is left in the table (for manual/debug visibility) but stops being picked up by
         * [getBatch] — a permanently broken backend must not turn into an infinite WorkManager
         * retry loop. */
        const val MAX_ATTEMPTS = 5
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: FallbackPendingEntity): Long

    @Query("SELECT * FROM fallback_pending WHERE attempts < :maxAttempts ORDER BY createdAt ASC LIMIT :limit")
    suspend fun getBatch(limit: Int, maxAttempts: Int = MAX_ATTEMPTS): List<FallbackPendingEntity>

    /** Items parked for manual review use attempts == MAX_ATTEMPTS so WorkManager won't retry them. */
    @Query("SELECT * FROM fallback_pending ORDER BY createdAt DESC")
    fun observeAll(): kotlinx.coroutines.flow.Flow<List<FallbackPendingEntity>>

    @Query("UPDATE fallback_pending SET attempts = :attempts WHERE id = :id")
    suspend fun parkForManualReview(id: Long, attempts: Int = MAX_ATTEMPTS)

    @Query("DELETE FROM fallback_pending WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("UPDATE fallback_pending SET attempts = attempts + 1 WHERE id = :id")
    suspend fun incrementAttempts(id: Long)
}
