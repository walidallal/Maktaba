package com.ElOuedUniv.maktaba.data.local.dao

import androidx.room.*
import com.ElOuedUniv.maktaba.data.local.entity.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    // ── Reads ─────────────────────────────────────────────────────

    @Query("""
        SELECT * FROM books
        WHERE (:query = '' OR title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%')
        ORDER BY createdAt DESC
    """)
    fun getAllBooks(query: String = ""): Flow<List<BookEntity>>

    @Query("""
        SELECT * FROM books
        WHERE (:query = '' OR title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%')
          AND status = :status
        ORDER BY createdAt DESC
    """)
    fun getBooksByStatus(query: String, status: String): Flow<List<BookEntity>>

    @Query("""
        SELECT * FROM books
        WHERE (:query = '' OR title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%')
          AND isFavorite = 1
        ORDER BY createdAt DESC
    """)
    fun getFavoriteBooks(query: String = ""): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE id = :id")
    fun getBookById(id: Long): Flow<BookEntity?>

    // ── Stats ─────────────────────────────────────────────────────

    @Query("SELECT COUNT(*) FROM books")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM books WHERE status = :status")
    fun getCountByStatus(status: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM books WHERE isFavorite = 1")
    fun getFavoriteCount(): Flow<Int>

    @Query("SELECT genre, COUNT(*) AS count FROM books GROUP BY genre")
    fun getGenreStats(): Flow<List<GenreCount>>

    // ── Writes ────────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(book: BookEntity): Long

    @Query("DELETE FROM books WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE books SET isFavorite = NOT isFavorite WHERE id = :id")
    suspend fun toggleFavorite(id: Long)

    @Query("UPDATE books SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)

    @Query("UPDATE books SET currentPage = :currentPage WHERE id = :id")
    suspend fun updateProgress(id: Long, currentPage: Int)
}

// ── Helper data class ─────────────────────────────────────────────

data class GenreCount(
    val genre: String,
    val count: Int
)
