package com.example.booksapp.utils

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class RetryInterceptor : Interceptor {
    private val maxRetryCount = 3
    private val retryDelayMillis = 2000L  // Početno kašnjenje za retry

    override fun intercept(chain: Interceptor.Chain): Response {
        var attempt = 0
        var response: Response?
        var delayMillis = retryDelayMillis

        while (attempt < maxRetryCount) {
            try {
                response = chain.proceed(chain.request())

                // Ako je odgovor uspješan, odmah vrati odgovor
                if (response.isSuccessful) {
                    return response
                }

                // Ako je greška zbog kvote, napraviti veći interval između pokušaja
                if (response.code == 429) { // 429 znači Rate Limit Exceeded
                    attempt++
                    Thread.sleep(delayMillis)
                    delayMillis *= 2 // Povećaj kašnjenje nakon svakog neuspješnog pokušaja
                    continue
                }

                // Za sve druge greške, izađi iz petlje
                break
            } catch (e: Exception) {
                attempt++
                if (attempt >= maxRetryCount) {
                    throw e // Ako dođe do maksimalnog broja pokušaja, izbacujemo grešku
                }
                Thread.sleep(delayMillis)
                delayMillis *= 2 // Povećaj kašnjenje između pokušaja
            }
        }

        // Ako se premaši kvota, vrati grešku
        throw IOException("Rate limit exceeded after $maxRetryCount attempts.")
    }
}