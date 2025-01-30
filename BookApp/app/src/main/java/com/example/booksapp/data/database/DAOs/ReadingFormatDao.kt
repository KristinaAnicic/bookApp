package com.example.booksapp.data.database.DAOs

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.example.booksapp.data.database.Entities.ReadingFormat
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingFormatDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllFormats(statuses: List<ReadingFormat>)

    @Upsert
    suspend fun upsertFormat(readingFormat: ReadingFormat)

    @Delete
    suspend fun deleteFormat(readingFormat: ReadingFormat)

    @Query("SELECT * FROM readingFormat")
    fun getFormatList(): Flow<List<ReadingFormat>>
}