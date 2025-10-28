package com.example.kotlin_app_levelup.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlin_app_levelup.data.local.AppDatabase
import com.example.kotlin_app_levelup.data.local.ProductEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(context: Context) : ViewModel() {

    private val dao = AppDatabase.getDatabase(context).productDao()

    private val _products = MutableStateFlow<List<ProductEntity>>(emptyList())
    val products: StateFlow<List<ProductEntity>> = _products

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            dao.getAllProducts().collect { list ->
                _products.value = list
            }
        }
    }

    fun searchProducts(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                dao.getAllProducts().collect { _products.value = it }
            } else {
                dao.searchProducts("%${query.lowercase()}%").collect { _products.value = it }
            }
        }
    }
    init {
        viewModelScope.launch {
            val count = dao.getCount()
            if (count == 0) {
                dao.insertAll(getDefaultProducts()) // Inserta los productos iniciales
            }
        }
        loadProducts()
    }
    private fun getDefaultProducts(): List<ProductEntity> {
        return listOf(
            ProductEntity(
                code = "AC000",
                name = "PlayStation 5",
                price = 549990,
                imageRes = com.example.kotlin_app_levelup.R.drawable.play51
            ),
            ProductEntity(
                code = "AC001",
                name = "Control Xbox Series X",
                price = 59990,
                imageRes = com.example.kotlin_app_levelup.R.drawable.conxbox1
            ),
            ProductEntity(
                code = "AC002",
                name = "HyperX Cloud II",
                price = 79990,
                imageRes = com.example.kotlin_app_levelup.R.drawable.hyper1
            ),
            ProductEntity(
                code = "CG001",
                name = "PC Gamer ASUS ROG Strix",
                price = 1299990,
                imageRes = com.example.kotlin_app_levelup.R.drawable.gamer1
            )
        )
    }


}
