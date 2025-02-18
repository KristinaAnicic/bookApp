package com.example.booksapp.Components
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.booksapp.data.database.Entities.Book
import com.example.booksapp.data.database.Entities.BookWithDetails
import com.example.booksapp.utils.ReadingFormatEnum
import com.example.booksapp.utils.ReadingStatusEnum
import com.example.booksapp.viewModel.BooksViewModel
import java.util.Date
import java.util.Locale

@Composable
fun AddBookDialog(book : BookWithDetails? = null,
                  booksViewModel: BooksViewModel,
                  onDismissRequest: () -> Unit,
                  onConfirmation: () -> Unit
) {
    val context = LocalContext.current
    var bookId by remember { mutableStateOf(book?.book?.bookId) }
    var title by remember { mutableStateOf(book?.book?.title ?: "") }
    var author by remember { mutableStateOf(book?.book?.author ?: "") }
    var image by remember { mutableStateOf(book?.book?.coverImage ?: "") }
    var description by remember { mutableStateOf(book?.book?.description  ?: "") }
    var publisher by remember { mutableStateOf(book?.book?.publisher  ?: "") }
    var publishDate by remember { mutableStateOf(book?.book?.publishDate  ?: "") }
    var numberOfPages by remember { mutableStateOf((book?.book?.numberOfPages ?: 0).toString()) }
    var numberOfChapters by remember { mutableStateOf((book?.book?.numberOfChapters ?: 0).toString()) }
    var readPages by remember { mutableStateOf((book?.book?.readPagesCount ?: 0).toString()) }
    var readChapters by remember { mutableStateOf((book?.book?.readChaptersCount ?: 0).toString()) }
    var readingStatusId by remember { mutableStateOf(book?.book?.readingStatusId ?: ReadingStatusEnum.PLAN_TO_READ.id) }
    var readingFormat by remember { mutableStateOf(book?.format?.formatName ?: ReadingFormatEnum.PAPERBACK.format_name) }
    var readingFormatId by remember { mutableStateOf(book?.format?.formatId ?: ReadingFormatEnum.PAPERBACK.id) }
    var startDate by remember { mutableStateOf(book?.book?.startDate) }
    var endDate by remember { mutableStateOf(book?.book?.endDate) }
    var trackChapters by remember { mutableStateOf(book?.book?.trackChapters ?: false) }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainer),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                Text(
                    text = "Add book",
                    modifier = Modifier.padding(12.dp),
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .background(MaterialTheme.colorScheme.surfaceContainer),
                    verticalArrangement = Arrangement.spacedBy(10.dp)

                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title *") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4
                    )
                    OutlinedTextField(
                        value = author,
                        onValueChange = { author = it },
                        label = { Text("Author *") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                    OutlinedTextField(
                        value = image,
                        onValueChange = { image = it },
                        label = { Text("Image link") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier
                            .fillMaxWidth(),
                        maxLines = 6
                    )

                    OutlinedTextField(
                        value = publishDate,
                        onValueChange = { publishDate = it },
                        label = { Text("Publish Date") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 1
                    )

                    OutlinedTextField(
                        value = publisher,
                        onValueChange = { publisher = it },
                        label = { Text("Publisher") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )
                    OutlinedTextField(
                        value = numberOfPages,
                        onValueChange = { newValue ->
                            if (newValue.matches(Regex("^\\d*$"))) {
                                numberOfPages = newValue
                            }
                        },
                        label = { Text("Number of pages *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.width(150.dp)
                    )
                    OutlinedTextField(
                        value = readPages,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text("Pages read") },
                        modifier = Modifier.width(150.dp),
                        onValueChange = { newValue ->
                            if (newValue.matches(Regex("^\\d*$"))) {
                                val newPage = newValue.ifEmpty { "0" }.toInt()
                                readPages = if (newPage > Integer.parseInt(numberOfPages)) {
                                    numberOfPages
                                } else {
                                    newValue
                                }
                            }
                        },
                    )
                    OutlinedTextField(
                        value = numberOfChapters,
                        onValueChange = { newValue ->
                            if (newValue.matches(Regex("^\\d*$"))) {
                                numberOfChapters = newValue
                            }
                        },
                        label = { Text("Number of chapters") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.width(150.dp)
                    )

                    OutlinedTextField(
                        value = readChapters,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text("Chapters read") },
                        modifier = Modifier.width(150.dp),
                        onValueChange = { newValue ->
                            if (newValue.matches(Regex("^\\d*$"))) {
                                val newChapterValue = newValue.ifEmpty { "0" }.toInt()
                                readChapters =
                                    if (newChapterValue > Integer.parseInt(numberOfChapters)) {
                                        numberOfChapters
                                    } else {
                                        newValue
                                    }
                            }
                        },
                    )

                    Text(
                        text = "In which format are you reading the book: "
                    )

                    CustomDropDown(
                        items = ReadingFormatEnum.values().map { it.format_name },
                        selected = readingFormat,
                        onItemSelected = { newFormat ->
                            readingFormat = newFormat
                            readingFormatId = ReadingFormatEnum.getIdByName(newFormat)
                                ?: ReadingFormatEnum.PAPERBACK.id
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "Track progress by: "
                    )
                    CustomDropDown(
                        items = listOf("Pages", "Chapters"),
                        selected = if (trackChapters) "Chapters" else "Pages",
                        onItemSelected = { newFormat ->
                            trackChapters = newFormat == "Chapters"
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(3.dp),
                    ) {
                        Text("Dismiss")
                    }
                    TextButton(
                        onClick = {
                            /*onConfirmation(Integer.parseInt(page))*/
                            if (title.isNotEmpty() && author.isNotEmpty() && Integer.parseInt(numberOfPages) > 0) {
                                if (trackChapters == true && numberOfChapters <= "0") {
                                    Toast.makeText(context, "Number of chapters has to be greater than 0", Toast.LENGTH_SHORT).show()
                                } else {
                                    if (readPages == numberOfPages) {
                                        readingStatusId = ReadingStatusEnum.COMPLETED.id
                                        startDate = Date()
                                        endDate = Date()
                                    } else if (Integer.parseInt(readPages) > 0 && Integer.parseInt(readPages) < Integer.parseInt(numberOfPages)) {
                                        readingStatusId = ReadingStatusEnum.CURRENTLY_READING.id
                                        startDate = Date()
                                        endDate = null
                                    } else {
                                        readingStatusId = ReadingStatusEnum.PLAN_TO_READ.id
                                        startDate = null
                                        endDate = null
                                    }
                                    val formattedTitle = title.split(" ").joinToString(" ") { word ->
                                        word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(
                                            Locale.getDefault()) else it.toString() }
                                    }
                                    title = formattedTitle

                                    val formattedAuthor = author.split(" ").joinToString(" ") { word ->
                                        word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(
                                            Locale.getDefault()) else it.toString() }
                                    }
                                    author = formattedAuthor

                                    val newBook = book?.book?.copy(
                                        title = title,
                                        author = author,
                                        coverImage = image,
                                        description = description.ifEmpty { null },
                                        publisher = publisher.ifEmpty { null },
                                        publishDate = publishDate.ifEmpty { null },
                                        numberOfPages = Integer.parseInt(numberOfPages),
                                        numberOfChapters = numberOfChapters.toIntOrNull() ?: 0,
                                        readingStatusId = readingStatusId,
                                        readingFormatId = readingFormatId,
                                        startDate = startDate,
                                        endDate = endDate,
                                        readPagesCount = readPages.toIntOrNull() ?: 0,
                                        readChaptersCount = readChapters.toIntOrNull() ?: 0,
                                        trackChapters = trackChapters
                                    ) ?: Book(
                                        title = title,
                                        author = author,
                                        coverImage = image,
                                        description = description.ifEmpty { null },
                                        publisher = publisher.ifEmpty { null },
                                        numberOfPages = Integer.parseInt(numberOfPages),
                                        numberOfChapters = numberOfChapters.toIntOrNull() ?: 0,
                                        readingStatusId = readingStatusId,
                                        readingFormatId = readingFormatId,
                                        startDate = startDate,
                                        endDate = endDate,
                                        readPagesCount = readPages.toIntOrNull() ?: 0,
                                        readChaptersCount = readChapters.toIntOrNull() ?: 0,
                                        trackChapters = trackChapters
                                    )
                                    booksViewModel.upsertBook(newBook)
                                    onConfirmation()
                                }
                            }
                            else{
                                Toast.makeText(context, "Fill the required fields", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.padding(3.dp),
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}
