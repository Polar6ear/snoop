package com.snoop.expensetracker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.snoop.expensetracker.data.db.entities.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun replaceAll(categories: List<CategoryEntity>)

    @Query("DELETE FROM categories")
    suspend fun deleteAll()

    @Query("SELECT * FROM categories ORDER BY id")
    suspend fun getAllOnce(): List<CategoryEntity>

    @Insert
    suspend fun insert(category: CategoryEntity): Long

    @Query("SELECT * FROM categories ORDER BY isCustom ASC, name ASC")
    fun observeAll(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getById(id: Long): CategoryEntity?

    @Query("SELECT id FROM categories WHERE name = :name LIMIT 1")
    suspend fun getIdByName(name: String): Long?

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun count(): Int

    @Query("DELETE FROM categories WHERE id = :id AND isCustom = 1")
    suspend fun deleteCustom(id: Long)
}
