package com.ElOuedUniv.maktaba.data.repository

import com.ElOuedUniv.maktaba.data.local.dao.BookDao
import com.ElOuedUniv.maktaba.data.local.entity.toDomain
import com.ElOuedUniv.maktaba.data.local.entity.toEntity
import com.ElOuedUniv.maktaba.domain.model.*
import com.ElOuedUniv.maktaba.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookRepositoryImpl @Inject constructor(
    private val dao: BookDao
) : BookRepository {

    // ── Streams ───────────────────────────────────────────────────

    override fun getAllBooks(
        query: String,
        filter: BookFilter,
        sortOrder: SortOrder
    ): Flow<List<Book>> {
        val flow = when (filter) {
            BookFilter.ALL       -> dao.getAllBooks(query)
            BookFilter.READING   -> dao.getBooksByStatus(query, ReadingStatus.READING.name)
            BookFilter.COMPLETED -> dao.getBooksByStatus(query, ReadingStatus.COMPLETED.name)
            BookFilter.TO_READ   -> dao.getBooksByStatus(query, ReadingStatus.TO_READ.name)
            BookFilter.FAVORITES -> dao.getFavoriteBooks(query)
        }
        return flow.map { entities ->
            val books = entities.map { it.toDomain() }
            when (sortOrder) {
                SortOrder.TITLE_ASC   -> books.sortedBy { it.title }
                SortOrder.TITLE_DESC  -> books.sortedByDescending { it.title }
                SortOrder.AUTHOR_ASC  -> books.sortedBy { it.author }
                SortOrder.DATE_ADDED  -> books.sortedByDescending { it.createdAt }
                SortOrder.RATING_DESC -> books.sortedByDescending { it.rating }
            }
        }
    }

    override fun getFavoriteBooks(): Flow<List<Book>> =
        dao.getFavoriteBooks().map { it.map { e -> e.toDomain() } }

    override fun getBookById(id: Long): Flow<Book?> =
        dao.getBookById(id).map { it?.toDomain() }

    // ── Stats ─────────────────────────────────────────────────────

    override fun getTotalCount(): Flow<Int> = dao.getTotalCount()

    override fun getCountByStatus(status: ReadingStatus): Flow<Int> =
        dao.getCountByStatus(status.name)

    override fun getFavoriteCount(): Flow<Int> = dao.getFavoriteCount()

    override fun getGenreBreakdown(): Flow<Map<Genre, Int>> =
        dao.getGenreStats().map { rows ->
            rows.associate { row ->
                val genre = runCatching { Genre.valueOf(row.genre) }.getOrDefault(Genre.OTHER)
                genre to row.count
            }
        }

    // ── Commands ──────────────────────────────────────────────────

    override suspend fun upsertBook(book: Book): Long =
        dao.upsert(book.toEntity())

    override suspend fun deleteBook(id: Long) =
        dao.deleteById(id)

    override suspend fun toggleFavorite(id: Long) =
        dao.toggleFavorite(id)

    override suspend fun updateStatus(id: Long, status: ReadingStatus) =
        dao.updateStatus(id, status.name)

    override suspend fun updateProgress(id: Long, currentPage: Int) =
        dao.updateProgress(id, currentPage)
}
