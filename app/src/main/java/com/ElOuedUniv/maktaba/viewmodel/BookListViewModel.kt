package com.ElOuedUniv.maktaba.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ElOuedUniv.maktaba.domain.model.*
import com.ElOuedUniv.maktaba.domain.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// ── UI State ──────────────────────────────────────────────────────

sealed interface BookListUiState {
    data object Loading : BookListUiState
    data class Success(val books: List<Book>) : BookListUiState
    data class Error(val message: String) : BookListUiState
}

// ── ViewModel ─────────────────────────────────────────────────────

@HiltViewModel
class BookListViewModel @Inject constructor(
    private val repository: BookRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _filter = MutableStateFlow(BookFilter.ALL)
    val filter = _filter.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.DATE_ADDED)
    val sortOrder = _sortOrder.asStateFlow()

    private val _uiState = MutableStateFlow<BookListUiState>(BookListUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        observeBooks()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeBooks() {
        combine(_searchQuery, _filter, _sortOrder) { q, f, s -> Triple(q, f, s) }
            .flatMapLatest { (q, f, s) ->
                repository.getAllBooks(query = q, filter = f, sortOrder = s)
            }
            .onEach { books ->
                _uiState.update { BookListUiState.Success(books) }
            }
            .catch { e ->
                _uiState.update { BookListUiState.Error(e.message ?: "Unknown error") }
            }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.update { query }
    }

    fun onFilterChange(filter: BookFilter) {
        _filter.update { filter }
    }

    fun onSortOrderChange(order: SortOrder) {
        _sortOrder.update { order }
    }

    fun onToggleFavorite(bookId: Long) {
        viewModelScope.launch {
            repository.toggleFavorite(bookId)
        }
    }

    fun onDeleteBook(bookId: Long) {
        viewModelScope.launch {
            repository.deleteBook(bookId)
        }
    }
}
