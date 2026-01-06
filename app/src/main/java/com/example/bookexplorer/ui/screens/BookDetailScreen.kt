package com.example.bookexplorer.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.bookexplorer.data.model.WorkDetailResponse
import com.example.bookexplorer.ui.components.ErrorView
import com.example.bookexplorer.ui.components.LoadingView
import com.example.bookexplorer.ui.viewmodel.DetailUiState
import com.example.bookexplorer.ui.viewmodel.DetailViewModel
import com.example.bookexplorer.data.model.getDescriptionString

@Composable
fun BookDetailScreen(
    workId: String,
    viewModel: DetailViewModel
) {
    LaunchedEffect(workId) {
        viewModel.loadBook(workId)
    }

    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is DetailUiState.Loading -> LoadingView()
        is DetailUiState.Error -> ErrorView(message = state.message, onRetry = { viewModel.loadBook(workId) })
        is DetailUiState.Success -> {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                val coverId = state.detail.covers?.firstOrNull()
                val coverUrl = coverId?.let { "https://covers.openlibrary.org/b/id/$it-L.jpg" }

                if (coverUrl != null) {
                    AsyncImage(
                        model = coverUrl,
                        contentDescription = state.detail.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentScale = ContentScale.Fit
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = state.detail.title, style = MaterialTheme.typography.headlineMedium)
                
                state.detail.firstPublishDate?.let {
                    Text(text = "First published: $it", style = MaterialTheme.typography.bodyMedium)
                }

                state.detail.numberOfPages?.let {
                    Text(text = "Pages: $it", style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.toggleFavorite(workId) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = if (state.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = if (state.isFavorite) "Remove from Favorites" else "Add to Favorites")
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(text = "Description", style = MaterialTheme.typography.titleMedium)
                Text(text = state.detail.getDescriptionString(), style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
