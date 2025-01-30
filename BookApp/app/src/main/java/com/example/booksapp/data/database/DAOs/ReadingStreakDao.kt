package com.example.booksapp.data.database.DAOs

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.booksapp.data.database.Entities.ReadingStreak
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingStreakDao {
    @Upsert
    suspend fun upsertStreak(readingStreak: ReadingStreak)

    @Delete
    suspend fun deleteStreak(readingStreak: ReadingStreak)

    @Query("SELECT * FROM readingstreak LIMIT 1")
    fun getStreak(): Flow<ReadingStreak>
}