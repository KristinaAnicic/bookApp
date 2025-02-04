package com.example.booksapp.Components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.booksapp.R
import com.example.booksapp.data.database.Entities.BookWithDetails
import com.example.booksapp.viewModel.BooksViewModel
import kotlinx.coroutines.flow.flowOf

@Composable
fun BookDetailScreen(booksViewModel: BooksViewModel, id: Long?, onBackPressed: () -> Unit) {
    val bookFlow = id?.let { booksViewModel.getBookById(it) } ?: flowOf(null)
    val book by bookFlow.collectAsState(initial = null)

    val description = rememberParsedDescription(book?.book?.description)
    val annotatedDescription = rememberAnnotatedDescription(description)

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        BookTopNavigation(booksViewModel, book, onBackPressed = {
            onBackPressed()
        })

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(1f)
                .fillMaxWidth()
                .padding(8.dp),
            //horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = "${book?.book?.coverImage}&&fife=w800",
                contentDescription = "Book cover",
                modifier = Modifier
                    .height(250.dp)
                    .padding(vertical = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )

            book?.book?.publishDate?.let { BoldNormalText("Publish date", it) }
            book?.book?.publisher?.let { BoldNormalText("Publisher", it) }
            book?.book?.numberOfPages?.let { BoldNormalText("Pages", it.toString()) }

            if(book?.book?.numberOfChapters != null){
                book?.book?.numberOfChapters?.let { BoldNormalText("Chapters", it.toString()) }
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
}

@Composable
fun BookTopNavigation(booksViewModel: BooksViewModel, book : BookWithDetails?, onBackPressed: () -> Unit){
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

        MoreOptionsMenu(book?.book?.favorite,
            onEditClick = { /* Action for Edit */ },
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
            }
        )
    }
}