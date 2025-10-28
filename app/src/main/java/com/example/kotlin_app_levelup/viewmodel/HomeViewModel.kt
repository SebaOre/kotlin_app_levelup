package com.example.kotlin_app_levelup.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlin_app_levelup.data.local.AppDatabase
import com.example.kotlin_app_levelup.data.local.ProductEntity
import com.example.kotlin_app_levelup.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(context: Context) : ViewModel() {

    private val repository: ProductRepository

    private val _products = MutableStateFlow<List<ProductEntity>>(emptyList())
    val products: StateFlow<List<ProductEntity>> = _products

    init {
        val dao = AppDatabase.getDatabase(context).productDao()
        repository = ProductRepository(dao)
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            repository.allProducts.collect { list ->
                if (list.isEmpty()) {
                    seedInitialData()
                } else {
                    _products.value = list
                }
            }
        }
    }

    private suspend fun seedInitialData() {
        val sampleProducts = listOf(
            ProductEntity(name = "PlayStation 5", price = 549990, imageRes = com.example.kotlin_app_levelup.R.drawable.play5),
            ProductEntity(name = "Control Xbox", price = 59990, imageRes = com.example.kotlin_app_levelup.R.drawable.controlxbox),
            ProductEntity(name = "PC Gamer ASUS", price = 1299990, imageRes = com.example.kotlin_app_levelup.R.drawable.pc_asus),
            ProductEntity(name = "Audífonos HyperX", price = 79990, imageRes = com.example.kotlin_app_levelup.R.drawable.hyperx),
            ProductEntity(name = "Mouse Logitech G502", price = 49990, imageRes = com.example.kotlin_app_levelup.R.drawable.mouse)
        )
        repository.insertProducts(sampleProducts)
    }

    fun searchProducts(query: String) {
        viewModelScope.launch {
            repository.searchProducts(query).collect {
                _products.value = it
            }
        }
    }
}
