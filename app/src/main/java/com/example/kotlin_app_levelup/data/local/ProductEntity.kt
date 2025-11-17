package com.example.kotlin_app_levelup.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val code: String,
    val categoria: String,
    val name: String,
    val price: Int,
    val year: Int,
    val image: String,
    val imageCarrusel: List<String>,
    val description: String,
    val manufacturer: String,
    val distributor: String
)
