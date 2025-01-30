package com.example.booksapp.data.database.DAOs

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.example.booksapp.data.database.Entities.ReadingStatus
import com.example.booksapp.data.database.Entities.ReadingStreak
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingStatusDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllStatuses(statuses: List<ReadingStatus>)

    @Upsert
    suspend fun upsertStatus(readingStatus: ReadingStatus)

    @Delete
    suspend fun deleteStatus(readingStatus: ReadingStatus)

    @Query("SELECT * FROM readingStatus")
    fun getStatusList(): Flow<List<ReadingStatus>>
}