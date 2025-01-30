package com.example.booksapp.data.database

enum class ReadingStatusEnum(val id: Long, val status_name: String) {
    PLAN_TO_READ(1, "Plan to read"),
    COMPLETED(2, "Completed"),
    CURRENTLY_READING(3, "Currently reading"),
    DROPPED(4, "Dropped")
}