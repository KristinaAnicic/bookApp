package com.example.booksapp

import com.example.booksapp.model.Bestsellers
import com.example.booksapp.model.Book
import com.example.booksapp.model.BookDetail.BookDetail
import com.example.booksapp.model.BookDetail.Item
import com.example.booksapp.model.Results
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {
    @GET("{category}.json")
    suspend fun getBestsellerBooks(
        @Path("category") category: String,
        @Query("api-key") apiKey: String = "N61YXu9PhyGUVhKNHBwjKFadmY3iwtl6"
    ): Response<Bestsellers>

    @GET("combined-print-and-e-book-fiction.json")
    suspend fun getBestsellerFictionBooks(
        @Query("api-key") apiKey: String = "N61YXu9PhyGUVhKNHBwjKFadmY3iwtl6"
    ):Response<Bestsellers>

    @GET("volumes")
    suspend fun getBookByIsbn(
        @Query("q") query: String
    ):Response<BookDetail>

    @GET("volumes/{bookId}")
    suspend fun getBookById(
        @Path("bookId") bookId: String
    ):Response<Item>
}