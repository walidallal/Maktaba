package com.ElOuedUniv.maktaba.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ElOuedUniv.maktaba.domain.model.Book
import com.ElOuedUniv.maktaba.domain.model.Genre
import com.ElOuedUniv.maktaba.domain.model.ReadingStatus

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val author: String,
    val isbn: String = "",
    val year: Int? = null,
    val genre: String = Genre.OTHER.name,
    val pages: Int = 0,
    val rating: Int = 0,
    val status: String = ReadingStatus.TO_READ.name,
    val currentPage: Int = 0,
    val isFavorite: Boolean = false,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

// ── Mappers ───────────────────────────────────────────────────────

fun BookEntity.toDomain() = Book(
    id          = id,
    title       = title,
    author      = author,
    isbn        = isbn,
    year        = year,
    genre       = runCatching { Genre.valueOf(genre) }.getOrDefault(Genre.OTHER),
    pages       = pages,
    rating      = rating,
    status      = runCatching { ReadingStatus.valueOf(status) }.getOrDefault(ReadingStatus.TO_READ),
    currentPage = currentPage,
    isFavorite  = isFavorite,
    notes       = notes,
    createdAt   = createdAt
)

fun Book.toEntity() = BookEntity(
    id          = id,
    title       = title,
    author      = author,
    isbn        = isbn,
    year        = year,
    genre       = genre.name,
    pages       = pages,
    rating      = rating,
    status      = status.name,
    currentPage = currentPage,
    isFavorite  = isFavorite,
    notes       = notes,
    createdAt   = createdAt
)
