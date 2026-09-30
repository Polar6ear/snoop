package com.snoop.expensetracker.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.snoop.expensetracker.data.db.entities.BalanceAnchorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BalanceDao {
    @Query("SELECT * FROM balance_anchor WHERE id = 1 LIMIT 1")
    fun observeAnchor(): Flow<BalanceAnchorEntity?>

    @Query("SELECT * FROM balance_anchor WHERE id = 1 LIMIT 1")
    suspend fun getAnchorOnce(): BalanceAnchorEntity?

    @Query("DELETE FROM balance_anchor")
    suspend fun clear()

    @Upsert
    suspend fun upsertAnchor(anchor: BalanceAnchorEntity)
}
