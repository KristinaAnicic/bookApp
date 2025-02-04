package com.example.booksapp.Fragments

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import coil.compose.AsyncImage
import com.example.booksapp.Components.rememberAnnotatedDescription
import com.example.booksapp.Components.rememberParsedDescription
import com.example.booksapp.MainActivity
import com.example.booksapp.R
import com.example.booksapp.data.database.Entities.Book
import com.example.booksapp.data.database.Entities.BookWithDetails
import com.example.booksapp.ui.theme.BooksAppTheme
import com.example.booksapp.viewModel.BooksViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

private const val ARG_ID = "id"

@AndroidEntryPoint
class SavedBookDetailFragment : Fragment() {
    // TODO: Rename and change types of parameters

    var bookId:Long? = null
    private val booksViewModel: BooksViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            //id = it.getString(ARG_ID).toLong()
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        bookId = arguments?.getString("ID")?.toLong()
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_saved_book_detail, container, false)

        return ComposeView(requireContext()).apply{
            setContent {
                BooksAppTheme {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        content = {
                            BookDetailScreen(booksViewModel, bookId,
                                onBackPressed = { requireActivity().onBackPressed() })
                        }
                    )
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(id: String) =
            SavedBookDetailFragment().apply {
                arguments = Bundle().apply {
                    putString("ID", id)
                }
            }
    }
}

@Composable
fun BookDetailScreen(booksViewModel:BooksViewModel, id: Long?,onBackPressed: () -> Unit) {
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
fun BookTopNavigation(booksViewModel: BooksViewModel,book : BookWithDetails?,onBackPressed: () -> Unit){
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

@Composable
fun BoldNormalText(boldText: String, normalText: String) {
    Text(
        text = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("${boldText}: ")
            }
            withStyle(
                style = SpanStyle(
                    fontSize = 14.sp)) {
                append("  $normalText")
            }
        },
        modifier = Modifier.padding(start = 5.dp, top = 5.dp)
    )
}

@Composable
fun MoreOptionsMenu(isFavorite : Boolean?,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val favoriteText = remember(isFavorite) {
        if (isFavorite == true) "Remove from favorites" else "Add to favorite"
    }

    val imageVector = remember(isFavorite) {
        if (isFavorite == true) Icons.Default.Favorite else Icons.Default.FavoriteBorder
    }

    Box(modifier = Modifier
        .wrapContentSize(Alignment.TopEnd)) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More options",
                modifier = Modifier
                    .size(150.dp)
                    .padding(top = 8.dp, bottom = 8.dp))
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Edit") },
                leadingIcon = {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                },
                onClick = {
                    expanded = false
                    onEditClick()
                }
            )
            DropdownMenuItem(
                text = { Text(favoriteText) },
                leadingIcon = {
                    Icon(imageVector, contentDescription = "Favorite")
                },
                onClick = {
                    expanded = false
                    onFavoriteClick()
                }
            )
            DropdownMenuItem(
                text = { Text("Delete") },
                leadingIcon = {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                },
                onClick = {
                    expanded = false
                    onDeleteClick()
                }
            )
        }
    }
}