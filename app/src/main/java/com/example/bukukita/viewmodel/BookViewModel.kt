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
import java.util.Locale

sealed class BookUiState {
    object Loading : BookUiState()
    data class Success(val books: List<Book>) : BookUiState()
    data class Error(val message: String) : BookUiState()
}

@OptIn(FlowPreview::class)
class BookViewModel : ViewModel() {
    private val repository = BookRepository()
    internal companion object {
        const val ALL_CATEGORY = ""

        fun normalizeCategorySelection(category: String): String {
            val trimmed = category.trim()
            return if (
                trimmed.isEmpty() ||
                trimmed.equals("Semua", ignoreCase = true) ||
                trimmed.equals("All", ignoreCase = true)
            ) {
                ALL_CATEGORY
            } else {
                trimmed
            }
        }
    }

    private val _uiState = MutableStateFlow<BookUiState>(BookUiState.Loading)
    val uiState: StateFlow<BookUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(ALL_CATEGORY)
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(emptyList())
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
    }

    fun resetFilters() {
        _searchQuery.value = ""
        _selectedCategory.value = ALL_CATEGORY
    }

    fun retry() {
        if (_categories.value.isEmpty()) {
            fetchCategories()
        }
        executeFetch(_searchQuery.value, _selectedCategory.value)
    }

    fun onCategorySelected(category: String) {
        _selectedCategory.value = normalizeCategorySelection(category)
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            try {
                val books = repository.getAllBooks()
                val uniqueCategories = books.mapNotNull { it.category }
                    .filter { it.isNotBlank() }
                    .distinct()
                    .sorted()
                
                _categories.value = uniqueCategories
            } catch (e: Exception) {
                // Keep empty list if offline; CategoryFilterRow still renders localized All chip
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

                _uiState.value = BookUiState.Success(books)
            } catch (e: Exception) {
                val fallback = if (Locale.getDefault().language == "en") "Unknown error" else "Kesalahan tidak diketahui"
                _uiState.value = BookUiState.Error(e.localizedMessage ?: fallback)
            }
        }
    }
}
