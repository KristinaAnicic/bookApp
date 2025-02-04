package com.example.booksapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import com.example.booksapp.Components.BookListGrid
import com.example.booksapp.Components.BookListRow
import com.example.booksapp.MainActivity
import com.example.booksapp.R
import com.example.booksapp.data.database.ReadingStatusEnum
import com.example.booksapp.ui.theme.BooksAppTheme
import com.example.booksapp.viewModel.BooksViewModel
import dagger.Component
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@AndroidEntryPoint
class AllSavedBooksFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val booksViewModel: BooksViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_all_saved_books, container, false)

        return ComposeView(requireContext()).apply{
            setContent {
                BooksAppTheme {
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
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AllSavedBooksFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

/*@Composable
fun SingleChoiceButton(viewModel: BooksViewModel) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val options = listOf("All", ReadingStatusEnum.CURRENTLY_READING,
        ReadingStatusEnum.PLAN_TO_READ,ReadingStatusEnum.COMPLETED,ReadingStatusEnum.DROPPED)

    options.forEachIndexed { index, option ->
        val label = if (option is ReadingStatusEnum) option.status_name else "All"
        OutlinedButton(
            onClick = { selectedIndex = index
                viewModel.setStatusType(if (option is ReadingStatusEnum) option else null) },
            modifier = Modifier
                .padding(vertical = 4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedIndex == index) colorResource(R.color.teal_700) else Color.Transparent,
                contentColor = if (selectedIndex == index) Color.White else Color.Black
            ),
        ) {
            Text(text = label)
        }
    }
}*/

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
                contentColor = if (isSelected) Color.White else Color.Black
            ),
        ) {
            Text(text = label)
        }
    }
}

@Composable
fun BookStatusFilter(bookViewModel : BooksViewModel){
    val selectedStatus = bookViewModel.getStatusType()

    Row(
        modifier = Modifier.fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ){
        FilterChip(
            selected = selectedStatus == null,
            onClick = {
                bookViewModel.setStatusType(null)
                      },
            label = { Text("All") }
        )
        ReadingStatusEnum.values().forEach { status ->
            FilterChip(
                selected = selectedStatus == status,
                onClick = {
                    bookViewModel.setStatusType(status)
                },
                label = { Text(status.status_name) })
        }
    }
}