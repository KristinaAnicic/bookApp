package com.example.booksapp.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booksapp.utils.BestsellerBookList
import com.example.booksapp.model.Book
import com.example.booksapp.utils.RetrofitInstance
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore

class BestsellerApiViewModel : ViewModel() {
    val fictionBooks = MutableLiveData<List<Book>>()
    val nonFictionBooks = MutableLiveData<List<Book>>()
    val adviceBooks = MutableLiveData<List<Book>>()
    val youngAdultBooks = MutableLiveData<List<Book>>()

    var areBooksLoaded: Boolean = false

    fun loadBooksFromApi(fictionUrl: String, nonFictionUrl: String, adviceUrl: String, youngAdultUrl: String) {
        if (BestsellerBookList.areBooksLoaded) return

        viewModelScope.launch {
            val fiction = loadBooks(fictionUrl)
            val nonFiction = loadBooks(nonFictionUrl)
            val advice = loadBooks(adviceUrl)
            val youngAdult = loadBooks(youngAdultUrl)

            fictionBooks.postValue(fiction)
            nonFictionBooks.postValue(nonFiction)
            adviceBooks.postValue(advice)
            youngAdultBooks.postValue(youngAdult)

            BestsellerBookList.fictionBooks = fiction
            BestsellerBookList.nonFictionBooks = nonFiction
            BestsellerBookList.adviceBooks = advice
            BestsellerBookList.youngAdultBooks = youngAdult

            BestsellerBookList.areBooksLoaded = true

            areBooksLoaded = true
        }
    }
    private val apiCallSemaphore = Semaphore(2)

    private suspend fun loadBooks(endpoint: String): List<Book> {
        apiCallSemaphore.acquire()
        //var retryDelay = 1000L
        try {
            while (true) {
                try {
                    val response = RetrofitInstance.api_Bestsellers.getBestsellerBooks(endpoint)
                    if (response.isSuccessful && response.body() != null) {
                        return response.body()!!.results.books
                    } else {
                        delay(2000)
                    }
                } catch (e: Exception) {
                    delay(2000)
                }
            }
        } finally {
            apiCallSemaphore.release() // Release the permit
        }
    }
}