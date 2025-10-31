package com.example.kotlin_app_levelup.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "purchase_items",
    indices = [Index(value = ["purchaseId"])]
)
data class PurchaseItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val purchaseId: Long,            // FK a purchases.id
    val productCode: String,
    val productName: String,
    val price: Int,
    val quantity: Int,
    val imageRes: Int
)
