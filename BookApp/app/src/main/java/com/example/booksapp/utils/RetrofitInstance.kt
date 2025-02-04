package com.example.booksapp.utils

import com.example.booksapp.ApiInterface
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    val api_Bestsellers : ApiInterface by lazy {
        Retrofit.Builder()
            .baseUrl(Utils.BASE_Bestsellers)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)
    }

    val api_Books : ApiInterface by lazy {
        Retrofit.Builder()
            .baseUrl(Utils.BASE_BookList)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)
    }
}