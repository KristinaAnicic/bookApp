package com.example.booksapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.booksapp.Adapter.BookListAdapter
import com.example.booksapp.R
import com.example.booksapp.databinding.FragmentDiscoverBooksBinding
import com.example.booksapp.model.Book
import com.example.booksapp.utils.BestsellerBookList
import com.example.booksapp.viewModel.BestsellerApiViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@AndroidEntryPoint
class DiscoverBooksFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentDiscoverBooksBinding
    private lateinit var bookListAdapter: BookListAdapter

    private lateinit var bestSellersApiViewModel: BestsellerApiViewModel

    private lateinit var loading1: ProgressBar
    private lateinit var loading2: ProgressBar
    private lateinit var loading3: ProgressBar
    private lateinit var loading4: ProgressBar

    private lateinit var rvFictionBooks: RecyclerView
    private lateinit var rvNonFictionBooks: RecyclerView
    private lateinit var rvAdviceBooks: RecyclerView
    private lateinit var rvYoungAdultBooks: RecyclerView

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
        //return inflater.inflate(R.layout.fragment_discover_books, container, false)
        binding = FragmentDiscoverBooksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicijalizacija pogleda
        loading1 = binding.loading1
        loading2 = binding.loading2
        loading3 = binding.loading3
        loading4 = binding.loading4

        rvFictionBooks = binding.rvFictionBookList
        rvNonFictionBooks = binding.rvNonfictionBookList
        rvAdviceBooks = binding.rvAdviceBookList
        rvYoungAdultBooks = binding.rvYoungAdultBookList

        //bestSellersViewModel = ViewModelProvider(this).get(BestsellersViewModel::class.java)
        bestSellersApiViewModel = ViewModelProvider(this).get(BestsellerApiViewModel::class.java)


        // Početno stanje (loading)
        loading1.visibility = View.VISIBLE
        loading2.visibility = View.VISIBLE
        loading3.visibility = View.VISIBLE
        loading4.visibility = View.VISIBLE


        val fictionUrl = getString(R.string.url_fiction)
        val nonFictionUrl = getString(R.string.url_nonfiction)
        val adviceUrl = getString(R.string.url_advice)
        val youngAdultUrl = getString(R.string.url_youngAdult)

        if (BestsellerBookList.areBooksLoaded) {
            setupRecyclerView(rvFictionBooks, BestsellerBookList.fictionBooks, loading1)
            setupRecyclerView(rvNonFictionBooks, BestsellerBookList.nonFictionBooks, loading2)
            setupRecyclerView(rvAdviceBooks, BestsellerBookList.adviceBooks, loading3)
            setupRecyclerView(rvYoungAdultBooks, BestsellerBookList.youngAdultBooks, loading4)

        } else {
            bestSellersApiViewModel.loadBooksFromApi(fictionUrl, nonFictionUrl, adviceUrl, youngAdultUrl)

            bestSellersApiViewModel.fictionBooks.observe(viewLifecycleOwner, { books ->
                setupRecyclerView(rvFictionBooks, books, loading1)
            })

            bestSellersApiViewModel.nonFictionBooks.observe(viewLifecycleOwner, { books ->
                setupRecyclerView(rvNonFictionBooks, books, loading2)
            })

            bestSellersApiViewModel.adviceBooks.observe(viewLifecycleOwner, { books ->
                setupRecyclerView(rvAdviceBooks, books, loading3)
            })

            bestSellersApiViewModel.youngAdultBooks.observe(viewLifecycleOwner, { books ->
                setupRecyclerView(rvYoungAdultBooks, books, loading4)
            })


            // Učitaj podatke za sve kategorije
            /*lifecycleScope.launch(Dispatchers.IO) {
            loadBooks(
                endpoint = getString(R.string.url_fiction),
                recyclerView = rvFictionBooks,
                progressBar = loading1
            )

            loadBooks(
                endpoint = getString(R.string.url_nonfiction),
                recyclerView = rvNonFictionBooks,
                progressBar = loading2
            )

            loadBooks(
                endpoint = getString(R.string.url_advice),
                recyclerView = rvAdviceBooks,
                progressBar = loading3
            )

            loadBooks(
                endpoint = getString(R.string.url_youngAdult),
                recyclerView = rvYoungAdultBooks,
                progressBar = loading4
            )
        }*/
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView, books: List<Book>, progressBar: ProgressBar) {
        recyclerView.apply {
            bookListAdapter = BookListAdapter(books)
            adapter = bookListAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
        progressBar.visibility = View.GONE


        bookListAdapter.onItemClickListener = { bookItem ->
            val fragment = BookDetailFragment.newInstance(bookItem.primary_isbn13)

            // Zamijeni trenutni fragment s DetailFragment
            val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.addToBackStack(null) // Omogućava navigaciju unazad
            transaction.commit()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DiscoverBooksFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}