package com.example.booksapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import coil.compose.AsyncImage
import com.example.booksapp.data.database.Entities.ReadingStreak
import com.example.booksapp.viewModel.BooksViewModel
import com.example.booksapp.ui.theme.BooksAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

    //private lateinit var booksViewModel: BooksViewModel
    //private var bookRepository = BookRepository(readingStatusDao, readingFormatDao)


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

        GlobalScope.launch(Dispatchers.IO) {
            booksViewModel.bookRepository.initializeDatabase()
        }

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //booksViewModel = ViewModelProvider(this).get(BooksViewModel::class.java)

        return ComposeView(requireContext()).apply {
            setContent {
                BooksAppTheme {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        Column(
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())) {
                            Streak(booksViewModel = booksViewModel)

                            Text(
                                text = "My book list",
                                modifier = Modifier
                                    .padding(all = 8.dp),
                                style = MaterialTheme.typography.titleLarge
                            )

                            BooksScreen(booksViewModel = booksViewModel)
                        }
                        }
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

@Composable
fun BooksScreen(booksViewModel: BooksViewModel) {
    val books by booksViewModel.books.collectAsState(initial = emptyList())

    if (books.isEmpty()) {
        Text(
            modifier = Modifier.padding(all = 8.dp),
            text = "No books available"
        )
    } else {
        LazyRow(
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(books) { book ->
                book.coverImage?.let {
                    imageUrl ->
                    if (imageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Book cover",
                            modifier = Modifier.width(100.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Streak(booksViewModel: BooksViewModel){
    val streak by booksViewModel.readingStreak.collectAsState(initial = null)

    Text(
        text="STATS",
        modifier = Modifier
            .padding(all = 8.dp),
        style = MaterialTheme.typography.titleMedium
    )

    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(10.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        StreakCard("WEEKLY STREAK", (streak?.consecutiveReadingWeeks ?: 0).toString(), 120.dp)
        StreakCard("DAILY STREAK", (streak?.consecutiveReadingDays ?: 0).toString(), 120.dp)
        StreakCard("LAST READING DATE", (streak?.lastReadDate ?: "no record").toString(), 160.dp)
    }
}

@Composable
fun StreakCard(title: String, value: String, width: Dp) {
    OutlinedCard(
        modifier = Modifier
            .width(width)
            .fillMaxHeight(),
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun StreakPreview() {
    BooksAppTheme {
        StreakPreviewContent()
    }
}

fun parseDate(dateString: String): Date {
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return format.parse(dateString) ?: throw IllegalArgumentException("Invalid date format: $dateString")
}

@Composable
fun StreakPreviewContent() {
    val mockStreak = ReadingStreak(
        consecutiveReadingWeeks = 5,
        consecutiveReadingDays = 30,
        lastReadDate = parseDate("2025-01-29")
    )

    Column {
        Text(
            text = "STATS",
            modifier = Modifier.padding(all = 8.dp),
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StreakCard("WEEKLY STREAK", mockStreak.consecutiveReadingWeeks.toString(), 100.dp)
            StreakCard("DAILY STREAK", mockStreak.consecutiveReadingDays.toString(), 100.dp)
            StreakCard("LAST READING DATE", mockStreak.lastReadDate.toString(), 120.dp)
        }
    }
}
