package com.example.booksapp.Components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.util.joinIntoString
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
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            items(books) { book ->
                book.coverImage?.let {
                        imageUrl ->
                    if (imageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Book cover",
                            modifier = Modifier
                                .height(150.dp)
                                .clickable {
                                    onBookClick(book.bookId.toString())
                                }
                        )
                    }
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
            Text(text = "No saved books",
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
                book.coverImage?.let {
                        imageUrl ->
                    if (imageUrl.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .clickable { onBookClick(book.bookId.toString()) }
                        ) {
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = "Book cover",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.aspectRatio(2f / 3f)
                                    .clickable {
                                        onBookClick(book.bookId.toString())
                                    }
                            )
                        }
                    }
                }
            }
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
