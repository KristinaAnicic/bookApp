package com.example.booksapp.viewModel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booksapp.data.database.BookRepository
import com.example.booksapp.data.database.DAOs.BookDao
import com.example.booksapp.data.database.DAOs.QuoteDao
import com.example.booksapp.data.database.DAOs.ReadingFormatDao
import com.example.booksapp.data.database.DAOs.ReadingStatusDao
import com.example.booksapp.data.database.DAOs.ReadingStreakDao
import com.example.booksapp.data.database.Entities.Book
import com.example.booksapp.data.database.Entities.BookWithDetails
import com.example.booksapp.data.database.Entities.Quote
import com.example.booksapp.data.database.Entities.ReadingFormat
import com.example.booksapp.data.database.Entities.ReadingStatus
import com.example.booksapp.data.database.Entities.ReadingStreak
import com.example.booksapp.utils.ReadingStatusEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.temporal.WeekFields
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class BooksViewModel @Inject constructor(
    private val bookDao: BookDao,
    private val quoteDao: QuoteDao,
    private val readingStatusDao: ReadingStatusDao,
    private val readingFormatDao: ReadingFormatDao,
    private val readingStreakDao: ReadingStreakDao,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    var bookRepository = BookRepository(readingStatusDao, readingFormatDao)
    private val _selectedStatus = MutableStateFlow<ReadingStatusEnum?>(null)
    val selectedStatus: StateFlow<ReadingStatusEnum?> = _selectedStatus.asStateFlow()

    private var isDatabaseInitialized = false

    val books: Flow<List<Book>> = bookDao.getBookList()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val booksByStatus: Flow<List<Book>> = _selectedStatus.flatMapLatest {
        status ->
            if (status == null) {
                bookDao.getBookList()
            } else {
                bookDao.getBooksByStatus(status.id)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteBooks: Flow<List<Book>> = bookDao.getFavoriteBookList()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val quotes: Flow<List<Quote>> = quoteDao.getAllQuotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val readingStreak: Flow<ReadingStreak?> = readingStreakDao.getStreak()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val readingFormats: Flow<List<ReadingFormat>> = readingFormatDao.getFormatList()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val readingStatuses: Flow<List<ReadingStatus>> = readingStatusDao.getStatusList()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            if (!sharedPreferences.getBoolean("is_database_initialized", false)) {
                bookRepository.initializeDatabase()
                sharedPreferences.edit().putBoolean("is_database_initialized", true).apply()
            }
        }
    }

    fun setStatusType(status: ReadingStatusEnum?) {
        _selectedStatus.value = status
    }

    fun getStatusType() : ReadingStatusEnum?{
        return _selectedStatus.value
    }

    fun getBookById(bookId: Long): Flow<BookWithDetails?> {
        return bookDao.getBookById(bookId)
    }

    fun getBookByGoogleApiId(bookId: String): Flow<BookWithDetails?> {
        return bookDao.getBookByGoogleApiId(bookId)
    }

    fun upsertBook(book: Book) {
        viewModelScope.launch {
            bookDao.upsertBook(book)
        }
    }

    fun deleteBook(book: Book) {
        viewModelScope.launch {
            bookDao.deleteBook(book)
        }
    }
    /*fun deleteBook(book: Book) {
        viewModelScope.launch {
            try {
                // Brisanje knjige iz baze podataka
                bookDao.deleteBook(book) // Pretpostavljam da koristiš neki repository za interakciju s bazom
            } catch (e: Exception) {
                // Obrada grešaka
                Log.e("BooksViewModel", "Error deleting book: ${e.message}")
            }
        }
    }*/

    fun upsertQuote(quote: Quote) {
        viewModelScope.launch {
            quoteDao.upsertQuote(quote)
        }
    }

    fun deleteQuote(quote: Quote) {
        viewModelScope.launch {
            quoteDao.deleteQuote(quote)
        }
    }

    fun setReadingStreak(){
        viewModelScope.launch {
            val currentDate = Date()
            val streak = readingStreakDao.getStreak().firstOrNull()

            if (streak != null){
                val lastReadDate = streak.lastReadDate
                var dayStreak = streak.consecutiveReadingDays
                var weekStreak = streak.consecutiveReadingWeeks

                val currentLocalDate = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                val lastLocalDate = lastReadDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()

                if(lastLocalDate.plusDays(1) == currentLocalDate){
                    dayStreak += 1
                }
                else if (lastLocalDate.isBefore(currentLocalDate.minusDays(1))) {
                    dayStreak = 1
                }

                val currentWeek = currentLocalDate.get(WeekFields.ISO.weekOfYear())
                val lastReadWeek = lastLocalDate.get(WeekFields.ISO.weekOfYear())
                val currentYear = currentLocalDate.year
                val lastReadYear = lastLocalDate.year

                if((lastReadWeek + 1 == currentWeek && currentYear == lastReadYear) ||
                    (lastReadYear + 1 == currentYear &&
                    lastReadWeek == currentLocalDate.minusWeeks(1).get(WeekFields.ISO.weekOfYear()))){

                    weekStreak += 1
                }
                else if ((lastReadWeek + 1 < currentWeek && currentYear == lastReadYear) ||
                    (lastReadYear + 1 == currentYear &&
                            lastReadWeek < currentLocalDate.minusWeeks(1).get(WeekFields.ISO.weekOfYear()))){

                    weekStreak = 1
                }

                readingStreakDao.upsertStreak(
                    streak.copy(
                        lastReadDate = currentDate,
                        consecutiveReadingDays = dayStreak,
                        consecutiveReadingWeeks = weekStreak
                    )
                )
            }
            else{
                readingStreakDao.upsertStreak(
                    ReadingStreak(
                        streakId = 0,
                        lastReadDate = currentDate,
                        consecutiveReadingDays = 1,
                        consecutiveReadingWeeks = 1
                    )
                )
            }
        }
    }

    fun resetStreak() {
        viewModelScope.launch {
            val streak = readingStreak.firstOrNull()
            if (streak != null) {
                readingStreakDao.deleteStreak(streak)
            }
        }
    }

}