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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import com.example.booksapp.Components.AddBookDialog
import com.example.booksapp.Components.BookListRow
import com.example.booksapp.Components.Streak
import com.example.booksapp.MainActivity
import com.example.booksapp.viewModel.BooksViewModel
import com.example.booksapp.ui.theme.BooksAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SavedBooksFragment : Fragment() {
    private val booksViewModel: BooksViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        booksViewModel.checkAndResetReadingStreak()
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
                    var showDialog = remember { mutableStateOf(false) }

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        floatingActionButton = {
                            FloatingActionButton(onClick = { showDialog.value  = true }) {
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
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 15.dp)
                                        .clickable(
                                            onClick = {
                                                (activity as? MainActivity)?.openAllSavedBooksFragment()
                                            }
                                        )
                                ){
                                    Text(
                                        text = "My book list",
                                        modifier = Modifier.padding(all = 7.dp),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = "More",
                                        modifier = Modifier.size(35.dp))
                                }


                                val books by booksViewModel.books.collectAsState(initial = emptyList())
                                BookListRow(books,
                                    onBookClick = { bookId ->
                                        (activity as? MainActivity)?.openSavedBookDetailFragment(bookId)
                                    })

                                Text(
                                    text = "Favorite books",
                                    //modifier = Modifier.padding(all = 8.dp),
                                    modifier = Modifier.padding(top = 15.dp, start = 7.dp),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )

                                val favoriteBooks by booksViewModel.favoriteBooks.collectAsState(initial = emptyList())
                                BookListRow(favoriteBooks,
                                    onBookClick = { bookId ->
                                        (activity as? MainActivity)?.openSavedBookDetailFragment(bookId)
                                    })
                        }

                        if (showDialog.value) {
                            AddBookDialog(
                                book = null,
                                booksViewModel = booksViewModel,
                                onConfirmation = {},
                                onDismissRequest = {showDialog.value = false}
                            )
                        }
                    })
                }
            }
        }
    }
        //return inflater.inflate(R.layout.fragment_my_books, container, false)
}