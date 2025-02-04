package com.example.booksapp.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import com.example.booksapp.Components.BookListGrid
import com.example.booksapp.MainActivity
import com.example.booksapp.R
import com.example.booksapp.data.database.ReadingStatusEnum
import com.example.booksapp.ui.theme.BooksAppTheme
import com.example.booksapp.viewModel.BooksViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AllSavedBooksFragment : Fragment() {
    private val booksViewModel: BooksViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply{
            setContent {
                BooksAppTheme {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        content = {
                            val books by booksViewModel.booksByStatus.collectAsState(initial = emptyList());
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                            ) {
                                Spacer(modifier = Modifier.padding(8.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    SingleChoiceButton(booksViewModel)
                                }
                                Spacer(modifier = Modifier.padding(8.dp))
                                BookListGrid(books,
                                    onBookClick = { bookId ->
                                        (activity as? MainActivity)?.openBookDetailFragment(bookId)
                                    })
                            }
                        })
                }
            }
        }
    }
}


@Composable
fun SingleChoiceButton(viewModel: BooksViewModel) {
    val selectedStatus by viewModel.selectedStatus.collectAsState()

    val options = listOf(
        "All",
        ReadingStatusEnum.CURRENTLY_READING,
        ReadingStatusEnum.PLAN_TO_READ,
        ReadingStatusEnum.COMPLETED,
        ReadingStatusEnum.DROPPED
    )
    options.forEach { option ->
        val label = if (option is ReadingStatusEnum) option.status_name else "All"
        val isSelected = when (option) {
            is ReadingStatusEnum -> selectedStatus == option
            else -> selectedStatus == null
        }
        OutlinedButton(
            onClick = {
                viewModel.setStatusType(if (option is ReadingStatusEnum) option else null)
            },
            modifier = Modifier
                .padding(vertical = 4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSelected) colorResource(R.color.teal_700) else Color.Transparent,
                contentColor = if (isSelected) Color.White else colorScheme.onSurface
            ),
        ) {
            Text(text = label)
        }
    }
}