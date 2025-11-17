package com.example.kotlin_app_levelup.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlin_app_levelup.data.local.AppDatabase
import com.example.kotlin_app_levelup.data.local.ProductEntity
import com.example.kotlin_app_levelup.data.remote.RetrofitInstance
import com.example.kotlin_app_levelup.data.repository.ProductRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(context: Context) : ViewModel() {

    private val dao = AppDatabase.getDatabase(context).productDao()

    private val repository = ProductRepository(
        dao = dao,
        api = RetrofitInstance.api
    )

    private val _products = MutableStateFlow<List<ProductEntity>>(emptyList())
    val products: StateFlow<List<ProductEntity>> = _products.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private var reloadJob: Job? = null

    init {
        // 1. Primero intento actualizar desde la API
        viewModelScope.launch {
            try {
                repository.refreshProducts()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 2. Cargar categorías y productos (ROOM)
        collectCategories()
        reload()
    }

    private fun collectCategories() {
        viewModelScope.launch {
            dao.getAllCategories().collect { _categories.value = it }
        }
    }

    private fun reload() {
        reloadJob?.cancel()
        reloadJob = viewModelScope.launch {
            val q = _query.value.trim().lowercase()
            val cat = _selectedCategory.value

            when {
                cat.isNullOrBlank() && q.isBlank() ->
                    repository.allProducts.collect { _products.value = it }

                cat.isNullOrBlank() && q.isNotBlank() ->
                    repository.searchProducts("%$q%").collect { _products.value = it }

                !cat.isNullOrBlank() && q.isBlank() ->
                    dao.getByCategory(cat).collect { _products.value = it }

                else ->
                    dao.searchInCategory(cat!!, "%$q%").collect { _products.value = it }
            }
        }
    }

    fun searchProducts(query: String) {
        _query.value = query
        reload()
    }

    fun filterByCategory(category: String?) {
        _selectedCategory.value = category
        reload()
    }
}
