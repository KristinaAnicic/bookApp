package com.example.booksapp.model.BookDetail

data class BookDetail(
    val items: List<Item>,
    val kind: String,
    val totalItems: Int
)