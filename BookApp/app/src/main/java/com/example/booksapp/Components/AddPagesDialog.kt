package com.example.booksapp.Components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.booksapp.R

@Composable
fun AddPagesDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: (Int) -> Unit,
    numOfPagesOrChapters: Int,
    numOfReadPagesOrChapters: Int
) {
    var page by remember { mutableStateOf(numOfReadPagesOrChapters.toString()) }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Add pages",
                    modifier = Modifier.padding(16.dp),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_horizontal_rule_24),
                        contentDescription = "Add pages",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(start = 4.dp)
                            .clickable{
                                val newValue = (Integer.parseInt(page) - 5)
                                if(newValue < 0)
                                    page = "0"
                                else
                                    page = newValue.toString()
                            },
                        tint = Color.Gray,
                    )
                    OutlinedTextField(
                        value = page,
                        onValueChange = { newValue ->
                            if (newValue.matches(Regex("^\\d*$"))) {
                                val newPage = newValue.ifEmpty { "0" }.toInt()
                                page = if (newPage > numOfPagesOrChapters) {
                                    numOfPagesOrChapters.toString()
                                } else {
                                    newValue
                                }
                            }},
                        label = { Text("Read pages") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.width(150.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add pages",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(start = 4.dp)
                            .clickable{
                                val newValue = (Integer.parseInt(page) + 10)
                                if(newValue > numOfPagesOrChapters)
                                    page = numOfPagesOrChapters.toString()
                                else
                                    page = newValue.toString()
                        },
                        tint = Color.Gray,
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth().padding(20.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton (
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Dismiss")
                    }
                    TextButton(
                        onClick = {
                            onConfirmation(Integer.parseInt(page))
                                  },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}
