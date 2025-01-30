package com.example.booksapp.utils

import com.example.booksapp.model.Book

object BestsellerBookList {
    var fictionBooks: List<Book> = emptyList()
    var nonFictionBooks: List<Book> = emptyList()
    var adviceBooks: List<Book> = emptyList()
    var youngAdultBooks: List<Book> = emptyList()

    var areBooksLoaded: Boolean = false
}