package com.example.booksapp.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FormatDate {
    fun formatDate(date: Date?): String {
        return if (date != null) {
            SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(date)
        } else {
            "no records"
        }
    }
}