package com.example.kotlin_app_levelup.data.repository

import com.example.kotlin_app_levelup.data.local.ProductDao
import com.example.kotlin_app_levelup.data.local.ProductEntity
import kotlinx.coroutines.flow.Flow

class ProductRepository(private val productDao: ProductDao) {

    val allProducts: Flow<List<ProductEntity>> = productDao.getAllProducts()

    fun searchProducts(query: String): Flow<List<ProductEntity>> {
        return productDao.searchProducts(query)
    }

    suspend fun insertProducts(products: List<ProductEntity>) {
        productDao.insertAll(products)
    }
}
