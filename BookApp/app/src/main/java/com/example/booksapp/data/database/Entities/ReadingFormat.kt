package com.example.booksapp.data.database.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ReadingFormat(
    @PrimaryKey(autoGenerate = true)
    val formatId: Long = 0,
    val formatName: String
)
