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
                description = "Consola de video de ultima generacion",
                imageRes = com.example.kotlin_app_levelup.R.drawable.play51
            ),
            ProductEntity(
                code = "AC001",
                name = "Control Xbox Series X",
                price = 59990,
                description = "Control de consola de Xbox Series X ",
                imageRes = com.example.kotlin_app_levelup.R.drawable.conxbox1
            ),
            ProductEntity(
                code = "AC002",
                name = "HyperX Cloud II",
                price = 79990,
                description = "Audifonos Gamer de alta calidad",
                imageRes = com.example.kotlin_app_levelup.R.drawable.hyper1
            ),
            ProductEntity(
                code = "CG001",
                name = "PC Gamer ASUS ROG Strix",
                price = 1250990,
                description = "Pc gamer con componentes de ultima generación",
                imageRes = com.example.kotlin_app_levelup.R.drawable.gamer1
            ),
            ProductEntity(
                code = "BA001",
                name = "Catan",
                price = 29990,
                description = "Juego de mesa para disfrutar con amigos",
                imageRes = com.example.kotlin_app_levelup.R.drawable.catan_1
            ),
            ProductEntity(
                code = "BA002",
                name = "Carcassonne",
                price = 24990,
                description = "Juego de mesa para disfrutar con amigos",
                imageRes = com.example.kotlin_app_levelup.R.drawable.carcassonne1
            ),
            ProductEntity(
                code = "CA001",
                name = "Secretlab Titan",
                price = 349990,
                description = "Silla Gamer de alta calidad",
                imageRes = com.example.kotlin_app_levelup.R.drawable.silla1
            ),
            ProductEntity(
                code = "CA003",
                name = "Logitech G502 HERO",
                price = 49990,
                description = "Mouse de alta calidad para juegos y tareas varias",
                imageRes = com.example.kotlin_app_levelup.R.drawable.mousel1
            ),
            ProductEntity(
                code = "CA002",
                name = "Razer Goliathus Chroma",
                price = 29990,
                description = "Mouse pad Gamer presicion y calidad",
                imageRes = com.example.kotlin_app_levelup.R.drawable.mpad1
            ),
            ProductEntity(
                code = "DA001",
                name = "Polera 'Level-Up'",
                price = 14990,
                description = "Polera personalizada de LevelUp",
                imageRes = com.example.kotlin_app_levelup.R.drawable.lvlup1
            ),
            ProductEntity(
                code = "DA002",
                name = "Poleron Gamer",
                price = 19990,
                description = "Polerones personlizado de videoJuegos",
                imageRes = com.example.kotlin_app_levelup.R.drawable.perso1
            )
        )
    }
}
