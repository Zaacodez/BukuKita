package com.example.bukukita.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bukukita.data.model.Book
import com.example.bukukita.data.repository.BookRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

sealed class BookUiState {
    object Loading : BookUiState()
    data class Success(val books: List<Book>) : BookUiState()
    data class Error(val message: String) : BookUiState()
}

@OptIn(FlowPreview::class)
class BookViewModel : ViewModel() {
    private val repository = BookRepository()
    private companion object {
        const val ALL_CATEGORY = "Semua"
    }

    private val _uiState = MutableStateFlow<BookUiState>(BookUiState.Loading)
    val uiState: StateFlow<BookUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(ALL_CATEGORY)
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _categories = MutableStateFlow(listOf(ALL_CATEGORY))
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    init {
        fetchCategories()
        
        viewModelScope.launch {
            combine(
                _searchQuery.debounce(500),
                _selectedCategory
            ) { query, category ->
                Pair(query, category)
            }.collectLatest { (query, category) ->
                executeFetch(query, category)
            }
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _uiState.value = BookUiState.Success(emptyList())
    }

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            try {
                val books = repository.getAllBooks()
                val uniqueCategories = books.mapNotNull { it.category }
                    .filter { it.isNotBlank() }
                    .distinct()
                    .sorted()
                
                _categories.value = listOf(ALL_CATEGORY) + uniqueCategories
            } catch (e: Exception) {
                // If fails, categories will just be ["All"] naturally
            }
        }
    }

    private fun executeFetch(query: String, category: String) {
        viewModelScope.launch {
            _uiState.value = BookUiState.Loading
            try {
                val books = if (query.isBlank() && category == ALL_CATEGORY) {
                    repository.getAllBooks()
                } else if (query.isBlank() && category != ALL_CATEGORY) {
                    repository.getBooksByCategory(category)
                } else if (query.isNotBlank() && category == ALL_CATEGORY) {
                    repository.searchBooks(query)
                } else {
                    repository.searchAndFilterBooks(query, category)
                }

                if (books.isEmpty()) {
                    _uiState.value = BookUiState.Error("Buku tidak ditemukan.")
                } else {
                    _uiState.value = BookUiState.Success(books)
                }
            } catch (e: Exception) {
                _uiState.value = BookUiState.Error(e.localizedMessage ?: "Kesalahan tidak diketahui")
            }
        }
    }
}
