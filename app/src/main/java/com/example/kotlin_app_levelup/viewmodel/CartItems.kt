package com.example.kotlin_app_levelup.viewmodel

import com.example.kotlin_app_levelup.data.local.ProductEntity

data class CartItem(
    val product: ProductEntity,
    val quantity: Int
)
