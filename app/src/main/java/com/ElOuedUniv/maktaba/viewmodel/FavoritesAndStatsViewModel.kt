package com.ElOuedUniv.maktaba.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ElOuedUniv.maktaba.domain.model.Book
import com.ElOuedUniv.maktaba.domain.model.Genre
import com.ElOuedUniv.maktaba.domain.model.ReadingStatus
import com.ElOuedUniv.maktaba.domain.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// ════════════════════════════════════════
//  FavoritesViewModel
// ════════════════════════════════════════

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: BookRepository
) : ViewModel() {

    val favorites: StateFlow<List<Book>> =
        repository.getFavoriteBooks()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    fun onRemoveFavorite(bookId: Long) {
        viewModelScope.launch {
            repository.toggleFavorite(bookId)
        }
    }
}

// ════════════════════════════════════════
//  StatisticsViewModel
// ════════════════════════════════════════

data class LibraryStats(
    val totalBooks: Int = 0,
    val favoriteCount: Int = 0,
    val readingCount: Int = 0,
    val completedCount: Int = 0,
    val toReadCount: Int = 0,
    val genreBreakdown: Map<Genre, Int> = emptyMap()
) {
    val completionPercent: Int
        get() = if (totalBooks > 0) (completedCount * 100) / totalBooks else 0
}

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val repository: BookRepository
) : ViewModel() {

    val stats: StateFlow<LibraryStats> =
        combine(
            repository.getTotalCount(),
            repository.getFavoriteCount(),
            repository.getCountByStatus(ReadingStatus.READING),
            repository.getCountByStatus(ReadingStatus.COMPLETED),
            repository.getCountByStatus(ReadingStatus.TO_READ),
            repository.getGenreBreakdown()
        ) { values ->
            @Suppress("UNCHECKED_CAST")
            val genres = values[5] as Map<Genre, Int>
            LibraryStats(
                totalBooks     = values[0] as Int,
                favoriteCount  = values[1] as Int,
                readingCount   = values[2] as Int,
                completedCount = values[3] as Int,
                toReadCount    = values[4] as Int,
                genreBreakdown = genres
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = LibraryStats()
            )
}