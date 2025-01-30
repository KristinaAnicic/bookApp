package com.example.booksapp.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.booksapp.databinding.ViewholderBookBinding
import com.example.booksapp.model.Book

class BookListAdapter (private val bookList: List<Book>) : RecyclerView.Adapter<BookListAdapter.ViewHolder>() {

    var onItemClickListener: ((Book) -> Unit)? = null

    inner class ViewHolder(val binding: ViewholderBookBinding) :
            RecyclerView.ViewHolder(binding.root){
            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ViewholderBookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = bookList[position]
        holder.binding.apply {
            titleTxt.text = currentItem.title
            //ratingBar2.rating = currentItem.
            bookAuthorTxt.text = "by ${currentItem.author}"
            Glide.with(holder.itemView.context)
                .load(currentItem.book_image)
                .into(pic)
            root.setOnClickListener(){
                onItemClickListener?.invoke(currentItem)
            }
        }
    }

}
