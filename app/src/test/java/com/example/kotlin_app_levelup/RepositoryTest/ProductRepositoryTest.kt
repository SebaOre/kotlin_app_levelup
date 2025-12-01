package com.example.kotlin_app_levelup

import com.example.kotlin_app_levelup.data.local.ProductDao
import com.example.kotlin_app_levelup.data.remote.ProductApiService
import com.example.kotlin_app_levelup.data.remote.Products
import com.example.kotlin_app_levelup.data.repository.ProductRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductRepositoryTest {

    private val fakeDao = mockk<ProductDao>(relaxed = true)
    private val fakeApi = mockk<ProductApiService>()
    private val repo = ProductRepository(fakeDao, fakeApi)

    @Test
    fun refreshProducts_insertaProductosCorrectamente() = runTest {

        // 1. Simular datos desde la API
        val fakeDto = listOf(
            Products(
                code = "X123",
                categoria = "Consolas",
                name = "PS5",
                price = 499990,
                year = 2020,
                images = listOf("img1"),
                description = "Consola Sony",
                manufacturer = "Sony",
                distributor = "Retail"
            )
        )

        coEvery { fakeApi.getProducts() } returns fakeDto

        // 2. Ejecutar lógica del repositorio
        repo.refreshProducts()

        // 3. Validar comportamiento correcto
        coVerify { fakeDao.clearAll() }
        coVerify { fakeDao.insertAll(any()) }
    }
}
