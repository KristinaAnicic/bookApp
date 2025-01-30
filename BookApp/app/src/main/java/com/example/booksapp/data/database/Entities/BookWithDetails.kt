package com.example.booksapp.data.database.Entities

import androidx.room.Embedded
import androidx.room.Relation

data class BookWithDetails(
    @Embedded val book: Book,
    @Relation(
        parentColumn = "readingStatusId",
        entityColumn = "statusId"
    )
    val status: ReadingStatus? = null,
    @Relation(
        parentColumn = "readingFormatId",
        entityColumn = "formatId"
    )
    val format: ReadingFormat? = null
)
