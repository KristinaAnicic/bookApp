package com.example.booksapp.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.example.booksapp.Components.BookListGridSearch
import com.example.booksapp.MainActivity
import com.example.booksapp.R
import com.example.booksapp.model.BookSearch.BookSearchList
import com.example.booksapp.ui.theme.BooksAppTheme
import com.example.booksapp.utils.RetrofitInstance
import kotlinx.coroutines.launch

private const val ARG_PARAM1 = "search"

class SearchFragment : Fragment() {
    private var search: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            search = it.getString(ARG_PARAM1)
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_search, container, false)
        return ComposeView(requireContext()).apply {
            setContent {
                BooksAppTheme {
                    var bookList by remember { mutableStateOf<BookSearchList?>(null) }
                    var isLoading by remember { mutableStateOf(false) }

                    lifecycleScope.launch {
                        if (!search.isNullOrEmpty()) {
                            try {
                                isLoading = true
                                bookList = fetchBookList(search!!)
                            } catch (e: Exception) {
                                Log.e("SearchFragment", "Error: ${e.message}")
                            } finally {
                                isLoading = false
                            }
                        }
                    }

                    Scaffold {
                        if (isLoading){
                            CircularProgressIndicator()
                        }
                        else if (bookList?.items?.isNotEmpty() == true) {
                            BookListGridSearch(
                                bookList!!.items,
                                onBookClick = {googleId ->
                                    //Toast.makeText(context, googleId, Toast.LENGTH_SHORT).show()
                                    (activity as? MainActivity)?.openBookDetailFragment(isbn = null, id = googleId)
                                    /*val fragment = BookDetailFragment.newInstance(id = googleId)

                                    val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
                                    transaction.replace(R.id.fragment_container, fragment)
                                    transaction.addToBackStack(null)
                                    transaction.commit()*/
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    suspend fun fetchBookList(search: String) : BookSearchList? {
        val responseId = RetrofitInstance.api_Books.getBooksBySearchString("intitle:${search}")
        if (responseId.isSuccessful || responseId.body() != null){
            val booksWithImages = responseId.body()?.items?.filter { book ->
                book.volumeInfo?.imageLinks?.thumbnail?.isNotEmpty() == true
            }
            if (booksWithImages.isNullOrEmpty() == false && booksWithImages.size > 20){
                return responseId.body()
            }
            else{
                val responseId2 = RetrofitInstance.api_Books.getBooksBySearchString(search)
                if (responseId2.isSuccessful || responseId2.body() != null){
                    return responseId2.body()
                }
                else{
                    return null
                }
            }

        }
        else{
            return null
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
}