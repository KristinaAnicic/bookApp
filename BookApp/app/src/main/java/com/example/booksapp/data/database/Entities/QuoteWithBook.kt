package com.example.booksapp.data.database.Entities

import androidx.room.Embedded
import androidx.room.Relation

data class QuoteWithBook(
    @Embedded val quote: Quote,
    @Relation(
        parentColumn = "bookId",
        entityColumn = "bookId"
    )
    val book: Book
)
