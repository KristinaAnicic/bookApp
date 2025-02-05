package com.example.booksapp.utils

enum class ReadingFormatEnum(val id: Long, val format_name: String) {
    PAPERBACK(1, "Paperback"),
    EBOOK(2, "Ebook"),
    AUDIOBOOK(3, "Audiobook");

    companion object {
        fun getIdByName(formatName: String): Long? {
            return entries.firstOrNull { it.format_name == formatName }?.id
        }
    }
}