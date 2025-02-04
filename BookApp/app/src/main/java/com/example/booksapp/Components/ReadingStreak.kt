package com.example.booksapp.Components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.booksapp.data.database.Entities.ReadingStreak
import com.example.booksapp.ui.theme.BooksAppTheme
import com.example.booksapp.viewModel.BooksViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun Streak(booksViewModel: BooksViewModel){
    val streak by booksViewModel.readingStreak.collectAsState(initial = null)

    Text(
        text="Stats",
        modifier = Modifier.padding(8.dp),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
    )

    Row(
        modifier = Modifier.fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        StreakCard("WEEKLY STREAK", (streak?.consecutiveReadingWeeks ?: 0).toString(), 120.dp)
        StreakCard("DAILY STREAK", (streak?.consecutiveReadingDays ?: 0).toString(), 120.dp)
        StreakCard("LAST READING DATE", (streak?.lastReadDate ?: "no record").toString(), 160.dp)
    }
}

@Composable
fun StreakCard(title: String, value: String, width: Dp) {
    OutlinedCard(
        modifier = Modifier
            .width(width)
            .fillMaxHeight(),
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun StreakPreview() {
    BooksAppTheme {
        StreakPreviewContent()
    }
}

fun parseDate(dateString: String): Date {
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return format.parse(dateString) ?: throw IllegalArgumentException("Invalid date format: $dateString")
}

@Composable
fun StreakPreviewContent() {
    val mockStreak = ReadingStreak(
        consecutiveReadingWeeks = 5,
        consecutiveReadingDays = 30,
        lastReadDate = parseDate("2025-01-29")
    )

    Column {
        Text(
            text = "STATS",
            modifier = Modifier.padding(all = 8.dp),
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StreakCard("WEEKLY STREAK", mockStreak.consecutiveReadingWeeks.toString(), 100.dp)
            StreakCard("DAILY STREAK", mockStreak.consecutiveReadingDays.toString(), 100.dp)
            StreakCard("LAST READING DATE", mockStreak.lastReadDate.toString(), 120.dp)
        }
    }
}