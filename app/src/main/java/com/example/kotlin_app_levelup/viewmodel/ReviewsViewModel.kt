package com.example.kotlin_app_levelup.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kotlin_app_levelup.data.local.AppDatabase
import com.example.kotlin_app_levelup.data.local.ReviewEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

class ReviewsViewModel(
    context: Context,
    private val productCode: String
) : ViewModel() {

    private val dao = AppDatabase.getDatabase(context).reviewDao()

    private val _reviews = MutableStateFlow<List<ReviewEntity>>(emptyList())
    val reviews: StateFlow<List<ReviewEntity>> = _reviews

    val avgRating: StateFlow<Float> =
        _reviews.map { list ->
            if (list.isEmpty()) 0f else list.map { it.rating }.average().toFloat()
        }.stateIn(viewModelScope, SharingStarted.Eagerly, 0f)

    init {
        viewModelScope.launch {
            dao.getByProductCode(productCode).collect { _reviews.value = it }
        }
    }

    fun addReview(author: String, rating: Int, text: String) {
        if (rating !in 1..5 || text.isBlank()) return
        viewModelScope.launch {
            dao.insert(
                ReviewEntity(
                    productCode = productCode,
                    author = author,
                    rating = rating,
                    text = text.trim()
                )
            )
        }
    }

    fun clearAllForThisProduct() {
        viewModelScope.launch { dao.clearForProduct(productCode) }
    }
}

class ReviewsViewModelFactory(
    private val context: Context,
    private val productCode: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ReviewsViewModel(context.applicationContext, productCode) as T
    }
}
