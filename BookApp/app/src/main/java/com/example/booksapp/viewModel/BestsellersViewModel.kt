package com.example.booksapp.viewModel

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.booksapp.ApiInterface
import com.example.booksapp.model.Book
import com.example.booksapp.utils.RetrofitInstance
import com.google.android.mediahome.books.BookItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.example.booksapp.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@HiltViewModel
class BestsellersViewModel @Inject constructor(
    //private val application: Application
    //private val sharedPreferences: SharedPreferences
): ViewModel() {

    private val _fictionBooks = MutableLiveData<List<Book>>()
    val fictionBooks: LiveData<List<Book>> = _fictionBooks

    private val _nonFictionBooks = MutableLiveData<List<Book>>()
    val nonFictionBooks: LiveData<List<Book>> = _nonFictionBooks

    private val _adviceBooks = MutableLiveData<List<Book>>()
    val adviceBooks: LiveData<List<Book>> = _adviceBooks

    private val _youngAdultBooks = MutableLiveData<List<Book>>()
    val youngAdultBooks: LiveData<List<Book>> = _youngAdultBooks

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    //private var areBooksLoaded = false

    /*private val fictionUrl = application.getString(R.string.url_fiction)
    private val nonFictionUrl = application.getString(R.string.url_nonfiction)
    private val adviceUrl = application.getString(R.string.url_advice)
    private val youngAdultUrl = application.getString(R.string.url_youngAdult)*/

    init {
        //loadBooksFromUrls(fictionUrl, nonFictionUrl, adviceUrl, youngAdultUrl)
    }
    /*private var areBooksLoaded: Boolean
        get() {
            val value = sharedPreferences.getBoolean("areBooksLoaded", false)
            Log.d("BestsellersViewModel", "areBooksLoaded (from prefs): $value")
            return value
        }
        set(value) {
            Log.d("BestsellersViewModel", "Setting areBooksLoaded to: $value")
            sharedPreferences.edit().putBoolean("areBooksLoaded", value).apply()
        }*/


    fun loadBooksFromUrls(fictionUrl: String, nonFictionUrl: String, adviceUrl: String, youngAdultUrl: String) {
        /*val storedBooks = loadBooksFromPreferences()

        if (storedBooks.fictionBooks.isNotEmpty() || storedBooks.nonFictionBooks.isNotEmpty() || storedBooks.adviceBooks.isNotEmpty() || storedBooks.youngAdultBooks.isNotEmpty()) {
            _fictionBooks.postValue(storedBooks.fictionBooks)
            _nonFictionBooks.postValue(storedBooks.nonFictionBooks)
            _adviceBooks.postValue(storedBooks.adviceBooks)
            _youngAdultBooks.postValue(storedBooks.youngAdultBooks)
            _isLoading.postValue(false)
            areBooksLoaded = true
            return
        }*/

        Log.d("BestsellersViewModel", "Loading books...")
        viewModelScope.launch {
            _isLoading.postValue(true)

            val fictionBooks = loadBooks(fictionUrl)
            _fictionBooks.postValue(fictionBooks)

            val nonFictionBooks = loadBooks(nonFictionUrl)
            _nonFictionBooks.postValue(nonFictionBooks)

            val adviceBooks = loadBooks(adviceUrl)
            _adviceBooks.postValue(adviceBooks)

            val youngAdultBooks = loadBooks(youngAdultUrl)
            _youngAdultBooks.postValue(youngAdultBooks)

            //saveBooksToPreferences(fictionBooks, nonFictionBooks, adviceBooks, youngAdultBooks)
            _isLoading.postValue(false)

            //areBooksLoaded = true // Označi da su podaci učitani
            Log.d("BestsellersViewModel", "Books loaded and areBooksLoaded set to true")
        }
    }

    /*private suspend fun loadBooks(endpoint: String): List<Book> {
        return try {
            val response = RetrofitInstance.api_Bestsellers.getBestsellerBooks(endpoint)
            if (response.isSuccessful && response.body() != null) {
                response.body()!!.results.books
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }*/

    private suspend fun loadBooks(endpoint: String): List<Book> {
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
    }

    /*fun saveBooksToPreferences(fictionBooks: List<Book>, nonFictionBooks: List<Book>, adviceBooks: List<Book>, youngAdultBooks: List<Book>) {
        val editor = sharedPreferences.edit()
        editor.putString("fictionBooks", Gson().toJson(fictionBooks))
        editor.putString("nonFictionBooks", Gson().toJson(nonFictionBooks))
        editor.putString("adviceBooks", Gson().toJson(adviceBooks))
        editor.putString("youngAdultBooks", Gson().toJson(youngAdultBooks))

        editor.apply()
    }

    fun loadBooksFromPreferences(): Books  {
        val fictionBooksJson = sharedPreferences.getString("fictionBooks", "[]")
        val nonFictionBooksJson = sharedPreferences.getString("nonFictionBooks", "[]")
        val adviceBooksJson = sharedPreferences.getString("adviceBooks", "[]")
        val youngAdultBooksJson = sharedPreferences.getString("youngAdultBooks", "[]")

        val fictionBooks = Gson().fromJson(fictionBooksJson, Array<Book>::class.java).toList()
        val nonFictionBooks = Gson().fromJson(nonFictionBooksJson, Array<Book>::class.java).toList()
        val adviceBooks = Gson().fromJson(adviceBooksJson, Array<Book>::class.java).toList()
        val youngAdultBooks = Gson().fromJson(youngAdultBooksJson, Array<Book>::class.java).toList()

        return Books(fictionBooks, nonFictionBooks, adviceBooks, youngAdultBooks)
    }*/

}

data class Books(
    val fictionBooks: List<Book>,
    val nonFictionBooks: List<Book>,
    val adviceBooks: List<Book>,
    val youngAdultBooks: List<Book>
)