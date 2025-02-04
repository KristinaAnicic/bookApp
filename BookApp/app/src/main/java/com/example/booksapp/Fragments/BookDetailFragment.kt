package com.example.booksapp.Fragments

import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.booksapp.MainActivity
import com.example.booksapp.R
import com.example.booksapp.viewModel.BooksViewModel
import com.example.booksapp.data.database.Entities.BookWithDetails
import com.example.booksapp.databinding.FragmentBookDetailBinding
import com.example.booksapp.model.BookDetail.Item
import com.example.booksapp.utils.BookDialogHelper
import com.example.booksapp.utils.RetrofitInstance
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

@AndroidEntryPoint
class BookDetailFragment : Fragment() {
    private lateinit var detailBinding: FragmentBookDetailBinding
    private lateinit var loading: ProgressBar
    private var isbn: String? = null
    private var currentBookItem: Item? = null
    var bookSaved: Boolean = false
    var bookFromDatabase: BookWithDetails? = null

    private val booksViewModel: BooksViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        detailBinding = FragmentBookDetailBinding.inflate(inflater, container, false)
        return detailBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            if (it is MainActivity) {
                it.hideBottomNav()
            }
        }

        isbn = arguments?.getString("ISBN")

        if (isbn != null) {
            loading = detailBinding.progressBar
            loading.visibility = View.VISIBLE

            detailBinding.linearLayout.visibility = View.GONE
            detailBinding.scrollView3.visibility = View.GONE

            lifecycleScope.launch {
                loadBookDetails(isbn!!)

                bookFromDatabase =
                    currentBookItem?.let {
                        booksViewModel.getBookByGoogleApiId(it.id).firstOrNull()
                    }

                if (bookFromDatabase != null) {
                    bookSaved = true
                    detailBinding.addImageView.setImageResource(R.drawable.baseline_add_box_24)
                }
            }
        }

        detailBinding.backImageView.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        detailBinding.addImageView.setOnClickListener {
            if (!bookSaved) {
                currentBookItem?.let { it1 ->
                    BookDialogHelper.showAddBookDialog(fragment = this,
                        book = it1,
                        booksViewModel = booksViewModel,
                        onBookAdded = {
                            bookSaved = true
                            lifecycleScope.launch {
                                bookFromDatabase =
                                    currentBookItem?.let { it1 -> booksViewModel.getBookByGoogleApiId(it1.id).firstOrNull() }
                            }
                            detailBinding.addImageView.setImageResource(R.drawable.baseline_add_box_24)
                            Toast.makeText(requireContext(), "Book added to library", Toast.LENGTH_SHORT).show()
                        })
                }
            } else {
                Log.e("LoadBookDetails", "Učitana knjiga: ${bookFromDatabase}")
                bookFromDatabase?.let { it1 -> booksViewModel.deleteBook(it1.book) }

                lifecycleScope.launch {
                    currentBookItem?.let { bookItem ->
                        booksViewModel.getBookByGoogleApiId(bookItem.id).collect { book ->
                            if (book == null) {
                                Toast.makeText(requireContext(), "Book successfully deleted", Toast.LENGTH_SHORT).show()
                                detailBinding.addImageView.setImageResource(R.drawable.outline_add_box_24)
                                bookSaved = false
                            } else {
                                // Ako knjiga nije obrisana
                                Toast.makeText(
                                    requireContext(),
                                    "Failed to delete the book",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }

        override fun onDestroyView() {
            super.onDestroyView()
            activity?.let {
                if (it is MainActivity) {
                    it.showBottomNav()
                }
            }
        }

    private suspend fun loadBookDetails(isbn: String) {
        try {
            val bookDetails = fetchBookData(isbn) ?: run {
                showErrorMessage("Failed to load book details")
                return
            }
            updateUI(bookDetails)
            loading.visibility = View.GONE
            detailBinding.linearLayout.visibility = View.VISIBLE
            detailBinding.scrollView3.visibility = View.VISIBLE

        } catch (e: IOException) {
            showErrorMessage("Network error: ${e.message ?: "Unknown error"}")
        } catch (e: HttpException) {
            showErrorMessage("Server error: ${e.message ?: "Unknown error"}")
        }
    }

    private suspend fun fetchBookData(isbn: String): Item? {
        val responseId = RetrofitInstance.api_Books.getBookByIsbn("isbn:$isbn")
        if (!responseId.isSuccessful || responseId.body() == null) {
            return null
        }
        val bookId = responseId.body()!!.items[0].id
        val responseDetails = RetrofitInstance.api_Books.getBookById(bookId)
        return if (responseDetails.isSuccessful && responseDetails.body() != null) {
            responseDetails.body()
        } else {
            null
        }
    }

    private fun updateUI(bookDetails: Item) {
        currentBookItem = bookDetails
        val bookItem = bookDetails.volumeInfo

        detailBinding.apply {
            Glide.with(requireContext())
                .load("${bookItem.imageLinks.thumbnail}&&fife=w800")
                .into(bookCover)
            Log.d("BookItem", "Thumbnail URL: ${bookItem.imageLinks.thumbnail}")
            bookTitleTxt.text = bookItem.title
            authorTxt.text = bookItem.authors.joinToString(", ")
            publishDateTxt.text = bookItem.publishedDate
            publisherTxt.text = bookItem.publisher
            categoriesTxt.text = bookItem.categories[0]
            pagesCount.text = bookItem.pageCount.toString()

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                bookDescriptionTxt.text =
                    Html.fromHtml(bookItem.description, Html.FROM_HTML_MODE_LEGACY)
            } else {
                bookDescriptionTxt.text = HtmlCompat.fromHtml(
                    bookItem.description,
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            }
        }
    }

    private suspend fun showErrorMessage(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(
                requireContext(), message,
                if (message.contains("error", ignoreCase = true)) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(isbn: String) =
            BookDetailFragment().apply {
                arguments = Bundle().apply {
                    putString("ISBN", isbn)
                }
            }
    }
}