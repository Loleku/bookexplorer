package com.example.bookexplorer.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.example.bookexplorer.ui.components.BookItem
import com.example.bookexplorer.ui.components.ErrorView
import com.example.bookexplorer.ui.components.SkeletonBookItem
import com.example.bookexplorer.ui.components.SkeletonLoadingView
import com.example.bookexplorer.ui.viewmodel.HomeUiState
import com.example.bookexplorer.ui.viewmodel.HomeViewModel
import com.example.bookexplorer.ui.viewmodel.isRefreshing
import com.example.bookexplorer.ui.viewmodel.searchQuery

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onBookClick: (String, String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val searchQuery = uiState.searchQuery
    val isRefreshing = uiState.isRefreshing

    val pullToRefreshState = rememberPullToRefreshState()
    
    if (pullToRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            viewModel.refresh()
        }
    }

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            pullToRefreshState.startRefresh()
        } else {
            pullToRefreshState.endRefresh()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(pullToRefreshState.nestedScrollConnection)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search books...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            Box(modifier = Modifier.weight(1f)) {
                when (val state = uiState) {
                    is HomeUiState.Loading -> SkeletonLoadingView()
                    is HomeUiState.Error -> ErrorView(
                        message = state.message,
                        onRetry = { viewModel.loadBooks(initial = true) }
                    )
                    is HomeUiState.Success -> {
                        LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
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

                        val shouldLoadMore by remember(state.books.size, state.isLoadingMore, state.hasMore) {
                            derivedStateOf {
                                val layoutInfo = listState.layoutInfo
                                val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                                lastVisibleItemIndex >= layoutInfo.totalItemsCount - 2 && state.hasMore && !state.isLoadingMore
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

        PullToRefreshContainer(
            state = pullToRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}
