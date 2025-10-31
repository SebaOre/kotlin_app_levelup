package com.example.kotlin_app_levelup.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "purchases")
data class PurchaseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val buyerName: String,           // nombre del usuario (o alias) al momento de la compra
    val total: Int,                  // total en CLP
    val createdAt: Long = System.currentTimeMillis(),
    val deliveryAddress: String
)
