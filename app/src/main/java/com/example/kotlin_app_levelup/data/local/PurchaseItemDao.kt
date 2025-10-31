package com.example.kotlin_app_levelup.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PurchaseItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<PurchaseItemEntity>)

    @Query("SELECT * FROM purchase_items WHERE purchaseId = :purchaseId")
    suspend fun getByPurchaseId(purchaseId: Long): List<PurchaseItemEntity>

    // 🔧 Alias útil: misma función, pero con nombre más corto
    @Query("SELECT * FROM purchase_items WHERE purchaseId = :id")
    suspend fun getByPurchase(id: Long): List<PurchaseItemEntity>
}
