package com.example.bookexplorer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookexplorer.data.model.BookWork
import com.example.bookexplorer.data.repository.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val books: List<BookWork>, val isLoadingMore: Boolean, val hasMore: Boolean) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

class HomeViewModel(private val repository: BookRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var currentPage = 0
    private var isLoading = false

    init {
        loadBooks(initial = true)
    }

    fun loadBooks(initial: Boolean = false) {
        if (isLoading) return
        isLoading = true

        if (initial) {
            currentPage = 0
            _uiState.value = HomeUiState.Loading
        } else {
            val currentState = _uiState.value
            if (currentState is HomeUiState.Success) {
                _uiState.value = currentState.copy(isLoadingMore = true)
            }
        }

        viewModelScope.launch {
            val result = repository.getFictionBooks(offset = currentPage * 30)
            result.onSuccess { newBooks ->
                val currentBooks = (_uiState.value as? HomeUiState.Success)?.books ?: emptyList()
                val allBooks = if (initial) newBooks else currentBooks + newBooks
                _uiState.value = HomeUiState.Success(allBooks, isLoadingMore = false, hasMore = newBooks.isNotEmpty())
                if (newBooks.isNotEmpty()) {
                    currentPage++
                }
                isLoading = false
            }.onFailure { e ->
                _uiState.value = HomeUiState.Error(e.localizedMessage ?: "Unknown error")
                isLoading = false
            }
        }
    }
}
