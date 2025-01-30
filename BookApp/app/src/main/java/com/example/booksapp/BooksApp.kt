package com.example.booksapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
public class BooksApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}