package com.example.booksapp.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import com.example.booksapp.Components.BookDetailScreen
import com.example.booksapp.ui.theme.BooksAppTheme
import com.example.booksapp.viewModel.BooksViewModel
import dagger.hilt.android.AndroidEntryPoint

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