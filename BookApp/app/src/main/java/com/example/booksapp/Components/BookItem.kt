package com.example.booksapp.Components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

@Composable
fun BookItem(
    book : Book,
    color: Color = colorResource(R.color.progress_color),
    onBookClick: (bookId: String) -> Unit,
    onAddPageClick: (numOfPages: Int) -> Unit){

    var showDialog = remember { mutableStateOf(false) }

    val trackTotal = when {
        book.trackChapters -> {
            book.numberOfChapters ?: 0
        }else -> {
            book.numberOfPages ?: 0 }
    }

    val readTotal = when {
        book.trackChapters -> {
            book.readChaptersCount ?: 0
        }else -> {
            book.readPagesCount ?: 0 }
    }

    val text = when {
        book.trackChapters -> {
            "Chapters read: "
        }else -> {
            "Pages read: " }
    }

    val progress = (readTotal.toFloat().div(trackTotal)).coerceIn(0f, 1f) ?: 0f


    Box(
        modifier = Modifier
            .height(180.dp)
            .fillMaxWidth()
            .padding(10.dp)
            .clip(shape = RoundedCornerShape(15.dp, 15.dp, 0.dp, 0.dp))
            .clickable{
                onBookClick(book.bookId.toString())
            }

    ){
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                var imageLoadFailed by remember { mutableStateOf(false) }
                if (book.coverImage.isNotEmpty() && !imageLoadFailed) {
                    AsyncImage(
                        model = book.coverImage,
                        contentDescription = "Book cover",
                        modifier = Modifier.fillMaxHeight()
                            .clickable {
                                onBookClick(book.bookId.toString())
                            },
                        onError = { imageLoadFailed = true }
                    )
                }
                if (imageLoadFailed || book.coverImage.isEmpty()) {
                    BookTitleAndAuthorBox(book = book, onBookClick = { id -> onBookClick(id) })
                }

                Row {
                    ShowTitleAndAuthor(book = book, modifier = Modifier.weight(1f))

                    Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.fillMaxHeight()
                    ){
                        ShowPagesAndAddButton(readTotal = readTotal, trackTotal = trackTotal,
                            addPageDialog = {showDialog.value = true})

                        Text(
                            text = String.format("%.2f", progress * 100) + "%",
                            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp),
                            fontSize = 14.sp,
                            textAlign = TextAlign.End
                        )
                    }
                }

            }
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp),
                color = color,
                trackColor = color.copy(alpha = 0.2f),
            )

        }
        if (showDialog.value) {
            AddPagesDialog(
                onConfirmation = {newPage->
                    showDialog.value = false
                    onAddPageClick(newPage)
                },
                onDismissRequest = {
                    showDialog.value = false
                },
                numOfReadPagesOrChapters = readTotal,
                numOfPagesOrChapters = trackTotal,
                addAndSubstractValue = if(book.trackChapters == true) 1 else 5
            )
        }
    }
}

@Composable
fun ShowTitleAndAuthor(modifier : Modifier = Modifier, book : Book){
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = book.title,
            modifier = Modifier.padding(10.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Text(
            text = book.author,
            modifier = Modifier.padding(start = 10.dp, bottom = 10.dp),
            fontSize = 14.sp
        )
    }
}

@Composable
fun ShowPagesAndAddButton(readTotal : Int, trackTotal: Int, modifier : Modifier = Modifier, addPageDialog:() -> Unit){
    Box(
        modifier = modifier.clickable{
            addPageDialog()
        }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = modifier
        ) {
            Icon(
                //imageVector = Icons.Outlined.Add,
                painter = painterResource(R.drawable.add_circle_svgrepo_com__1_),
                contentDescription = "Add pages",
                modifier = Modifier.size(50.dp).padding(start = 4.dp),
                tint = Color.Gray,
            )
            Text(
                text = "$readTotal/$trackTotal",
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp),
                fontSize = 15.sp,
                textAlign = TextAlign.End
            )
        }
    }
}