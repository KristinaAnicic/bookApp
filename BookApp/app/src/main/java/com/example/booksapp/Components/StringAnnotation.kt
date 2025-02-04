package com.example.booksapp.Components

import android.graphics.Typeface
import android.text.Spanned
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.core.text.HtmlCompat

@Composable
fun rememberParsedDescription(html: String?): Spanned {
    return remember(html) {
        val descriptionText = html ?: ""
        HtmlCompat.fromHtml(descriptionText, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }
}

// Helper funkcija za konverziju u AnnotatedString
@Composable
fun rememberAnnotatedDescription(description: Spanned): AnnotatedString {
    return remember(description) {
        buildAnnotatedString {
            append(description.toString())
            description.getSpans(0, description.length, Any::class.java).forEach { span ->
                val start = description.getSpanStart(span)
                val end = description.getSpanEnd(span)

                when (span) {
                    is StyleSpan -> handleStyleSpan(span, start, end)
                    is UnderlineSpan -> addStyle(
                        SpanStyle(textDecoration = TextDecoration.Underline),
                        start,
                        end
                    )
                }
            }
        }
    }
}

// Pomoćna funkcija za obradu StyleSpan-ova
private fun AnnotatedString.Builder.handleStyleSpan(
    span: StyleSpan,
    start: Int,
    end: Int
) {
    when (span.style) {
        Typeface.BOLD -> addStyle(SpanStyle(fontWeight = FontWeight.Bold), start, end)
        Typeface.ITALIC -> addStyle(SpanStyle(fontStyle = FontStyle.Italic), start, end)
        Typeface.BOLD_ITALIC -> addStyle(
            SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic),
            start,
            end
        )
    }
}