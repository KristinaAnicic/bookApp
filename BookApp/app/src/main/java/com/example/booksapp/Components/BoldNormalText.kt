package com.example.booksapp.Components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BoldNormalText(boldText: String, normalText: String) {
    Text(
        text = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("${boldText}: ")
            }
            withStyle(
                style = SpanStyle(
                    fontSize = 14.sp)
            ) {
                append("  $normalText")
            }
        },
        modifier = Modifier.padding(start = 5.dp, top = 5.dp)
    )
}