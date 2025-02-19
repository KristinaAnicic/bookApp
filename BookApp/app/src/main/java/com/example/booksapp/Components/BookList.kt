package com.example.booksapp.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.booksapp.data.database.Entities.Book
import com.example.booksapp.model.BookSearch.Item

@Composable
fun BookListRow(books: List<Book>,
                onBookClick: (bookId: String) -> Unit) {

    if (books.isEmpty()) {
        OutlinedCard(
            modifier = Modifier.padding(top = 8.dp).fillMaxWidth(),
            //text = "No saved books"
        ){
            Text(text = "No saved books",
                modifier = Modifier.padding(30.dp),
                textAlign = TextAlign.Center)
        }
    } else {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            items(books) { book ->
                var imageLoadFailed by remember { mutableStateOf(false) }

                if (book.coverImage.isNotEmpty() && !imageLoadFailed) {
                    AsyncImage(
                        model = book.coverImage,
                        contentDescription = "Book cover",
                        modifier = Modifier
                            .height(150.dp)
                            .clickable { onBookClick(book.bookId.toString()) },
                        onError = { imageLoadFailed = true }
                    )
                }

                if (imageLoadFailed || book.coverImage.isEmpty()) {
                    BookTitleAndAuthorBox(book = book, onBookClick = { id -> onBookClick(id) })
                }
            }
        }
    }
}

@Composable
fun BookListGrid(books: List<Book>,
                 onBookClick: (bookId: String) -> Unit){
    if (books.isEmpty()) {
        OutlinedCard(
            modifier = Modifier.padding(top = 8.dp).fillMaxWidth(),
        ){
            Text(text = "This list is empty for now",
                modifier = Modifier.padding(30.dp),
                textAlign = TextAlign.Center)
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxSize().padding(10.dp)
        ) {
            items(books) { book ->
                var imageLoadFailed by remember { mutableStateOf(false) }

                if (book.coverImage.isNotEmpty() && !imageLoadFailed) {
                    AsyncImage(
                        model = book.coverImage,
                        contentDescription = "Book cover",
                        modifier = Modifier
                            .aspectRatio(2f / 3f)
                            .clickable { onBookClick(book.bookId.toString()) },
                        onError = { imageLoadFailed = true } // Obeležava da je učitavanje slike neuspešno
                    )
                }

                if (imageLoadFailed || book.coverImage.isEmpty()) {
                    BookTitleAndAuthorBox(book = book, onBookClick = { id -> onBookClick(id) })
                }
            }
        }
    }
}

@Composable
fun BookTitleAndAuthorBox(book: Book, onBookClick: (id: String) -> Unit){
    Box(
        modifier = Modifier
            .height(150.dp)
            .width(100.dp)
            .clickable {
                onBookClick(book.bookId.toString())
            }
            .background(Color.LightGray.copy(alpha = 0.2f)),
    ) {
        Column(
            modifier = Modifier.padding(8.dp).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = book.title ?: "No title",
                maxLines = 4,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = book.author ?: "Unknown author",
                maxLines = 2,
                fontSize = 12.sp,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun BookListGridSearch(books: List<Item>,
                 onBookClick: (bookId: String) -> Unit) {
    if (books.isEmpty()) {
        OutlinedCard(
            modifier = Modifier.padding(top = 8.dp).fillMaxWidth(),
        ) {
            Text(
                text = "No books found",
                modifier = Modifier.padding(30.dp),
                textAlign = TextAlign.Center
            )
        }
    } else {
        val booksWithImages = books.filter { book ->
            book.volumeInfo?.imageLinks?.thumbnail?.isNotEmpty() == true
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.fillMaxSize().padding(10.dp)
        ) {
            items(booksWithImages) { book ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onBookClick(book.id) }
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        book.volumeInfo.imageLinks.thumbnail.let { imageUrl ->
                            if (imageUrl.isNotEmpty()) {
                                AsyncImage(
                                    model = imageUrl,
                                    contentDescription = "Book cover",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.aspectRatio(2f / 3f)
                                        .clickable {
                                            onBookClick(book.id)
                                        }
                                )
                            }
                        }
                        Text(
                            text = book.volumeInfo.title,
                            fontWeight = FontWeight.Bold,
                            lineHeight = (17).sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom=3.dp).fillMaxWidth(),
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = book.volumeInfo.authors?.joinToString(", ")?.let { "by $it" }
                                ?: "by Unknown",
                            fontSize = 14.sp,
                            lineHeight = (17).sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(5.dp).fillMaxWidth(),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
