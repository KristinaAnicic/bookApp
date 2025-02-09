package com.example.booksapp.data.database.DAOs

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.booksapp.data.database.Entities.Book
import com.example.booksapp.data.database.Entities.BookWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Upsert
    suspend fun upsertBook(book: Book)

    @Delete
    suspend fun deleteBook(book: Book)

    @Query("SELECT * FROM book order by bookId DESC")
    fun getBookList(): Flow<List<Book>>

    @Transaction
    @Query("SELECT * FROM book WHERE bookId = :id")
    fun getBookById(id: Long): Flow<BookWithDetails>

    @Transaction
    @Query("SELECT * FROM book WHERE googleApiId = :id")
    fun getBookByGoogleApiId(id: String): Flow<BookWithDetails>

    @Query("SELECT * FROM book WHERE readingStatusId = :statusId ORDER BY bookId DESC")
    fun getBooksByStatus(statusId: Long): Flow<List<Book>>

    @Query("SELECT * FROM book WHERE readingStatusId IN (:statusIds) ORDER BY readingStatusId ASC, lastReadDate DESC, bookId DESC")
    fun getBooksByStatuses(statusIds: List<Long>): Flow<List<Book>>

    @Query("SELECT * FROM book WHERE favorite = 1 order by bookId DESC")
    fun getFavoriteBookList(): Flow<List<Book>>
}