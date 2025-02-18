package com.example.booksapp.Components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.booksapp.R

@Composable
fun CustomDropDown(
    items: List<String>,
    selected: String? = null,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Select",
    backgroundColor: Color = Color.Transparent,
    textColor: Color = colorResource(R.color.custom_text_color)
){
    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf(selected) }

    LaunchedEffect(selected) {
        selectedItem = selected
    }
    Box(modifier = modifier) {
        TextButton(onClick = { expanded = true },
            colors = ButtonDefaults.textButtonColors(
                containerColor = backgroundColor,
                contentColor = textColor),
            modifier = Modifier.border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(25.dp))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(15.dp),
            ) {
                Text(
                    text = selectedItem ?: placeholder,
                    modifier = Modifier.padding(start= 10.dp)
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Dropdown arrow",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        selectedItem = item
                        expanded = false
                        onItemSelected(item)
                    }
                )
            }
        }
    }
}