package com.example.kotlin_app_levelup.data.repository

import com.example.kotlin_app_levelup.data.local.ProductDao
import com.example.kotlin_app_levelup.data.local.ProductEntity
import com.example.kotlin_app_levelup.data.remote.ProductApiService
import kotlinx.coroutines.flow.Flow

class ProductRepository(
    private val dao: ProductDao,
    private val api: ProductApiService
) {

    val allProducts: Flow<List<ProductEntity>> = dao.getAllProducts()

    fun searchProducts(query: String): Flow<List<ProductEntity>> {
        return dao.searchProducts(query)
    }

    suspend fun refreshProducts() {
        val remoteProducts = api.getProducts()

        val entities = remoteProducts.map { dto ->
            ProductEntity(
                code = dto.code,
                categoria = dto.categoria,
                name = dto.name,
                price = dto.price,
                year = dto.year,
                image = dto.images.firstOrNull() ?: "",
                imageCarrusel = dto.images,
                description = dto.description,
                manufacturer = dto.manufacturer,
                distributor = dto.distributor
            )
        }

        dao.clearAll()
        dao.insertAll(entities)
    }
}
