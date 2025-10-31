package com.example.kotlin_app_levelup.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val code: String,
    val name: String,
    val categoria: String,
    val price: Int,
    val description: String,
    val imageRes: Int,
    val imageCarrusel: List<Int> = emptyList()
)
