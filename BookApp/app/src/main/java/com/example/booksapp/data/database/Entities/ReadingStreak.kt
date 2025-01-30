package com.example.booksapp.data.database.Entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class ReadingStreak(
    @PrimaryKey(autoGenerate = true)
    val streakId: Long = 0,
    val lastReadDate: Date,
    val consecutiveReadingDays: Int,
    val consecutiveReadingWeeks: Int
)
