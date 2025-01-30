package com.example.booksapp.data.database

import com.example.booksapp.data.database.DAOs.ReadingFormatDao
import com.example.booksapp.data.database.DAOs.ReadingStatusDao
import com.example.booksapp.data.database.Entities.ReadingFormat
import com.example.booksapp.data.database.Entities.ReadingStatus
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class BookRepository @Inject constructor(
    private val readingStatusDao: ReadingStatusDao,
    private val readingFormatDao: ReadingFormatDao,
) {
    suspend fun initializeDatabase(){
        val currentStates = readingStatusDao.getStatusList().first()

        if(currentStates.isEmpty()){
            readingStatusDao.insertAllStatuses(
                listOf(
                    ReadingStatus(statusName = "Plan to read"),
                    ReadingStatus(statusName = "Completed"),
                    ReadingStatus(statusName = "Currently reading"),
                    ReadingStatus(statusName = "Dropped")
                )
            )
        }

        val currentFormats = readingFormatDao.getFormatList().first()
        if (currentFormats.isEmpty()) {
            readingFormatDao.insertAllFormats(
                listOf(
                    ReadingFormat(formatName = "Paperback"),
                    ReadingFormat(formatName = "Ebook"),
                    ReadingFormat(formatName = "Audiobook")
                )
            )
        }
    }
}