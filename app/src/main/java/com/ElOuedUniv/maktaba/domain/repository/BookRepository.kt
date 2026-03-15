package com.ElOuedUniv.maktaba.domain.repository

import com.ElOuedUniv.maktaba.domain.model.Book
import com.ElOuedUniv.maktaba.domain.model.BookFilter
import com.ElOuedUniv.maktaba.domain.model.Genre
import com.ElOuedUniv.maktaba.domain.model.ReadingStatus
import com.ElOuedUniv.maktaba.domain.model.SortOrder
import kotlinx.coroutines.flow.Flow

interface BookRepository {

    // ── Streams ──────────────────────────────────────────────────
    fun getAllBooks(
        query: String = "",
        filter: BookFilter = BookFilter.ALL,
        sortOrder: SortOrder = SortOrder.DATE_ADDED
    ): Flow<List<Book>>

    fun getFavoriteBooks(): Flow<List<Book>>

    fun getBookById(id: Long): Flow<Book?>

    // ── Stats ────────────────────────────────────────────────────
    fun getTotalCount(): Flow<Int>

    fun getCountByStatus(status: ReadingStatus): Flow<Int>

    fun getFavoriteCount(): Flow<Int>

    fun getGenreBreakdown(): Flow<Map<Genre, Int>>

    // ── Commands ─────────────────────────────────────────────────
    suspend fun upsertBook(book: Book): Long

    suspend fun deleteBook(id: Long)

    suspend fun toggleFavorite(id: Long)

    suspend fun updateStatus(id: Long, status: ReadingStatus)

    suspend fun updateProgress(id: Long, currentPage: Int)
}
