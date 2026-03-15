package com.ElOuedUniv.maktaba.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ElOuedUniv.maktaba.domain.model.Book
import com.ElOuedUniv.maktaba.domain.model.ReadingStatus
import com.ElOuedUniv.maktaba.domain.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// ── UI State ──────────────────────────────────────────────────────

data class BookDetailUiState(
    val book: Book? = null,
    val isLoading: Boolean = true,
    val isDeleted: Boolean = false,
    val error: String? = null
)

// ── ViewModel ─────────────────────────────────────────────────────

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val repository: BookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookDetailUiState())
    val uiState = _uiState.asStateFlow()

    fun loadBook(id: Long) {
        repository.getBookById(id)
            .onEach { book ->
                _uiState.update {
                    it.copy(book = book, isLoading = false)
                }
            }
            .catch { e ->
                _uiState.update {
                    it.copy(error = e.message, isLoading = false)
                }
            }
            .launchIn(viewModelScope)
    }

    fun onToggleFavorite() {
        viewModelScope.launch {
            _uiState.value.book?.let { repository.toggleFavorite(it.id) }
        }
    }

    fun onStatusChange(status: ReadingStatus) {
        viewModelScope.launch {
            _uiState.value.book?.let { repository.updateStatus(it.id, status) }
        }
    }

    fun onProgressUpdate(currentPage: Int) {
        viewModelScope.launch {
            _uiState.value.book?.let { repository.updateProgress(it.id, currentPage) }
        }
    }

    fun onDelete() {
        viewModelScope.launch {
            _uiState.value.book?.let {
                repository.deleteBook(it.id)
                _uiState.update { s -> s.copy(isDeleted = true) }
            }
        }
    }
}
