package com.example.bookexplorer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookexplorer.data.model.BookWork
import com.example.bookexplorer.data.repository.BookRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class HomeUiState {
    data class Loading(val searchQuery: String = "", val isRefreshing: Boolean = false) : HomeUiState()
    data class Success(
        val books: List<BookWork>,
        val isLoadingMore: Boolean,
        val hasMore: Boolean,
        val isRefreshing: Boolean = false,
        val searchQuery: String = ""
    ) : HomeUiState()
    data class Error(val message: String, val searchQuery: String = "", val isRefreshing: Boolean = false) : HomeUiState()
}

val HomeUiState.isRefreshing: Boolean
    get() = when (this) {
        is HomeUiState.Loading -> isRefreshing
        is HomeUiState.Success -> isRefreshing
        is HomeUiState.Error -> isRefreshing
    }

val HomeUiState.searchQuery: String
    get() = when (this) {
        is HomeUiState.Loading -> searchQuery
        is HomeUiState.Success -> searchQuery
        is HomeUiState.Error -> searchQuery
    }

class HomeViewModel(private val repository: BookRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var currentPage = 0
    private var isLoading = false
    private var searchJob: Job? = null

    init {
        loadBooks(initial = true)
    }

    fun onSearchQueryChanged(query: String) {
        val currentQuery = _uiState.value.searchQuery
        if (currentQuery == query) return

        _uiState.value = when (val state = _uiState.value) {
            is HomeUiState.Loading -> state.copy(searchQuery = query)
            is HomeUiState.Success -> state.copy(searchQuery = query)
            is HomeUiState.Error -> state.copy(searchQuery = query)
        }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            loadBooks(initial = true)
        }
    }

    fun refresh() {
        loadBooks(initial = true, isRefreshing = true)
    }

    fun loadBooks(initial: Boolean = false, isRefreshing: Boolean = false) {
        if (isLoading) return
        isLoading = true

        val currentQuery = _uiState.value.searchQuery

        if (initial) {
            currentPage = 0
            _uiState.value = HomeUiState.Loading(searchQuery = currentQuery, isRefreshing = isRefreshing)
        } else {
            val currentState = _uiState.value
            if (currentState is HomeUiState.Success) {
                _uiState.value = currentState.copy(isLoadingMore = true)
            }
        }

        viewModelScope.launch {
            if (initial) delay(2000)
            
            val result = try {
                if (currentQuery.isBlank()) {
                    repository.getFictionBooks(offset = currentPage * 30)
                } else {
                    repository.searchBooks(currentQuery, page = currentPage + 1)
                }
            } finally {
                isLoading = false
            }

            result.onSuccess { newBooks ->
                val currentBooks = (_uiState.value as? HomeUiState.Success)?.books ?: emptyList()
                val allBooks = if (initial) newBooks else currentBooks + newBooks
                
                _uiState.value = HomeUiState.Success(
                    books = allBooks,
                    isLoadingMore = false,
                    hasMore = newBooks.size >= 20,
                    isRefreshing = false,
                    searchQuery = currentQuery
                )
                
                if (newBooks.isNotEmpty()) {
                    currentPage++
                }
            }.onFailure { e ->
                _uiState.value = HomeUiState.Error(
                    message = e.localizedMessage ?: "Unknown error",
                    searchQuery = currentQuery
                )
            }
        }
    }
}
