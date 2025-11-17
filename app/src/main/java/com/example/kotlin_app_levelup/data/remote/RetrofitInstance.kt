package com.example.kotlin_app_levelup.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    val api: ProductApiService by lazy {
        Retrofit.Builder()
            .baseUrl(" https://agenda-minor-truly-apart.trycloudflare.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProductApiService::class.java)
    }
}
