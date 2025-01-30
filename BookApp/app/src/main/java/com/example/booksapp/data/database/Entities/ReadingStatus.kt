package com.example.booksapp.data.database.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ReadingStatus(
    @PrimaryKey(autoGenerate = true)
    val statusId: Long = 0,
    val statusName: String
)
