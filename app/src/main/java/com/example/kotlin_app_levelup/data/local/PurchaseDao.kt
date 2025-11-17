package com.example.kotlin_app_levelup.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PurchaseDao {
    @Insert
    suspend fun insert(purchase: PurchaseEntity): Long

    @Query("SELECT * FROM purchases ORDER BY createdAt DESC")
    suspend fun getAll(): List<PurchaseEntity>

    @Query("DELETE FROM purchases")
    suspend fun clearAll()

}
