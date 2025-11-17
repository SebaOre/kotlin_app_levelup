package com.example.kotlin_app_levelup.data.remote

data class Products (
    val code: String,
    val categoria: String,
    val name: String,
    val price: Int,
    val year: Int,
    val images: List<String>,
    val description: String,
    val manufacturer: String,
    val distributor: String
)