package com.snoop.expensetracker.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.snoop.expensetracker.data.db.entities.MerchantMappingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MerchantMappingDao {
    @Query("SELECT * FROM merchant_mappings WHERE merchantKey = :merchantKey")
    suspend fun getByMerchantKey(merchantKey: String): MerchantMappingEntity?

    @Query("SELECT * FROM merchant_mappings ORDER BY learnedAt DESC")
    fun observeAll(): Flow<List<MerchantMappingEntity>>

    @Upsert
    suspend fun upsert(mapping: MerchantMappingEntity)

    @Query("DELETE FROM merchant_mappings")
    suspend fun deleteAll()

    @Query("SELECT * FROM merchant_mappings ORDER BY merchantKey")
    suspend fun getAllOnce(): List<MerchantMappingEntity>

    @Upsert
    suspend fun upsertAll(mappings: List<MerchantMappingEntity>)

    @Query("DELETE FROM merchant_mappings WHERE merchantKey = :merchantKey")
    suspend fun delete(merchantKey: String)
}
