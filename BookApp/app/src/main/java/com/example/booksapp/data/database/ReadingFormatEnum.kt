package com.example.booksapp.data.database

enum class ReadingFormatEnum(val id: Long, val format_name: String) {
    PAPERBACK(1, "Paperback"),
    EBOOK(2, "Ebook"),
    AUDIOBOOK(3, "Audiobook")
}