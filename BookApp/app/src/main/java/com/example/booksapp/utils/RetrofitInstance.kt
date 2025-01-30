package com.example.booksapp.utils

import com.example.booksapp.ApiInterface
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(RetryInterceptor()) // Dodaj retry interceptor
        .build()

    val api_Bestsellers : ApiInterface by lazy {
        Retrofit.Builder()
            .baseUrl(Utils.BASE_Bestsellers)
            //.client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)
    }

    val api_Books : ApiInterface by lazy {
        Retrofit.Builder()
            .baseUrl(Utils.BASE_BookList)
            //.client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)
    }
}