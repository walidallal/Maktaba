package com.ElOuedUniv.maktaba.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ElOuedUniv.maktaba.domain.model.*
import com.ElOuedUniv.maktaba.domain.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// ── Form State ────────────────────────────────────────────────────

data class BookFormState(
    val title: String = "",
    val author: String = "",
    val isbn: String = "",
    val year: String = "",
    val genre: Genre = Genre.OTHER,
    val pages: String = "",
    val rating: Int = 5,
    val status: ReadingStatus = ReadingStatus.TO_READ,
    val notes: String = "",
    val isFavorite: Boolean = false,
    // validation
    val titleError: String? = null,
    val authorError: String? = null,
    // async state
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val savedId: Long? = null
) {
    fun toBook(existingId: Long = 0) = Book(
        id = existingId,
        title = title.trim(),
        author = author.trim(),
        isbn = isbn.trim(),
        year = year.toIntOrNull(),
        genre = genre,
        pages = pages.toIntOrNull() ?: 0,
        rating = rating,
        status = status,
        notes = notes.trim(),
        isFavorite = isFavorite
    )
}

// ── ViewModel ─────────────────────────────────────────────────────

@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val repository: BookRepository
) : ViewModel() {

    private val _formState = MutableStateFlow(BookFormState())
    val formState = _formState.asStateFlow()

    // Optional: Load existing book for edit mode
    fun loadForEdit(id: Long) {
        _formState.update { it.copy(isLoading = true) }
        repository.getBookById(id)
            .filterNotNull()
            .take(1)
            .onEach { book ->
                _formState.update {
                    BookFormState(
                        title = book.title,
                        author = book.author,
                        isbn = book.isbn,
                        year = book.year?.toString() ?: "",
                        genre = book.genre,
                        pages = if (book.pages > 0) book.pages.toString() else "",
                        rating = book.rating,
                        status = book.status,
                        notes = book.notes,
                        isFavorite = book.isFavorite,
                        isLoading = false
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    // ── Field updaters ────────────────────────────────────────────

    fun onTitleChange(v: String) =
        _formState.update { it.copy(title = v, titleError = null) }

    fun onAuthorChange(v: String) =
        _formState.update { it.copy(author = v, authorError = null) }

    fun onIsbnChange(v: String) =
        _formState.update { it.copy(isbn = v) }

    fun onYearChange(v: String) =
        _formState.update { it.copy(year = v) }

    fun onGenreChange(v: Genre) =
        _formState.update { it.copy(genre = v) }

    fun onPagesChange(v: String) =
        _formState.update { it.copy(pages = v) }

    fun onRatingChange(v: Int) =
        _formState.update { it.copy(rating = v) }

    fun onStatusChange(v: ReadingStatus) =
        _formState.update { it.copy(status = v) }

    fun onNotesChange(v: String) =
        _formState.update { it.copy(notes = v) }

    fun onFavoriteToggle() =
        _formState.update { it.copy(isFavorite = !it.isFavorite) }

    // ── Save ──────────────────────────────────────────────────────

    fun onSave(existingId: Long = 0) {
        if (!validate()) return
        viewModelScope.launch {
            _formState.update { it.copy(isSaving = true) }
            val id = repository.upsertBook(_formState.value.toBook(existingId))
            _formState.update { it.copy(isSaving = false, savedId = id) }
        }
    }

    private fun validate(): Boolean {
        var valid = true
        _formState.update {
            val titleErr = if (it.title.isBlank()) "Title is required" else null
            val authorErr = if (it.author.isBlank()) "Author is required" else null
            if (titleErr != null || authorErr != null) valid = false
            it.copy(titleError = titleErr, authorError = authorErr)
        }
        return valid
    }
}
