package com.example.booksapp.Components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.booksapp.data.database.Entities.Book

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
            //text = "No saved books"
        ){
            Text(text = "No saved books",
                modifier = Modifier.padding(30.dp),
                textAlign = TextAlign.Center)
        }
    } else {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(3),
            verticalItemSpacing = 5.dp,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.fillMaxSize().padding(10.dp)
        ) {
            items(books) { book ->
                book.coverImage?.let {
                        imageUrl ->
                    if (imageUrl.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .height(200.dp)
                                .fillMaxWidth()
                                .clickable { onBookClick(book.bookId.toString()) }
                        ) {
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = "Book cover",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
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
