package com.example.booksapp

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.booksapp.Fragments.DiscoverBooksFragment
import com.example.booksapp.Fragments.SavedBooksFragment
import com.example.booksapp.databinding.ActivityMainBinding
import com.example.booksapp.ui.theme.BooksAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.activity_main)

        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            var selectedFragment: Fragment? = null

            when (item.itemId) {
                R.id.menu_explore -> {
                    selectedFragment = supportFragmentManager.findFragmentByTag("discover")
                        ?: DiscoverBooksFragment()
                }

                R.id.menu_home -> {
                    selectedFragment = supportFragmentManager.findFragmentByTag("discover")
                        ?: DiscoverBooksFragment()
                }

                R.id.menu_myBooks -> {
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
                ) // R.id.fragment_container je container u tvom layoutu
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