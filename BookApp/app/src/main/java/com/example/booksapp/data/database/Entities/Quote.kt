package com.example.booksapp.data.database.Entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Book::class,
            parentColumns = ["bookId"],
            childColumns = ["bookId"]
        )
    ],
    indices = [
        Index("bookId")
    ]
)
data class Quote(
    @PrimaryKey(autoGenerate = true)
    val quoteId: Long = 0,
    val bookId: Long,
    val quote: String,
    val page: Int? = null,
    val chapter: Int? = null,
    val additionalNotes: String? = null
)
