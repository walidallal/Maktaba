package com.ElOuedUniv.maktaba.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ElOuedUniv.maktaba.domain.model.Book
import com.ElOuedUniv.maktaba.domain.model.BookFilter
import com.ElOuedUniv.maktaba.ui.components.*
import com.ElOuedUniv.maktaba.ui.theme.MaktabaColors
import com.ElOuedUniv.maktaba.viewmodel.BookListUiState
import com.ElOuedUniv.maktaba.viewmodel.BookListViewModel

@Composable
fun BookListScreen(
    onBookClick: (Long) -> Unit,
    onAddClick: () -> Unit,
    onFavsClick: () -> Unit,
    onStatsClick: () -> Unit,
    viewModel: BookListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val activeFilter by viewModel.filter.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaktabaColors.Bg),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // ── Header ──────────────────────────────────────────────
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "المكتبة",
                        fontSize = 11.sp,
                        color = MaktabaColors.TextTertiary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Maktaba",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = MaktabaColors.Gold
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconBox(icon = "🔍")
                    IconBox(icon = "⚙")
                }
            }
        }

        // ── Search Bar ──────────────────────────────────────────
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaktabaColors.Surface2)
                    .border(1.dp, MaktabaColors.Border, RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("🔍", fontSize = 14.sp, color = MaktabaColors.TextTertiary)
                Text(
                    text = if (searchQuery.isEmpty()) "Search titles, authors…" else searchQuery,
                    fontSize = 13.sp,
                    color = if (searchQuery.isEmpty()) MaktabaColors.TextTertiary
                            else MaktabaColors.TextPrimary
                )
            }
            Spacer(Modifier.height(12.dp))
        }

        // ── Filter Chips ─────────────────────────────────────────
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf(
                    BookFilter.ALL       to "All",
                    BookFilter.READING   to "Reading",
                    BookFilter.COMPLETED to "Completed",
                    BookFilter.TO_READ   to "To Read",
                    BookFilter.FAVORITES to "Favorites"
                )
                items(filters) { (filter, label) ->
                    MaktabaFilterChip(
                        label = label,
                        selected = activeFilter == filter,
                        onClick = { viewModel.onFilterChange(filter) }
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        // ── Content ───────────────────────────────────────────────
        when (val state = uiState) {
            is BookListUiState.Loading -> item {
                Box(
                    Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaktabaColors.Gold, strokeWidth = 2.dp)
                }
            }

            is BookListUiState.Error -> item {
                Box(
                    Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.message, color = MaktabaColors.Red, fontSize = 13.sp)
                }
            }

            is BookListUiState.Success -> {
                if (state.books.isEmpty()) {
                    item {
                        Box(
                            Modifier.fillMaxWidth().height(240.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("📚", fontSize = 48.sp)
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    "No books yet",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaktabaColors.TextSecondary
                                )
                                Text(
                                    "Tap + to add your first book",
                                    fontSize = 12.sp,
                                    color = MaktabaColors.TextTertiary
                                )
                            }
                        }
                    }
                } else {
                    // Featured: first currently-reading book
                    val featured = state.books.firstOrNull {
                        it.status == com.ElOuedUniv.maktaba.domain.model.ReadingStatus.READING
                    }
                    if (featured != null) {
                        item {
                            SectionLabel("Currently Reading")
                            FeaturedBookCard(
                                book = featured,
                                onClick = { onBookClick(featured.id) },
                                onFavoriteToggle = { viewModel.onToggleFavorite(featured.id) }
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                    }

                    item { SectionLabel("All Books") }

                    items(state.books, key = { it.id }) { book ->
                        BookListItem(
                            book = book,
                            onClick = { onBookClick(book.id) },
                            onFavoriteToggle = { viewModel.onToggleFavorite(book.id) }
                        )
                    }
                }
            }
        }
    }
}

// ── Featured Card ─────────────────────────────────────────────────

@Composable
private fun FeaturedBookCard(
    book: Book,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit
) {
    val bookEmojis = listOf("📗","📘","📙","📕","📓","📔")
    val emoji = bookEmojis[book.id.toInt() % bookEmojis.size]

    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaktabaColors.Surface2)
            .border(1.dp, MaktabaColors.Border, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
    ) {
        // Accent top line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(
                    brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                        listOf(MaktabaColors.Gold, MaktabaColors.Teal)
                    )
                )
        )

        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            BookCoverBox(emoji = emoji, size = 56.dp)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = book.title,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp,
                    color = MaktabaColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = book.author,
                    fontSize = 11.sp,
                    color = MaktabaColors.TextSecondary,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    GenreChip(genre = book.genre)
                    StatusBadge(status = book.status)
                }
                if (book.pages > 0) {
                    Spacer(Modifier.height(10.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LinearProgressIndicator(
                            progress = { book.progress },
                            modifier = Modifier.weight(1f).height(3.dp).clip(RoundedCornerShape(2.dp)),
                            color = MaktabaColors.Teal,
                            trackColor = MaktabaColors.Border2
                        )
                        Text(
                            text = "${book.progressPercent}%",
                            fontSize = 10.sp,
                            color = MaktabaColors.Teal,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
            // Favorite button
            Text(
                text = if (book.isFavorite) "★" else "☆",
                fontSize = 18.sp,
                color = if (book.isFavorite) MaktabaColors.Gold else MaktabaColors.Border2,
                modifier = Modifier.clickable(onClick = onFavoriteToggle)
            )
        }
    }
}

// ── List Item ─────────────────────────────────────────────────────

@Composable
private fun BookListItem(
    book: Book,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit
) {
    val bookEmojis = listOf("📗","📘","📙","📕","📓","📔")
    val emoji = bookEmojis[book.id.toInt() % bookEmojis.size]

    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaktabaColors.Surface1)
            .border(1.dp, MaktabaColors.Border, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        BookCoverBox(emoji = emoji, size = 40.dp)

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = book.title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaktabaColors.TextPrimary
            )
            Text(
                text = book.author,
                fontSize = 11.sp,
                color = MaktabaColors.TextSecondary,
                modifier = Modifier.padding(vertical = 3.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                RatingStars(rating = book.rating, size = 10.dp)
                StatusBadge(status = book.status)
            }
        }

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (book.pages > 0) {
                ProgressRing(
                    progress = book.progress,
                    size = 30.dp,
                    color = if (book.progress >= 1f) MaktabaColors.Green else MaktabaColors.Teal
                )
            }
            Text(
                text = if (book.isFavorite) "★" else "☆",
                fontSize = 16.sp,
                color = if (book.isFavorite) MaktabaColors.Gold else MaktabaColors.Border2,
                modifier = Modifier.clickable(onClick = onFavoriteToggle)
            )
        }
    }
}

// ── Icon Box ──────────────────────────────────────────────────────

@Composable
private fun IconBox(icon: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaktabaColors.Surface2)
            .border(1.dp, MaktabaColors.Border, RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(icon, fontSize = 16.sp)
    }
}
