package com.example.booksapp.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import com.example.booksapp.Components.BookListRow
import com.example.booksapp.Components.Streak
import com.example.booksapp.MainActivity
import com.example.booksapp.viewModel.BooksViewModel
import com.example.booksapp.ui.theme.BooksAppTheme
import dagger.hilt.android.AndroidEntryPoint

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@AndroidEntryPoint
class SavedBooksFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    /*private val db by lazy {
        Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java,
            "books.db"
        ).build()
    }*/

    /*private val db by lazy {
        AppDatabase.getDatabase(requireContext())
    }*/

    private val booksViewModel: BooksViewModel by viewModels()

    /*private val booksViewModel by viewModels<BooksViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return BooksViewModel(db.bookDao(), db.quoteDao(), db.readingStatusDao(),
                        db.readingFormatDao(), db.readingStreakDao()) as T
                }
            }
        }
    )*/


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*GlobalScope.launch(Dispatchers.IO) {
            booksViewModel.bookRepository.initializeDatabase()
        }*/

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //booksViewModel = ViewModelProvider(this).get(BooksViewModel::class.java)
        return ComposeView(requireContext()).apply {
            setContent {
                BooksAppTheme {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        floatingActionButton = {
                            FloatingActionButton(onClick = { }) {
                                Icon(Icons.Default.Add, contentDescription = "Add")
                            }
                        }
                    , content = {
                        Column (
                            modifier = Modifier
                                //.padding(innerPadding)
                                //.padding(8.dp)
                                .fillMaxSize()
                                .padding(start = 8.dp, top = 20.dp)
                                .verticalScroll(rememberScrollState())) {

                                Streak(booksViewModel = booksViewModel)

                                Spacer(
                                    modifier = Modifier.height(10.dp)
                                )
                                Row (
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 15.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ){
                                    Text(
                                        text = "My book list",
                                        //modifier = Modifier.padding(all = 8.dp),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = "More",
                                        modifier = Modifier.size(35.dp)
                                            .clickable(
                                                onClick = {
                                                    (activity as? MainActivity)?.openAllSavedBooksFragment()
                                                }
                                            ))
                                }


                                val books by booksViewModel.books.collectAsState(initial = emptyList())
                                BookListRow(books,
                                    onBookClick = { bookId ->
                                        (activity as? MainActivity)?.openSavedBookDetailFragment(bookId)
                                    })

                                Text(
                                    text = "Favorite books",
                                    //modifier = Modifier.padding(all = 8.dp),
                                    modifier = Modifier.padding(top = 15.dp),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )

                                val favoriteBooks by booksViewModel.favoriteBooks.collectAsState(initial = emptyList())
                                BookListRow(favoriteBooks,
                                    onBookClick = { bookId ->
                                        (activity as? MainActivity)?.openSavedBookDetailFragment(bookId)
                                    })
                        }
                    })
                }
            }
        }
    }
        //return inflater.inflate(R.layout.fragment_my_books, container, false)

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SavedBooksFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}