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

sealed class FavoritesUiState {
    object Loading : FavoritesUiState()
    data class Success(val favorites: List<FavoriteBookUiModel>) : FavoritesUiState()
    object Empty : FavoritesUiState()
}

data class FavoriteBookUiModel(
    val id: String,
    val title: String,
    val coverId: Long?
)

class FavoritesViewModel(
    private val repository: BookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<FavoritesUiState>(FavoritesUiState.Loading)
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            repository.getFavorites().collect { cachedBooks ->
                if (cachedBooks.isEmpty()) {
                    _uiState.value = FavoritesUiState.Empty
                } else {
                    val uiModels = cachedBooks.map { cachedBook ->
                        FavoriteBookUiModel(
                            id = cachedBook.id,
                            title = cachedBook.detail.title,
                            coverId = cachedBook.detail.covers?.firstOrNull()
                        )
                    }
                     _uiState.value = FavoritesUiState.Success(uiModels)
                }
            }
        }
    }
}
