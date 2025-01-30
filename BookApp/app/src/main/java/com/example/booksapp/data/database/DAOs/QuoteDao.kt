package com.example.booksapp.data.database.DAOs

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.booksapp.data.database.Entities.BookWithDetails
import com.example.booksapp.data.database.Entities.Quote
import kotlinx.coroutines.flow.Flow

@Dao
interface QuoteDao {
    @Upsert
    suspend fun upsertQuote(quote: Quote)

    @Delete
    suspend fun deleteQuote(quote: Quote)

    @Query("SELECT * FROM quote WHERE bookId = :bookId")
    fun getQuoteByBookId(bookId: Long): Flow<List<Quote>>

    @Query("SELECT * FROM quote")
    fun getAllQuotes(): Flow<List<Quote>>
}