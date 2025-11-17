package com.example.kotlin_app_levelup.data.remote

import retrofit2.http.GET

interface ProductApiService {

    @GET("api/products") // <-- ruta de tu API real
    suspend fun getProducts(): List<Products>
}
