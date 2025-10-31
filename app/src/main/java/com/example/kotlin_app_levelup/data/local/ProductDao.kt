package com.example.kotlin_app_levelup.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY id ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    // 🔎 Búsqueda por nombre/descripcion/categoria (case-insensitive)
    @Query("""
        SELECT * FROM products
        WHERE LOWER(name) LIKE :pattern OR LOWER(description) LIKE :pattern OR LOWER(categoria) LIKE :pattern
        ORDER BY id ASC
    """)
    fun searchProducts(pattern: String): Flow<List<ProductEntity>>

    // 🏷️ Todas las categorías distintas
    @Query("SELECT DISTINCT categoria FROM products ORDER BY categoria ASC")
    fun getAllCategories(): Flow<List<String>>

    // 🏷️ Filtrar por categoría exacta
    @Query("SELECT * FROM products WHERE categoria = :category ORDER BY id ASC")
    fun getByCategory(category: String): Flow<List<ProductEntity>>

    // 🔎 + 🏷️ Búsqueda dentro de una categoría
    @Query("""
        SELECT * FROM products
        WHERE categoria = :category
          AND (LOWER(name) LIKE :pattern OR LOWER(description) LIKE :pattern)
        ORDER BY id ASC
    """)
    fun searchInCategory(category: String, pattern: String): Flow<List<ProductEntity>>

    @Query("SELECT COUNT(*) FROM products")
    suspend fun getCount(): Int

    @Query("SELECT * FROM products WHERE code = :code LIMIT 1")
    suspend fun getByCode(code: String): ProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)
}
