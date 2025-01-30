package com.example.booksapp.model

data class Bestsellers(
    val copyright: String,
    val last_modified: String,
    val num_results: Int,
    val results: Results,
    val status: String
)