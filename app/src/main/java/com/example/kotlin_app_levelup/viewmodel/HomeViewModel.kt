package com.example.kotlin_app_levelup.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlin_app_levelup.R
import com.example.kotlin_app_levelup.data.local.AppDatabase
import com.example.kotlin_app_levelup.data.local.ProductEntity
import com.example.kotlin_app_levelup.data.repository.ProductRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(context: Context) : ViewModel() {

    private val repository: ProductRepository
    private val dao = AppDatabase.getDatabase(context).productDao()

    private val _products = MutableStateFlow<List<ProductEntity>>(emptyList())
    val products: StateFlow<List<ProductEntity>> = _products.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private var loadJob: Job? = null

    init {
        // Inicializamos el repositorio con el DAO
        repository = ProductRepository(dao)

        viewModelScope.launch {
            val count = dao.getCount()
            if (count == 0) {
                // Insertamos productos usando el Repository
                repository.insertProducts(getDefaultProducts())
            }
        }

        // Cargar categorías y productos
        collectCategories()
        reload()
    }

    private fun collectCategories() {
        viewModelScope.launch {
            dao.getAllCategories().collect { _categories.value = it }
        }
    }

    private fun reload() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            val q = _query.value.trim().lowercase()
            val cat = _selectedCategory.value
            when {
                cat.isNullOrBlank() && q.isBlank() -> {
                    // Leemos desde el Repository
                    repository.allProducts.collect { _products.value = it }
                }
                cat.isNullOrBlank() && q.isNotBlank() -> {
                    repository.searchProducts("%$q%").collect { _products.value = it }
                }
                !cat.isNullOrBlank() && q.isBlank() -> {
                    dao.getByCategory(cat!!).collect { _products.value = it }
                }
                else -> {
                    dao.searchInCategory(cat!!, "%$q%").collect { _products.value = it }
                }
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

    // Productos iniciales
    private fun getDefaultProducts(): List<ProductEntity> {
        return listOf(
            ProductEntity(
                code = "AC000",
                name = "PlayStation 5",
                price = 549990,
                description = "Consola de video de ultima generacion",
                categoria = "Consolas",
                imageRes = R.drawable.play51,
                imageCarrusel = listOf(
                    R.drawable.play51, R.drawable.play52, R.drawable.play53, R.drawable.play54, R.drawable.play55
                )
            ),
            ProductEntity(
                code = "AC001",
                name = "Control Xbox Series X",
                price = 59990,
                description = "Control de consola de Xbox Series X",
                categoria = "Consolas",
                imageRes = R.drawable.conxbox1,
                imageCarrusel = listOf(
                    R.drawable.conxbox1, R.drawable.conxbox2, R.drawable.conxbox3, R.drawable.conxbox4, R.drawable.conxbox5
                )
            ),
            ProductEntity(
                code = "AC002",
                name = "HyperX Cloud II",
                price = 79990,
                description = "Audifonos Gamer de alta calidad",
                categoria = "Sillas",
                imageRes = R.drawable.hyper1,
                imageCarrusel = listOf(
                    R.drawable.hyper1, R.drawable.hyper2, R.drawable.hyper3, R.drawable.hyper4, R.drawable.hyper5
                )
            ),
            ProductEntity(
                code = "CG001",
                name = "PC Gamer ASUS ROG Strix",
                price = 1250990,
                description = "Pc gamer con componentes de ultima generación",
                categoria = "Computadores",
                imageRes = R.drawable.gamer1,
                imageCarrusel = listOf(
                    R.drawable.gamer1, R.drawable.gamer2, R.drawable.gamer3, R.drawable.gamer4, R.drawable.gamer5
                )
            ),
            ProductEntity(
                code = "BA001",
                name = "Catan",
                price = 29990,
                description = "Juego de mesa para disfrutar con amigos",
                categoria = "Juegos de mesa",
                imageRes = R.drawable.catan_1,
                imageCarrusel = listOf(
                    R.drawable.catan_1, R.drawable.catan2, R.drawable.catan3, R.drawable.catan4, R.drawable.catan5
                )
            ),
            ProductEntity(
                code = "BA002",
                name = "Carcassonne",
                price = 24990,
                description = "Juego de mesa para disfrutar con amigos",
                categoria = "Juegos de mesa",
                imageRes = R.drawable.carcassonne1,
                imageCarrusel = listOf(
                    R.drawable.carcassonne1, R.drawable.carcassonne2, R.drawable.carcassonne3, R.drawable.carcassonne4, R.drawable.carcassonne5
                )
            ),
            ProductEntity(
                code = "CA001",
                name = "Secretlab Titan",
                price = 349990,
                description = "Silla Gamer de alta calidad",
                categoria = "Silla Gamer",
                imageRes = R.drawable.silla1,
                imageCarrusel = listOf(
                    R.drawable.silla1, R.drawable.silla2, R.drawable.silla3, R.drawable.silla4, R.drawable.silla5
                )
            ),
            ProductEntity(
                code = "CA003",
                name = "Logitech G502 HERO",
                price = 49990,
                description = "Mouse de alta calidad para juegos y tareas varias",
                categoria = "Mouse",
                imageRes = R.drawable.mousel1,
                imageCarrusel = listOf(
                    R.drawable.mousel1, R.drawable.mousel2, R.drawable.mousel3, R.drawable.mousel4, R.drawable.mousel5
                )
            ),
            ProductEntity(
                code = "CA002",
                name = "Razer Goliathus Chroma",
                price = 29990,
                description = "Mouse pad Gamer precisión y calidad",
                categoria = "Mouse",
                imageRes = R.drawable.mpad1,
                imageCarrusel = listOf(
                    R.drawable.mpad1, R.drawable.mpad2, R.drawable.mpad3, R.drawable.mpad4, R.drawable.mpad5
                )
            ),
            ProductEntity(
                code = "DA001",
                name = "Polera 'Level-Up'",
                price = 14990,
                description = "Polera personalizada de LevelUp",
                categoria = "Poleras",
                imageRes = R.drawable.lvlup1,
                imageCarrusel = listOf(
                    R.drawable.lvlup1, R.drawable.lvlup2, R.drawable.lvlup3, R.drawable.lvlup4, R.drawable.lvlup5
                )
            ),
            ProductEntity(
                code = "DA002",
                name = "Poleron Gamer",
                price = 19990,
                description = "Polerones personalizados de videojuegos",
                categoria = "Poleras",
                imageRes = R.drawable.perso1,
                imageCarrusel = listOf(
                    R.drawable.perso1, R.drawable.perso2, R.drawable.perso3, R.drawable.perso4, R.drawable.perso5
                )
            )
        )
    }
}
