package com.example.booksapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.booksapp.data.database.DAOs.BookDao
import com.example.booksapp.data.database.DAOs.QuoteDao
import com.example.booksapp.data.database.DAOs.ReadingFormatDao
import com.example.booksapp.data.database.DAOs.ReadingStatusDao
import com.example.booksapp.data.database.DAOs.ReadingStreakDao
import com.example.booksapp.data.database.Entities.Book
import com.example.booksapp.data.database.Entities.Quote
import com.example.booksapp.data.database.Entities.ReadingFormat
import com.example.booksapp.data.database.Entities.ReadingStatus
import com.example.booksapp.data.database.Entities.ReadingStreak

@Database(
    entities = [
        Book::class,
        Quote::class,
        ReadingStreak::class,
        ReadingStatus::class,
        ReadingFormat::class],
    version = 3
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun quoteDao(): QuoteDao
    abstract fun readingStreakDao(): ReadingStreakDao
    abstract fun readingStatusDao(): ReadingStatusDao
    abstract fun readingFormatDao(): ReadingFormatDao

    /*companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "books.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }*/
}