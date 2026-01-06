package com.example.bookexplorer.ui.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.example.bookexplorer.ui.components.BookItem
import com.example.bookexplorer.ui.components.ErrorView
import com.example.bookexplorer.ui.components.SkeletonBookItem
import com.example.bookexplorer.ui.components.SkeletonLoadingView
import com.example.bookexplorer.ui.viewmodel.HomeUiState
import com.example.bookexplorer.ui.viewmodel.HomeViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onBookClick: (String, String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    when (val state = uiState) {
        is HomeUiState.Loading -> SkeletonLoadingView()
        is HomeUiState.Error -> ErrorView(message = state.message, onRetry = { viewModel.loadBooks(initial = true) })
        is HomeUiState.Success -> {
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(state = listState) {
                    items(state.books) { book ->
                        BookItem(book = book, onClick = {
                            val id = book.key.removePrefix("/works/")
                            onBookClick(id, book.title)
                        })
                    }

                    if (state.isLoadingMore) {
                        items(3) {
                            SkeletonBookItem()
                        }
                    }
                }

                val shouldLoadMore by remember {
                    derivedStateOf {
                        val layoutInfo = listState.layoutInfo
                        val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                        lastVisibleItemIndex >= layoutInfo.totalItemsCount - 5 && state.hasMore && !state.isLoadingMore
                    }
                }

                LaunchedEffect(shouldLoadMore) {
                    if (shouldLoadMore) {
                        viewModel.loadBooks()
                    }
                }
            }
        }
    }
}
