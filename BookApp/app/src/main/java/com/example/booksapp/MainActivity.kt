package com.example.booksapp

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.booksapp.Fragments.AllSavedBooksFragment
import com.example.booksapp.Fragments.BookDetailFragment
import com.example.booksapp.Fragments.DiscoverBooksFragment
import com.example.booksapp.Fragments.HomePageFragment
import com.example.booksapp.Fragments.SavedBookDetailFragment
import com.example.booksapp.Fragments.SavedBooksFragment
import com.example.booksapp.Fragments.SearchFragment
import com.example.booksapp.databinding.ActivityMainBinding
import com.example.booksapp.ui.theme.BooksAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.activity_main)
        searchView = binding.editSearch

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(s: String): Boolean {
                return true
            }

            override fun onQueryTextSubmit(search: String): Boolean {
                openSearchFragment(search)
                return true
            }

        })

        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            var selectedFragment: Fragment? = null

            when (item.itemId) {
                R.id.menu_explore -> {
                    searchView.visibility = View.VISIBLE
                    clearSearchBar()
                    selectedFragment = supportFragmentManager.findFragmentByTag("discover")
                        ?: DiscoverBooksFragment()
                }

                R.id.menu_home -> {
                    searchView.visibility = View.GONE
                    clearSearchBar()
                    selectedFragment = supportFragmentManager.findFragmentByTag("home")
                        ?: HomePageFragment()
                }

                R.id.menu_myBooks -> {
                    searchView.visibility = View.GONE
                    clearSearchBar()
                    selectedFragment =
                        supportFragmentManager.findFragmentByTag("saved") ?: SavedBooksFragment()
                }
            }
            if (selectedFragment != null) {
                val fragmentTransaction: FragmentTransaction =
                    supportFragmentManager.beginTransaction()
                fragmentTransaction.replace(
                    R.id.fragment_container,
                    selectedFragment
                )
                fragmentTransaction.commitAllowingStateLoss()
            }

            true
        }
        if (savedInstanceState == null) {
            binding.bottomNavigation.selectedItemId = R.id.menu_explore
        }

    }
//initView()
/*setContent {
    BooksAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Greeting(
                name = "Android",
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}*/

    fun hideBottomNav() {
        binding.bottomNavigation.visibility = View.GONE
    }

    fun showBottomNav() {
        binding.bottomNavigation.visibility = View.VISIBLE
    }

    fun hideSearchBar() {
        searchView.visibility = View.GONE
    }

    fun showSearchBar() {
        searchView.visibility = View.VISIBLE
    }

    fun clearSearchBar() {
        searchView.setQuery("", false)
    }

    fun openSavedBookDetailFragment(bookId: String) {
        searchView.visibility = View.GONE
        val fragment = SavedBookDetailFragment.newInstance(bookId)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null) // Add to back stack for back navigation
            .commit()
    }

    fun openBookDetailFragment(isbn : String?, id: String?){
        searchView.visibility = View.GONE
        val fragment = BookDetailFragment.newInstance(isbn = isbn, id = id)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    fun openAllSavedBooksFragment() {
        searchView.visibility = View.GONE
        val fragment = AllSavedBooksFragment();
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    fun openSearchFragment(searchText : String) {
        searchView.visibility = View.VISIBLE
        val fragment = SearchFragment.newInstance(searchText);
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}



@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
Text(
text = "Hello $name!",
modifier = modifier
)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
BooksAppTheme {
Greeting("Android")
}
}