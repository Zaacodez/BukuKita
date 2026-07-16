package com.example.bukukita.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bukukita.data.model.Book
import com.example.bukukita.data.repository.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class BookDetailUiState {
    object Loading : BookDetailUiState()
    data class Success(val book: Book) : BookDetailUiState()
    data class Error(val message: String) : BookDetailUiState()
}

class BookDetailViewModel : ViewModel() {
    private val repository = BookRepository()

    private val _uiState = MutableStateFlow<BookDetailUiState>(BookDetailUiState.Loading)
    val uiState: StateFlow<BookDetailUiState> = _uiState.asStateFlow()

    fun fetchBookDetail(id: String) {
        viewModelScope.launch {
            _uiState.value = BookDetailUiState.Loading
            try {
                val book = repository.getBookById(id)
                if (book != null) {
                    _uiState.value = BookDetailUiState.Success(book)
                } else {
                    _uiState.value = BookDetailUiState.Error("Book not found")
                }
            } catch (e: Exception) {
                _uiState.value = BookDetailUiState.Error(e.localizedMessage ?: "Unknown Error")
            }
        }
    }
}
