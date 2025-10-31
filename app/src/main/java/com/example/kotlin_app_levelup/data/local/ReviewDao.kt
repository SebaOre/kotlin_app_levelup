package com.example.kotlin_app_levelup.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews WHERE productCode = :productCode ORDER BY createdAt DESC")
    fun getByProductCode(productCode: String): Flow<List<ReviewEntity>>

    @Query("SELECT AVG(rating) FROM reviews WHERE productCode = :productCode")
    fun getAverage(productCode: String): Flow<Double?>

    @Insert
    suspend fun insert(entity: ReviewEntity)

    @Query("DELETE FROM reviews WHERE productCode = :productCode")
    suspend fun clearForProduct(productCode: String)
}
