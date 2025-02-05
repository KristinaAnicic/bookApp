package com.example.booksapp.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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

    private val MIGRATION_2_3 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE books ADD COLUMN trackChapters INTEGER DEFAULT 0")
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "books_database"
        )
            .addMigrations(MIGRATION_2_3)
            .fallbackToDestructiveMigration().build()
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