package com.example.booksapp.utils

enum class ReadingStatusEnum(val id: Long, val status_name: String) {
    CURRENTLY_READING(1, "Currently reading"),
    PLAN_TO_READ(2, "Plan to read"),
    COMPLETED(3, "Completed"),
    DROPPED(4, "Dropped")
}