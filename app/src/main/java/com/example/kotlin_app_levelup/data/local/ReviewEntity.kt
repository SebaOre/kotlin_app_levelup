package com.example.kotlin_app_levelup.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reviews",
    indices = [Index(value = ["productCode"])]
)
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productCode: String,   // code del ProductEntity
    val author: String,        // "Anónimo" o userName
    val rating: Int,           // 1..5
    val text: String,
    val createdAt: Long = System.currentTimeMillis()
)
