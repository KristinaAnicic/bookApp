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
import com.example.booksapp.data.database.Entities.Book
import com.example.booksapp.data.database.Entities.BookWithDetails
import com.example.booksapp.data.database.ReadingStatusEnum
import com.example.booksapp.databinding.FragmentBookDetailBinding
import com.example.booksapp.model.BookDetail.Item
import com.example.booksapp.utils.RetrofitInstance
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BookDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class BookDetailFragment : Fragment() {
    // TODO: Rename and change types of parameters
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
        arguments?.let {
            //param1 = it.getString(ARG_PARAM1)
            //param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_book_detail, container, false)
        detailBinding = FragmentBookDetailBinding.inflate(inflater, container, false)
        return detailBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //booksViewModel = ViewModelProvider(this).get(BooksViewModel::class.java)
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

                Log.d("BookCheck", "Book from database: $bookFromDatabase")

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
                val book = currentBookItem?.let { book ->
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
                }
                bookSaved = true
                lifecycleScope.launch {
                    bookFromDatabase =
                        currentBookItem?.let { it1 -> booksViewModel.getBookByGoogleApiId(it1.id).firstOrNull() }
                }

                detailBinding.addImageView.setImageResource(R.drawable.baseline_add_box_24)

            } else {
                Log.e("LoadBookDetails", "Učitana knjiga: ${bookFromDatabase}")
                bookFromDatabase?.let { it1 -> booksViewModel.deleteBook(it1.book) }

                /*val bookAfterDeletion =
                    currentBookItem?.let { it1 -> booksViewModel.getBookByGoogleApiId(it1.id) }*/
                lifecycleScope.launch {
                    currentBookItem?.let { bookItem ->
                        booksViewModel.getBookByGoogleApiId(bookItem.id).collect { book ->
                            if (book == null) {
                                // Ako knjiga nije pronađena (tj. uspješno je obrisana)
                                Toast.makeText(
                                    requireContext(),
                                    "Book successfully deleted",
                                    Toast.LENGTH_SHORT
                                ).show()
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
                val responseId = RetrofitInstance.api_Books.getBookByIsbn("isbn:$isbn")
                if (responseId.isSuccessful && responseId.body() != null) {
                    val bookId = responseId.body()!!.items[0].id
                    val response = RetrofitInstance.api_Books.getBookById(bookId)

                    if (response.isSuccessful && responseId.body() != null) {
                        val bookItem = response.body()!!.volumeInfo
                        currentBookItem = response.body()!!

                        detailBinding.apply {
                            Glide.with(requireContext())
                                .load("${bookItem.imageLinks.thumbnail}&&fife=w800")
                                .into(bookCover)
                            Log.d("BookItem", "Thumbnail URL: ${bookItem.imageLinks.thumbnail}")
                            bookTitleTxt.text = bookItem.title
                            authorTxt.text = bookItem.authors.joinToString(", ")
                            publishDateTxt.text = bookItem.publishedDate
                            publisherTxt.text = bookItem.publisher
                            //bookDescriptionTxt.text = Html.fromHtml(bookItem.description, Html.FROM_HTML_MODE_LEGACY)
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
                    loading.visibility = View.GONE
                    detailBinding.linearLayout.visibility = View.VISIBLE
                    detailBinding.scrollView3.visibility = View.VISIBLE

                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Failed to load books", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Network error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Server error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BookDetailFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(isbn: String) =
            BookDetailFragment().apply {
                arguments = Bundle().apply {
                    putString("ISBN", isbn)
                }
            }
    }
}