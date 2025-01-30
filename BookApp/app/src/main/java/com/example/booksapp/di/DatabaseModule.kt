package com.example.booksapp.di

import android.content.Context
import androidx.room.Room
import com.example.booksapp.data.database.AppDatabase
import com.example.booksapp.data.database.DAOs.BookDao
import com.example.booksapp.data.database.DAOs.QuoteDao
import com.example.booksapp.data.database.DAOs.ReadingFormatDao
import com.example.booksapp.data.database.DAOs.ReadingStatusDao
import com.example.booksapp.data.database.DAOs.ReadingStreakDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "books_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideBookDao(database: AppDatabase): BookDao {
        return database.bookDao()
    }

    @Provides
    @Singleton
    fun provideQuoteDao(database: AppDatabase): QuoteDao {
        return database.quoteDao()
    }

    @Provides
    @Singleton
    fun provideReadingStatusDao(database: AppDatabase): ReadingStatusDao {
        return database.readingStatusDao()
    }

    @Provides
    @Singleton
    fun provideReadingFormatDao(database: AppDatabase): ReadingFormatDao {
        return database.readingFormatDao()
    }

    @Provides
    @Singleton
    fun provideReadingStreakDao(database: AppDatabase): ReadingStreakDao {
        return database.readingStreakDao()
    }
}