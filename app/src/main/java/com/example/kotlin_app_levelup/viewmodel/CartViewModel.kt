package com.example.kotlin_app_levelup.viewmodel

import androidx.lifecycle.ViewModel
import com.example.kotlin_app_levelup.data.local.ProductEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class CartItem(
    val product: ProductEntity,
    var quantity: Int = 1
)

class CartViewModel : ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    // Agregar al carrito
    fun addToCart(product: ProductEntity) {
        _cartItems.update { current ->
            val existing = current.find { it.product.code == product.code }
            if (existing != null) {
                current.map {
                    if (it.product.code == product.code)
                        it.copy(quantity = it.quantity + 1)
                    else it
                }
            } else {
                current + CartItem(product, 1)
            }
        }
    }

    // Quitar producto
    fun removeItem(productCode: String) {
        _cartItems.update { current -> current.filterNot { it.product.code == productCode } }
    }

    // Cambiar cantidad
    fun updateQuantity(productCode: String, newQty: Int) {
        _cartItems.update { current ->
            current.map {
                if (it.product.code == productCode) it.copy(quantity = newQty.coerceAtLeast(1)) else it
            }
        }
    }

    // Vaciar carrito
    fun clearCart() {
        _cartItems.value = emptyList()
    }

    // Calcular total
    fun getTotal(): Int {
        return _cartItems.value.sumOf { it.product.price * it.quantity }
    }
}
