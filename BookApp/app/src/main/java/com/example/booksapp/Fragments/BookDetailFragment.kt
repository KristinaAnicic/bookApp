package com.example.booksapp.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.booksapp.MainActivity
import com.example.booksapp.R
import com.example.booksapp.viewModel.BooksViewModel
import com.example.booksapp.data.database.Entities.Book
import com.example.booksapp.data.database.Entities.BookWithDetails
import com.example.booksapp.data.database.ReadingFormatEnum
import com.example.booksapp.data.database.ReadingStatusEnum
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
import java.time.LocalDate
import java.util.Date

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
                /*val book = currentBookItem?.let { book ->
                    Book(
                        title = book.volumeInfo.title,
                        author = book.volumeInfo.authors.joinToString(", "),
                        coverImage = "${book.volumeInfo.imageLinks.thumbnail}&&fife=w800",
                        description = book.volumeInfo.description,
                        publishDate = book.volumeInfo.publishedDate,
                        publisher = book.volumeInfo.publisher,
                        numberOfPages = book.volumeInfo.pageCount,
                        readingStatusId = ReadingStatusEnum.PLAN_TO_READ.id,
                        readingFormatId = 1,
                        googleApiId = book.id
                    )
                }
                if (book != null) {
                    booksViewModel.upsertBook(book)
                }*/
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

    /*@SuppressLint("MissingInflatedId")
    private fun showAddBookDialog(book: Item) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_book, null)
        val spinnerFormat = dialogView.findViewById<Spinner>(R.id.readingFormats)

        val formats = listOf(ReadingFormatEnum.PAPERBACK.format_name, ReadingFormatEnum.EBOOK.format_name, ReadingFormatEnum.AUDIOBOOK.format_name)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, formats)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFormat.adapter = adapter

        val editTextNumOfPages = dialogView.findViewById<EditText>(R.id.numOfPages)
        val editTextPagesRead = dialogView.findViewById<EditText>(R.id.pagesRead)
        val editTextNumOfChapters = dialogView.findViewById<EditText>(R.id.numOfChapters)
        val editTextChaptersRead = dialogView.findViewById<EditText>(R.id.chaptersRead)

        editTextNumOfPages.setText(book.volumeInfo.pageCount?.toString() ?: "0")
        editTextNumOfChapters.setText("0")

        editTextPagesRead.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val pagesRead = s?.toString()?.toIntOrNull() ?: 0
                val numberOfPages = editTextNumOfPages.text.toString().toIntOrNull() ?: 0

                if (pagesRead < 0) {
                    editTextPagesRead.setText("0")
                } else if (pagesRead > numberOfPages) {
                    editTextPagesRead.setText(numberOfPages.toString())
                }
            }
        })

        editTextChaptersRead.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val chaptersRead = s?.toString()?.toIntOrNull() ?: 0
                val numberOfChapters = editTextNumOfChapters.text.toString().toIntOrNull() ?: 0

                if (chaptersRead < 0) {
                    editTextChaptersRead.setText("0")
                } else if (chaptersRead > numberOfChapters) {
                    editTextChaptersRead.setText(numberOfChapters.toString())
                }
            }
        })


        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Add Book")
            .setView(dialogView)
            .setPositiveButton("Add") { dialog, which ->
                val numberOfPages = editTextNumOfPages.text.toString().toIntOrNull() ?: book.volumeInfo.pageCount
                val pagesRead = editTextPagesRead.text.toString().toIntOrNull() ?: 0
                val numberOfChapters = editTextNumOfChapters.text.toString().toIntOrNull() ?: 0
                val chaptersRead = editTextChaptersRead.text.toString().toIntOrNull() ?: 0

                val selectedFormat = spinnerFormat.selectedItem as String
                val readingFormatEnum = ReadingFormatEnum.entries.find { it.format_name == selectedFormat }
                val readingFormatId = readingFormatEnum?.id ?: ReadingFormatEnum.PAPERBACK.id

                var readingStatusId = ReadingStatusEnum.PLAN_TO_READ.id
                var startDate : Date? = null
                var endDate : Date? = null

                if ((pagesRead == numberOfPages) || (chaptersRead == numberOfChapters)){
                    readingStatusId = ReadingStatusEnum.COMPLETED.id
                    startDate = Date()
                    endDate = Date()
                }
                else if (pagesRead > 0 || chaptersRead > 0){
                    readingStatusId = ReadingStatusEnum.CURRENTLY_READING.id
                    startDate = Date()
                }

                val updatedBook = book.let { book ->
                    Book(
                        numberOfPages = numberOfPages,
                        readingFormatId = readingFormatId,
                        readPagesCount = pagesRead,
                        numberOfChapters = numberOfChapters,
                        readChaptersCount = chaptersRead,
                        startDate = startDate,
                        endDate = endDate,
                        title = book.volumeInfo.title,
                        author = book.volumeInfo.authors.joinToString(", "),
                        coverImage = "${book.volumeInfo.imageLinks.thumbnail}&&fife=w800",
                        description = book.volumeInfo.description,
                        publishDate = book.volumeInfo.publishedDate,
                        publisher = book.volumeInfo.publisher,
                        readingStatusId = readingStatusId,
                        googleApiId = book.id
                    )
                }

                booksViewModel.upsertBook(updatedBook)
                bookSaved = true
                detailBinding.addImageView.setImageResource(R.drawable.baseline_add_box_24)
                Toast.makeText(requireContext(), "Book added to library", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel") { dialog, which ->
            }
            .create()

        dialog.show()
    }*/

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