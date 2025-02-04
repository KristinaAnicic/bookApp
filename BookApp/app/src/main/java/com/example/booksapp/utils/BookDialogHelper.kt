package com.example.booksapp.utils

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.booksapp.R
import com.example.booksapp.data.database.Entities.Book
import com.example.booksapp.model.BookDetail.Item
import com.example.booksapp.viewModel.BooksViewModel
import java.util.Date

object BookDialogHelper {
    @SuppressLint("MissingInflatedId")
    fun showAddBookDialog(fragment: Fragment,
                          book: Item,
                          booksViewModel: BooksViewModel,
                          onBookAdded: () -> Unit
    ) {
        val context = fragment.requireContext()
        val dialogView = fragment.layoutInflater.inflate(R.layout.dialog_add_book, null)

        val spinnerFormat = dialogView.findViewById<Spinner>(R.id.readingFormats)

        val formats = listOf(ReadingFormatEnum.PAPERBACK.format_name, ReadingFormatEnum.EBOOK.format_name, ReadingFormatEnum.AUDIOBOOK.format_name)
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, formats)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFormat.adapter = adapter

        val editTextNumOfPages = dialogView.findViewById<EditText>(R.id.numOfPages)
        val editTextPagesRead = dialogView.findViewById<EditText>(R.id.pagesRead)
        val editTextNumOfChapters = dialogView.findViewById<EditText>(R.id.numOfChapters)
        val editTextChaptersRead = dialogView.findViewById<EditText>(R.id.chaptersRead)
        val markAsCompletedBtn = dialogView.findViewById<Button>(R.id.completedButton)

        markAsCompletedBtn.setOnClickListener {
            val numberOfPages = editTextNumOfPages.text.toString().toIntOrNull() ?: 0
            val numberOfChapters = editTextNumOfChapters.text.toString().toIntOrNull() ?: 0

            editTextPagesRead.setText(numberOfPages.toString())
            editTextChaptersRead.setText(numberOfChapters.toString())
        }

        editTextNumOfPages.setText(book.volumeInfo.pageCount?.toString() ?: "0")
        editTextNumOfChapters.setText("0")

        editTextPagesRead.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val pagesRead = s?.toString()?.toIntOrNull() ?: 0
                val numberOfPages = editTextNumOfPages.text.toString().toIntOrNull() ?: 0

                if (pagesRead < 0) {
                    editTextPagesRead.setText("0")
                } else if (pagesRead > numberOfPages) {
                    editTextPagesRead.setText(numberOfPages.toString())
                }
            }
        })

        editTextChaptersRead.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val chaptersRead = s?.toString()?.toIntOrNull() ?: 0
                val numberOfChapters = editTextNumOfChapters.text.toString().toIntOrNull() ?: 0

                if (chaptersRead < 0) {
                    editTextChaptersRead.setText("0")
                } else if (chaptersRead > numberOfChapters) {
                    editTextChaptersRead.setText(numberOfChapters.toString())
                }
            }
        })


        val dialog = AlertDialog.Builder(context)
            .setTitle("Add Book")
            .setView(dialogView)
            .setPositiveButton("Add") { dialog, which ->
                val numberOfPages = editTextNumOfPages.text.toString().toIntOrNull() ?: book.volumeInfo.pageCount
                val pagesRead = editTextPagesRead.text.toString().toIntOrNull() ?: 0
                val numberOfChapters = editTextNumOfChapters.text.toString().toIntOrNull() ?: 0
                val chaptersRead = editTextChaptersRead.text.toString().toIntOrNull() ?: 0

                val selectedFormat = spinnerFormat.selectedItem as String
                val readingFormatEnum = ReadingFormatEnum.entries.find { it.format_name == selectedFormat }
                val readingFormatId = readingFormatEnum?.id ?: ReadingFormatEnum.PAPERBACK.id

                var readingStatusId = ReadingStatusEnum.PLAN_TO_READ.id
                var startDate : Date? = null
                var endDate : Date? = null

                if ((pagesRead == numberOfPages && pagesRead > 0) || (chaptersRead == numberOfChapters && chaptersRead > 0)){
                    readingStatusId = ReadingStatusEnum.COMPLETED.id
                    startDate = Date()
                    endDate = Date()
                }
                else if (pagesRead > 0 || chaptersRead > 0){
                    readingStatusId = ReadingStatusEnum.CURRENTLY_READING.id
                    startDate = Date()
                }

                val updatedBook = book.let { book ->
                    Book(
                        numberOfPages = numberOfPages,
                        readingFormatId = readingFormatId,
                        readPagesCount = pagesRead,
                        numberOfChapters = numberOfChapters,
                        readChaptersCount = chaptersRead,
                        startDate = startDate,
                        endDate = endDate,
                        title = book.volumeInfo.title,
                        author = book.volumeInfo.authors.joinToString(", "),
                        coverImage = "${book.volumeInfo.imageLinks.thumbnail}&&fife=w800",
                        description = book.volumeInfo.description,
                        publishDate = book.volumeInfo.publishedDate,
                        publisher = book.volumeInfo.publisher,
                        readingStatusId = readingStatusId,
                        googleApiId = book.id
                    )
                }

                booksViewModel.upsertBook(updatedBook)

                onBookAdded()
                //bookSaved = true
                //detailBinding.addImageView.setImageResource(R.drawable.baseline_add_box_24)
                //.makeText(requireContext(), "Book added to library", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel") { dialog, which ->
            }
            .create()

        dialog.show()
    }
}