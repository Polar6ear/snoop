package com.snoop.expensetracker.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.snoop.expensetracker.data.db.entities.ParserVersionEntity

@Dao
interface ParserVersionDao {

    @Upsert
    suspend fun upsert(entity: ParserVersionEntity)

    @Query("SELECT * FROM parser_versions WHERE bankKey = :bankKey")
    suspend fun getByBankKey(bankKey: String): ParserVersionEntity?

    @Query("SELECT * FROM parser_versions")
    suspend fun getAll(): List<ParserVersionEntity>
}
