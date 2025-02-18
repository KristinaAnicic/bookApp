package com.example.booksapp.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.booksapp.R
import com.example.booksapp.data.database.Entities.Book
import com.example.booksapp.data.database.Entities.BookWithDetails
import com.example.booksapp.data.database.Entities.ReadingFormat
import com.example.booksapp.utils.FormatDate.formatDate
import com.example.booksapp.utils.ReadingFormatEnum
import com.example.booksapp.utils.ReadingStatusEnum
import com.example.booksapp.viewModel.BooksViewModel
import kotlinx.coroutines.flow.flowOf

@Composable
fun BookDetailScreen(booksViewModel: BooksViewModel, id: Long?, onBackPressed: () -> Unit) {
    val bookFlow = id?.let { booksViewModel.getBookById(it) } ?: flowOf(null)
    val bookNull by bookFlow.collectAsState(initial = null)
    var showDialog = remember { mutableStateOf(false) }

    bookNull?.let { book ->
        val description = rememberParsedDescription(book.book.description)
        val annotatedDescription = rememberAnnotatedDescription(description)

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (showDialog.value) {
                AddBookDialog(
                    book = book,
                    booksViewModel = booksViewModel,
                    onConfirmation = {showDialog.value = false},
                    onDismissRequest = {showDialog.value = false}
                )
            }

            BookTopNavigation(booksViewModel, book, onBackPressed = {
                onBackPressed()
                },
                onEditClick = {
                    showDialog.value = true
                })

            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(8.dp),
                //horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var isError by remember { mutableStateOf(false) }
                AsyncImage(
                    model = if (isError) book.book.coverImage else "${book.book.coverImage}&&fife=w800",
                    contentDescription = "Book cover",
                    modifier = Modifier
                        .height(250.dp)
                        .padding(vertical = 16.dp)
                        .align(Alignment.CenterHorizontally),
                    onError = {
                        if (!isError) {
                            isError = true
                        }
                    }
                )

                Row(
                    modifier = Modifier.fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    if (book.book.trackChapters) {
                        val chaptersRead = (book.book.readChaptersCount ?: 0).toString()
                        val numOfChapters = book.book.numberOfChapters.toString()
                        TextCard("Chapters read", chaptersRead + "/" + numOfChapters, 130.dp,
                            backgroundColor = colorResource(id = R.color.custom_dark_teal),
                            borderColor = colorResource(id = R.color.custom_dark_teal),
                            textColor = Color.White)
                    } else {
                        val pagesRead = (book.book.readPagesCount ?: 0).toString()
                        val numOfPages = book.book.numberOfPages.toString()
                        TextCard("Pages read", pagesRead + "/" + numOfPages, 130.dp,
                            backgroundColor = colorResource(id = R.color.custom_card_color),
                            borderColor = colorResource(id = R.color.custom_card_color),
                            textColor = Color.White)
                    }
                    TextCard("Start date", (formatDate(book.book.startDate) ?: "no record").toString(), 130.dp,
                        backgroundColor = colorResource(id = R.color.custom_card_color),
                        borderColor = colorResource(id = R.color.custom_card_color),
                        textColor = Color.White)
                    TextCard("End date", (formatDate(book.book.endDate) ?: "no record").toString(), 130.dp,
                        backgroundColor = colorResource(id = R.color.custom_card_color),
                        borderColor = colorResource(id = R.color.custom_card_color),
                        textColor = Color.White)
                }
                Spacer(modifier = Modifier.padding(5.dp))

                book.format?.formatName?.let {
                    CustomDropDown(
                        items = listOf(
                            ReadingFormatEnum.PAPERBACK.format_name,
                            ReadingFormatEnum.EBOOK.format_name,
                            ReadingFormatEnum.AUDIOBOOK.format_name),
                        selected = it,
                        onItemSelected = { newFormat ->
                            book.book.readingFormatId = ReadingFormatEnum.getIdByName(newFormat) ?: ReadingFormatEnum.PAPERBACK.id
                            booksViewModel.upsertBook(book.book)
                        },
                    )
                }
                Spacer(modifier = Modifier.padding(5.dp))

                book.book.publishDate?.let { BoldNormalText("Publish date", it) }
                book.book.publisher?.let { BoldNormalText("Publisher", it) }
                BoldNormalText("Pages", book.book.numberOfPages.toString())

                if (book.book.numberOfChapters != null) {
                    book.book.numberOfChapters.let { BoldNormalText("Chapters", it.toString()) }
                }

                Text(
                    text = "Description:",
                    modifier = Modifier.padding(5.dp),
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = annotatedDescription,
                    modifier = Modifier.padding(5.dp),
                    lineHeight = 19.sp,
                    fontSize = 14.sp
                )
            }
        }
    } ?: run {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Book not found")
            Button(onClick = onBackPressed) {
                Text("Back")
            }
        }
    }
}

@Composable
fun BookTopNavigation(booksViewModel: BooksViewModel,
                      book : BookWithDetails,
                      onBackPressed: () -> Unit,
                      onEditClick: () -> Unit){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.baseline_arrow_back_ios_24),
            contentDescription = "Back",
            modifier = Modifier
                .size(40.dp)
                .padding(top = 8.dp, bottom = 8.dp, start=8.dp)
                .clickable(onClick = onBackPressed)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            //modifier = Modifier.width(250.dp)
            modifier = Modifier.weight(1f).padding(start = 10.dp, end = 10.dp)
        ) {
            book?.book?.let {
                Text(
                    text = it.title,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            }
            book?.book?.let {
                Text(
                    text = it.author,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        MoreOptionsMenu(
            isFavorite = book?.book?.favorite,
            isDropped = book.book.readingStatusId == ReadingStatusEnum.DROPPED.id,
            isDisabled = ((book.book.readPagesCount ?:0) <= 0 && (book.book.readChaptersCount ?:0) <= 0),
            onEditClick = {
                onEditClick()
            },
            onDeleteClick = {
                book?.let {
                    booksViewModel.deleteBook(it.book)
                    onBackPressed()
                } ?: onBackPressed()
            },
            onFavoriteClick = {
                book?.book?.let { currentBook ->
                    booksViewModel.upsertBook(currentBook.copy(favorite = !currentBook.favorite))
                }
            },
            onDroppedClick = {
                if (book.book.readingStatusId == ReadingStatusEnum.DROPPED.id){
                    book.book.readingStatusId = ReadingStatusEnum.CURRENTLY_READING.id
                }
                else if (book.book.readingStatusId != ReadingStatusEnum.DROPPED.id &&
                    ((book.book.readPagesCount ?: 0) > 0 || (book.book.readChaptersCount
                        ?: 0) > 0)
                ){
                    book.book.readingStatusId = ReadingStatusEnum.DROPPED.id
                }
                booksViewModel.upsertBook(book.book)
            }
        )
    }
}