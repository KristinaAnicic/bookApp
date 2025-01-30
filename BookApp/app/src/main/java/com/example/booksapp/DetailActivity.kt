package com.example.booksapp

import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.booksapp.databinding.ActivityDetailBinding
import com.example.booksapp.utils.RetrofitInstance
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Retrofit
import java.io.IOException

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
    private lateinit var detailBinding: ActivityDetailBinding
    private lateinit var loading: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()
        detailBinding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(detailBinding.root)

        val isbn = intent.getStringExtra("ISBN")
        //setContentView(R.layout.activity_detail)
        detailBinding.backImageView.setOnClickListener {
            finish()
        }

        loading = detailBinding.progressBar
        loading.visibility = View.VISIBLE

        detailBinding.linearLayout.visibility = View.GONE
        detailBinding.scrollView3.visibility = View.GONE

        lifecycleScope.launch(Dispatchers.Main) {
            try{
                val responseId = RetrofitInstance.api_Books.getBookByIsbn("isbn:$isbn")
                if(responseId.isSuccessful && responseId.body() != null){
                    val bookId = responseId.body()!!.items[0].id
                    val response = RetrofitInstance.api_Books.getBookById(bookId)

                    if(response.isSuccessful && responseId.body() != null){
                        val bookItem = response.body()!!.volumeInfo
                        detailBinding.apply {
                            Glide.with(this@DetailActivity)
                                .load("${bookItem.imageLinks.thumbnail}&&fife=w800")
                                .into(bookCover)
                            Log.d("BookItem", "Thumbnail URL: ${bookItem.imageLinks.thumbnail}")
                            bookTitleTxt.text = bookItem.title
                            authorTxt.text = bookItem.authors.joinToString(", ")
                            publishDateTxt.text = bookItem.publishedDate
                            publisherTxt.text = bookItem.publisher
                            //bookDescriptionTxt.text = Html.fromHtml(bookItem.description, Html.FROM_HTML_MODE_LEGACY)
                            categoriesTxt.text = bookItem.categories[0]
                            pagesCount.text = bookItem.pageCount.toString()

                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                bookDescriptionTxt.text = Html.fromHtml(bookItem.description, Html.FROM_HTML_MODE_LEGACY)
                            } else {
                                bookDescriptionTxt.text = Html.fromHtml(bookItem.description)
                            }
                        }
                    }
                    loading.visibility = View.GONE
                    detailBinding.linearLayout.visibility = View.VISIBLE
                    detailBinding.scrollView3.visibility = View.VISIBLE

                }
                else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Failed to load books", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: IOException) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Network error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } catch (e: HttpException) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Server error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/
    }
}