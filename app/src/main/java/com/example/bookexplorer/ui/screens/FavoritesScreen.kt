package com.example.bookexplorer.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.bookexplorer.ui.components.LoadingView
import com.example.bookexplorer.ui.viewmodel.FavoriteBookUiModel
import com.example.bookexplorer.ui.viewmodel.FavoritesUiState
import com.example.bookexplorer.ui.viewmodel.FavoritesViewModel

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onBookClick: (String, String) -> Unit
) {


    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is FavoritesUiState.Loading -> LoadingView()
        is FavoritesUiState.Empty -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No favorites yet.")
            }
        }
        is FavoritesUiState.Success -> {
            LazyColumn {
                items(state.favorites) { book ->
                    FavoriteBookItem(book = book, onClick = { onBookClick(book.id, book.title) })
                }
            }
        }
    }
}

@Composable
fun FavoriteBookItem(book: FavoriteBookUiModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            val coverUrl = book.coverId?.let { "https://covers.openlibrary.org/b/id/$it-M.jpg" }

            AsyncImage(
                model = coverUrl,
                contentDescription = book.title,
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 16.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                Text(text = book.title, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
