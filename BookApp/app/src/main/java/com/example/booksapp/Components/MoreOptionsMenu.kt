package com.example.booksapp.Components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp

@Composable
fun MoreOptionsMenu(isFavorite : Boolean?,
                    isDropped: Boolean?,
                    isDisabled: Boolean = false,
                    onEditClick: () -> Unit,
                    onDeleteClick: () -> Unit,
                    onFavoriteClick: () -> Unit,
                    onDroppedClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val favoriteText = remember(isFavorite) {
        if (isFavorite == true) "Remove from favorites" else "Add to favorite"
    }

    val droppedText = remember(isDropped) {
        if (isDropped == true) "Start reading again" else "Dropped"
    }

    val imageVector = remember(isFavorite) {
        if (isFavorite == true) Icons.Default.Favorite else Icons.Default.FavoriteBorder
    }

    Box(modifier = Modifier
        .wrapContentSize(Alignment.TopEnd)) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More options",
                modifier = Modifier
                    .size(150.dp)
                    .padding(top = 8.dp, bottom = 8.dp))
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Edit") },
                leadingIcon = {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                },
                onClick = {
                    expanded = false
                    onEditClick()
                }
            )
            DropdownMenuItem(
                text = { Text(favoriteText) },
                leadingIcon = {
                    Icon(imageVector, contentDescription = "Favorite")
                },
                onClick = {
                    expanded = false
                    onFavoriteClick()
                }
            )
            DropdownMenuItem(
                text = {
                    Text(
                        text = droppedText,
                        modifier = Modifier.alpha(if (isDisabled) 0.5f else 1f)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = "Dropped",
                        modifier = Modifier.alpha(if (isDisabled) 0.5f else 1f))
                },
                onClick = {
                    if (!isDisabled) {
                        expanded = false
                        onDroppedClick()
                    }
                }
            )
            DropdownMenuItem(
                text = { Text("Delete") },
                leadingIcon = {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                },
                onClick = {
                    expanded = false
                    onDeleteClick()
                }
            )
        }
    }
}