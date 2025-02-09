package com.example.booksapp.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import com.example.booksapp.Components.AddPagesDialog
import com.example.booksapp.Components.BookItem
import com.example.booksapp.MainActivity
import com.example.booksapp.R
import com.example.booksapp.data.database.Entities.Book
import com.example.booksapp.ui.theme.BooksAppTheme
import com.example.booksapp.utils.ReadingStatusEnum
import com.example.booksapp.viewModel.BooksViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date

@AndroidEntryPoint
class HomePageFragment : Fragment() {
    private val booksViewModel: BooksViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_home_page, container, false)

        return ComposeView(requireContext()).apply {
            setContent {
                BooksAppTheme {
                    Scaffold (
                        content = {
                            val books by booksViewModel.activeBooks.collectAsState(initial = emptyList())
                            val booksByStatus = books.groupBy { it.readingStatusId }

                            LazyColumn(
                                modifier = Modifier
                                    //.horizontalScroll(rememberScrollState())
                                    .fillMaxSize(),
                                //verticalArrangement = Arrangement.spacedBy(5.dp)
                            ){
                                val currentlyReadingBooks = booksByStatus[1] ?: emptyList()
                                if(currentlyReadingBooks.isNotEmpty()){
                                    item{
                                        Text(
                                            text = "Currently reading",
                                            modifier = Modifier
                                                .padding(top = 16.dp, bottom = 16.dp).fillMaxWidth()
                                                .background(
                                                    color = colorResource(R.color.custom_card_color),
                                                    shape = RoundedCornerShape(14.dp)
                                                ).padding(10.dp),
                                            color = Color.White,
                                            fontSize = 20.sp,
                                            textAlign = TextAlign.Center

                                        )
                                    }
                                    items(currentlyReadingBooks) { book -> BookItem(
                                        book = book,
                                        onBookClick = {

                                    },
                                        onAddPageClick = {newPage ->
                                            updateBookPages(newPage, book)
                                        })
                                    }
                                }

                                val planToReadBooks = booksByStatus[2] ?: emptyList()
                                if(planToReadBooks.isNotEmpty()){
                                    item{
                                        Text(
                                            text = "Start reading",
                                            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp).fillMaxWidth()
                                                .background(
                                                    color = colorResource(R.color.custom_card_color),
                                                    shape = RoundedCornerShape(14.dp)
                                                ).padding(10.dp),
                                            fontSize = 20.sp,
                                            color = Color.White,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                    items(planToReadBooks) { book -> BookItem(
                                        book = book,
                                        onBookClick = {

                                            },
                                        onAddPageClick = {newPage ->
                                            updateBookPages(newPage, book)
                                        })
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    fun updateBookPages(updatedPages: Int, book: Book){
        if (book.trackChapters){
            if (book.readChaptersCount != updatedPages) {
                if (updatedPages > book.readChaptersCount!!) {
                    booksViewModel.setReadingStreak()
                }
                if(book.readChaptersCount == 0 && updatedPages > 0){
                    book.readingStatusId = ReadingStatusEnum.CURRENTLY_READING.id
                }
                else if (book.readChaptersCount!! > 0 && updatedPages == 0){
                    book.readingStatusId = ReadingStatusEnum.PLAN_TO_READ.id
                    book.startDate = null
                    book.lastReadDate = null
                }
                else if(book.readChaptersCount == book.numberOfChapters){
                    book.readingStatusId = ReadingStatusEnum.COMPLETED.id
                    book.endDate = Date()
                    book.readPagesCount = book.numberOfPages
                }

                book.readChaptersCount = updatedPages
                book.lastReadDate = Date()
                booksViewModel.upsertBook(book)
            }
        }
        else {
            if (book.readPagesCount != updatedPages) {
                if (updatedPages > book.readPagesCount!!) {
                    booksViewModel.setReadingStreak()
                }
                if(book.readPagesCount == 0 && updatedPages > 0){
                    book.readingStatusId = ReadingStatusEnum.CURRENTLY_READING.id
                }
                else if (book.readPagesCount!! > 0 && updatedPages == 0){
                    book.readingStatusId = ReadingStatusEnum.PLAN_TO_READ.id
                    book.startDate = null
                    book.lastReadDate = null
                }
                else if(book.readPagesCount == book.numberOfPages){
                    book.readingStatusId = ReadingStatusEnum.COMPLETED.id
                    book.endDate = Date()
                    book.readChaptersCount = book.numberOfChapters
                }
                book.readPagesCount = updatedPages
                book.lastReadDate = Date()
                booksViewModel.upsertBook(book)
            }
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomePageFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}