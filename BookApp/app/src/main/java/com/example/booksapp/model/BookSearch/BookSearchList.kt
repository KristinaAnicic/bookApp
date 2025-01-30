package com.example.booksapp.model.BookSearch

data class BookSearchList(
    val items: List<Item>,
    val kind: String,
    val totalItems: Int
)