package com.example.bookexplorer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookexplorer.data.model.WorkDetailResponse
import com.example.bookexplorer.data.repository.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

sealed class DetailUiState {
    object Loading : DetailUiState()
    data class Success(val detail: WorkDetailResponse, val isFavorite: Boolean) : DetailUiState()
    data class Error(val message: String) : DetailUiState()
}

class DetailViewModel(
    private val repository: BookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadBook(workId: String) {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            val result = repository.getBookDetail(workId)
            
            result.onSuccess { detail ->
                launch {
                    repository.isFavorite(workId).collect { isFav ->
                         _uiState.value = DetailUiState.Success(detail, isFav)
                    }
                }
            }.onFailure { e ->
                _uiState.value = DetailUiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun toggleFavorite(workId: String) {
        val currentState = _uiState.value
        if (currentState is DetailUiState.Success) {
            viewModelScope.launch {
                if (currentState.isFavorite) {
                    repository.removeFavorite(workId)
                } else {
                    repository.addFavorite(workId, currentState.detail)
                }
            }
        }
    }
}
