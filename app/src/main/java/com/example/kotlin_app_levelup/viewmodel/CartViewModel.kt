package com.example.kotlin_app_levelup.viewmodel

import androidx.lifecycle.ViewModel
import com.example.kotlin_app_levelup.data.local.ProductEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class CartViewModel : ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    /* ========= API ========= */

    // 👍 addToCart que pediste (por defecto suma 1)
    fun addToCart(product: ProductEntity, qty: Int = 1) = addItem(product, qty)

    // (opcional) si en algún lado construyes CartItem directo
    fun addToCart(item: CartItem) = addItem(item.product, item.quantity)

    /* ======== Interno ======== */

    private fun addItem(product: ProductEntity, qty: Int = 1) {
        _cartItems.update { list ->
            val idx = list.indexOfFirst { it.product.code == product.code }
            if (idx >= 0) {
                val cur = list[idx]
                list.toMutableList().also { it[idx] = cur.copy(quantity = cur.quantity + qty) }
            } else {
                list + CartItem(product, qty)
            }
        }
    }

    fun updateQuantity(code: String, newQty: Int) {
        _cartItems.update { list ->
            val idx = list.indexOfFirst { it.product.code == code }
            if (idx < 0) list
            else if (newQty <= 0) list.toMutableList().also { it.removeAt(idx) }
            else list.toMutableList().also { it[idx] = list[idx].copy(quantity = newQty) }
        }
    }

    fun removeItem(code: String) {
        _cartItems.update { it.filterNot { ci -> ci.product.code == code } }
    }
}
