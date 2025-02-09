package com.example.booksapp.data.database.Entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = ReadingStatus::class,
            parentColumns = ["statusId"],
            childColumns = ["readingStatusId"]
        ),
        ForeignKey(
            entity = ReadingFormat::class,
            parentColumns = ["formatId"],
            childColumns = ["readingFormatId"]
        )
    ],
    indices = [Index("readingStatusId"), Index("readingFormatId")]
)
data class Book(
    @PrimaryKey(autoGenerate = true)
    val bookId: Long = 0,
    val title: String,
    val author: String,
    val coverImage: String,
    val description: String? = null,
    val publishDate: String? = null,
    val publisher: String? = null,
    val numberOfPages: Int,
    val numberOfChapters: Int? = null,
    var readingStatusId: Long,
    var readingFormatId: Long,
    var startDate: Date? = null,
    var endDate: Date? = null,
    var readPagesCount: Int? = 0,
    var readChaptersCount: Int? = 0,
    val impressions: String? = null,
    val pauseNotes: String? = null,
    var lastReadDate: Date? = null,
    var favorite: Boolean = false,
    val hasBook: Boolean = false,
    val trackChapters: Boolean = false,
    val googleApiId: String? = null
)
