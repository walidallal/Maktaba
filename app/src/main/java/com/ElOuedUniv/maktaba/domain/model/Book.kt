package com.ElOuedUniv.maktaba.domain.model

data class Book(
    val id: Long = 0,
    val title: String,
    val author: String,
    val isbn: String = "",
    val year: Int? = null,
    val genre: Genre = Genre.OTHER,
    val pages: Int = 0,
    val rating: Int = 0,          // 1–5
    val status: ReadingStatus = ReadingStatus.TO_READ,
    val currentPage: Int = 0,
    val isFavorite: Boolean = false,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    val progress: Float
        get() = if (pages > 0) currentPage.toFloat() / pages else 0f

    val progressPercent: Int
        get() = (progress * 100).toInt()
}

enum class Genre(val displayName: String) {
    TECHNOLOGY("Technology"),
    SCIENCE_FICTION("Science Fiction"),
    HISTORY("History"),
    PHILOSOPHY("Philosophy"),
    BIOGRAPHY("Biography"),
    SCIENCE("Science"),
    LITERATURE("Literature"),
    RELIGION("Religion"),
    OTHER("Other")
}

enum class ReadingStatus(val displayName: String) {
    TO_READ("To Read"),
    READING("Reading"),
    COMPLETED("Completed"),
    DROPPED("Dropped")
}

enum class BookFilter {
    ALL, READING, COMPLETED, TO_READ, FAVORITES
}

enum class SortOrder {
    TITLE_ASC, TITLE_DESC, AUTHOR_ASC, DATE_ADDED, RATING_DESC
}
