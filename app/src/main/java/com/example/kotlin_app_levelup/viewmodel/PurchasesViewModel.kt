package com.example.kotlin_app_levelup.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.example.kotlin_app_levelup.data.local.AppDatabase
import com.example.kotlin_app_levelup.data.local.PurchaseEntity
import com.example.kotlin_app_levelup.data.local.PurchaseItemEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PurchasesViewModel(
    context: Context
) : ViewModel() {

    private val db = AppDatabase.getDatabase(context.applicationContext)

    private val _purchases = MutableStateFlow<List<PurchaseEntity>>(emptyList())
    val purchases: StateFlow<List<PurchaseEntity>> = _purchases.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _purchases.value = db.purchaseDao().getAll()
        }
    }

    /**
     * Guarda la compra completa (encabezado + items) y refresca la lista.
     * deliveryAddress es NO nulo porque tu entidad lo espera así.
     */
    fun placeOrder(
        cartItems: List<CartItem>,
        computedTotal: Int,
        deliveryAddress: String,
        buyerName: String = "Usuario"
    ) {
        if (cartItems.isEmpty()) return

        viewModelScope.launch {
            db.withTransaction {
                val purchaseId = db.purchaseDao().insert(
                    PurchaseEntity(
                        buyerName = buyerName,
                        total = computedTotal,
                        deliveryAddress = deliveryAddress
                    )
                )

                val items = cartItems.map { ci ->
                    PurchaseItemEntity(
                        purchaseId = purchaseId,
                        productCode = ci.product.code,
                        productName = ci.product.name,
                        price = ci.product.price,
                        quantity = ci.quantity,
                        imageRes = ci.product.imageRes
                    )
                }
                db.purchaseItemDao().insertAll(items)
            }
            _purchases.value = db.purchaseDao().getAll()
        }
    }
}

class PurchasesViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PurchasesViewModel(context) as T
    }
}
