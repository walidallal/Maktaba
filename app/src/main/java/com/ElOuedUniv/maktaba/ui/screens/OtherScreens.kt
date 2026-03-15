package com.ElOuedUniv.maktaba.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ElOuedUniv.maktaba.domain.model.*
import com.ElOuedUniv.maktaba.ui.components.*
import com.ElOuedUniv.maktaba.ui.theme.MaktabaColors
import com.ElOuedUniv.maktaba.viewmodel.*

// ════════════════════════════════════════
//  BookDetailScreen
// ════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    bookId: Long,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    viewModel: BookDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(bookId) { viewModel.loadBook(bookId) }
    LaunchedEffect(uiState.isDeleted) { if (uiState.isDeleted) onBack() }

    val bookEmojis = listOf("📗","📘","📙","📕","📓","📔")

    Box(modifier = Modifier.fillMaxSize().background(MaktabaColors.Bg)) {
        when {
            uiState.isLoading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator(color = MaktabaColors.Gold, strokeWidth = 2.dp)
            }
            uiState.book != null -> {
                val book = uiState.book!!
                val emoji = bookEmojis[book.id.toInt() % bookEmojis.size]

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    // Cover header
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .background(
                                    Brush.verticalGradient(
                                        listOf(MaktabaColors.Surface2, MaktabaColors.Bg)
                                    )
                                )
                        ) {
                            // Back button
                            Box(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaktabaColors.Surface3)
                                    .border(1.dp, MaktabaColors.Border, RoundedCornerShape(10.dp))
                                    .clickable(onClick = onBack)
                                    .align(Alignment.TopStart),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack, "Back",
                                    tint = MaktabaColors.TextPrimary, modifier = Modifier.size(18.dp)
                                )
                            }
                            // Favorite button
                            Box(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaktabaColors.Surface3)
                                    .border(1.dp, MaktabaColors.Border, RoundedCornerShape(10.dp))
                                    .clickable { viewModel.onToggleFavorite() }
                                    .align(Alignment.TopEnd),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (book.isFavorite) "★" else "☆",
                                    fontSize = 18.sp,
                                    color = if (book.isFavorite) MaktabaColors.Gold else MaktabaColors.TextTertiary
                                )
                            }
                            // Cover
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                BookCoverBox(emoji = emoji, size = 72.dp)
                            }
                        }
                    }

                    // Info
                    item {
                        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                            Text(
                                text = book.title,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = MaktabaColors.TextPrimary,
                                lineHeight = 30.sp
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "${book.author}${book.year?.let { " · $it" } ?: ""}",
                                fontSize = 13.sp,
                                color = MaktabaColors.TextSecondary
                            )
                            Spacer(Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                GenreChip(book.genre)
                                StatusBadge(book.status)
                                if (!book.isbn.isNullOrBlank()) {
                                    Text(
                                        text = book.isbn,
                                        fontSize = 9.sp,
                                        color = MaktabaColors.TextTertiary,
                                        fontFamily = FontFamily.Monospace,
                                        modifier = Modifier.align(Alignment.CenterVertically)
                                    )
                                }
                            }
                        }
                    }

                    // Stats row
                    item {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            DetailStatCard("Pages", "${book.pages}", MaktabaColors.Teal, Modifier.weight(1f))
                            DetailStatCard("Progress", "${book.progressPercent}%", MaktabaColors.Gold, Modifier.weight(1f))
                            DetailStatCard("Rating", "★ ${book.rating}", MaktabaColors.Gold, Modifier.weight(1f))
                        }
                    }

                    // Progress bar
                    if (book.pages > 0) {
                        item {
                            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Reading progress", fontSize = 11.sp, color = MaktabaColors.TextTertiary)
                                    Text(
                                        "p.${book.currentPage} / ${book.pages}",
                                        fontSize = 11.sp,
                                        color = MaktabaColors.Teal,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Spacer(Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { book.progress },
                                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                                    color = MaktabaColors.Teal,
                                    trackColor = MaktabaColors.Border2
                                )
                            }
                        }
                    }

                    // Status selector
                    item {
                        SectionLabel("Status", Modifier.padding(top = 12.dp))
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(ReadingStatus.entries) { s ->
                                MaktabaFilterChip(
                                    label = s.displayName,
                                    selected = book.status == s,
                                    onClick = { viewModel.onStatusChange(s) }
                                )
                            }
                        }
                    }

                    // Notes
                    if (book.notes.isNotBlank()) {
                        item {
                            SectionLabel("Notes", Modifier.padding(top = 12.dp))
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaktabaColors.Surface2)
                                    .border(1.dp, MaktabaColors.Border, RoundedCornerShape(12.dp))
                                    .padding(14.dp)
                            ) {
                                Text(book.notes, fontSize = 13.sp, color = MaktabaColors.TextSecondary, lineHeight = 20.sp)
                            }
                        }
                    }

                    // Actions
                    item {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Edit
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaktabaColors.Gold)
                                    .clickable { onEdit(book.id) }
                                    .padding(vertical = 14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("✏  Edit", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaktabaColors.Bg)
                            }
                            // Delete
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaktabaColors.Surface2)
                                    .border(1.dp, MaktabaColors.Border, RoundedCornerShape(12.dp))
                                    .clickable { viewModel.onDelete() }
                                    .padding(vertical = 14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Delete", fontWeight = FontWeight.Medium, fontSize = 13.sp, color = MaktabaColors.Red)
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailStatCard(label: String, value: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaktabaColors.Surface2)
            .border(1.dp, MaktabaColors.Border, RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = color, fontFamily = FontFamily.Serif)
        Text(label.uppercase(), fontSize = 8.sp, color = MaktabaColors.TextTertiary, letterSpacing = 0.6.sp)
    }
}

// ════════════════════════════════════════
//  AddEditBookScreen
// ════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditBookScreen(
    bookId: Long? = null,
    onSaved: () -> Unit,
    onCancel: () -> Unit,
    viewModel: AddEditViewModel = hiltViewModel()
) {
    val formState by viewModel.formState.collectAsState()
    val isEditMode = bookId != null
    LaunchedEffect(bookId) { bookId?.let { viewModel.loadForEdit(it) } }
    LaunchedEffect(formState.savedId) { if (formState.savedId != null) onSaved() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaktabaColors.Bg)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaktabaColors.Surface2)
                    .border(1.dp, MaktabaColors.Border, RoundedCornerShape(10.dp))
                    .clickable(onClick = onCancel),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = MaktabaColors.TextPrimary, modifier = Modifier.size(18.dp))
            }
            Text(
                text = if (isEditMode) "Edit Book" else "Add New Book",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = MaktabaColors.TextPrimary
            )
        }

        // Form
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            MaktabaTextField(
                value = formState.title,
                onValueChange = viewModel::onTitleChange,
                label = "Title *",
                placeholder = "Book title…",
                isError = formState.titleError != null,
                errorMessage = formState.titleError
            )
            MaktabaTextField(
                value = formState.author,
                onValueChange = viewModel::onAuthorChange,
                label = "Author *",
                placeholder = "Author name…",
                isError = formState.authorError != null,
                errorMessage = formState.authorError
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MaktabaTextField(
                    value = formState.isbn,
                    onValueChange = viewModel::onIsbnChange,
                    label = "ISBN",
                    placeholder = "978…",
                    modifier = Modifier.weight(1f)
                )
                MaktabaTextField(
                    value = formState.year,
                    onValueChange = viewModel::onYearChange,
                    label = "Year",
                    placeholder = "2024",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            // Genre dropdown
            var genreExpanded by remember { mutableStateOf(false) }
            Column {
                Text("GENRE", fontSize = 9.sp, fontWeight = FontWeight.SemiBold,
                    color = MaktabaColors.TextTertiary, letterSpacing = 0.8.sp,
                    modifier = Modifier.padding(bottom = 4.dp))
                ExposedDropdownMenuBox(expanded = genreExpanded, onExpandedChange = { genreExpanded = it }) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaktabaColors.Surface2)
                            .border(1.dp, MaktabaColors.Border, RoundedCornerShape(12.dp))
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(formState.genre.displayName, fontSize = 13.sp, color = MaktabaColors.TextSecondary)
                        Text("▾", fontSize = 12.sp, color = MaktabaColors.TextTertiary)
                    }
                    ExposedDropdownMenu(
                        expanded = genreExpanded,
                        onDismissRequest = { genreExpanded = false },
                        containerColor = MaktabaColors.Surface3
                    ) {
                        Genre.entries.forEach { g ->
                            DropdownMenuItem(
                                text = { Text(g.displayName, color = MaktabaColors.TextPrimary, fontSize = 13.sp) },
                                onClick = { viewModel.onGenreChange(g); genreExpanded = false }
                            )
                        }
                    }
                }
            }

            // Rating
            Column {
                Text("RATING", fontSize = 9.sp, fontWeight = FontWeight.SemiBold,
                    color = MaktabaColors.TextTertiary, letterSpacing = 0.8.sp,
                    modifier = Modifier.padding(bottom = 6.dp))
                RatingSelectorRow(rating = formState.rating, onRatingChange = viewModel::onRatingChange)
            }

            // Status chips
            Column {
                Text("STATUS", fontSize = 9.sp, fontWeight = FontWeight.SemiBold,
                    color = MaktabaColors.TextTertiary, letterSpacing = 0.8.sp,
                    modifier = Modifier.padding(bottom = 6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ReadingStatus.entries.forEach { s ->
                        MaktabaFilterChip(
                            label = s.displayName,
                            selected = formState.status == s,
                            onClick = { viewModel.onStatusChange(s) }
                        )
                    }
                }
            }

            MaktabaTextField(
                value = formState.notes,
                onValueChange = viewModel::onNotesChange,
                label = "Notes",
                placeholder = "Personal notes…",
                singleLine = false,
                maxLines = 4,
                minLines = 3
            )

            // Favorite toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaktabaColors.Surface2)
                    .border(1.dp, MaktabaColors.Border, RoundedCornerShape(12.dp))
                    .clickable { viewModel.onFavoriteToggle() }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Add to Favorites", fontSize = 13.sp, color = MaktabaColors.TextSecondary)
                Text(
                    text = if (formState.isFavorite) "★" else "☆",
                    fontSize = 20.sp,
                    color = if (formState.isFavorite) MaktabaColors.Gold else MaktabaColors.Border2
                )
            }

            // Save button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (formState.isSaving) MaktabaColors.GoldDim else MaktabaColors.Gold)
                    .clickable(enabled = !formState.isSaving) { viewModel.onSave(bookId ?: 0L) }
                    .padding(vertical = 15.dp),
                contentAlignment = Alignment.Center
            ) {
                if (formState.isSaving) {
                    CircularProgressIndicator(color = MaktabaColors.Bg, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text(
                        text = if (isEditMode) "Save Changes" else "Save Book",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaktabaColors.Bg,
                        letterSpacing = 0.5.sp
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

// ════════════════════════════════════════
//  FavoritesScreen
// ════════════════════════════════════════

@Composable
fun FavoritesScreen(
    onBookClick: (Long) -> Unit,
    onBack: () -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val favorites by viewModel.favorites.collectAsState()
    val bookEmojis = listOf("📗","📘","📙","📕","📓","📔")

    Column(modifier = Modifier.fillMaxSize().background(MaktabaColors.Bg)) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("المفضلة", fontSize = 11.sp, color = MaktabaColors.TextTertiary, letterSpacing = 1.sp)
                Text("Favorites", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold,
                    fontSize = 24.sp, color = MaktabaColors.Gold)
            }
            Text(
                text = "${favorites.size} books",
                fontSize = 11.sp,
                color = MaktabaColors.TextTertiary,
                fontFamily = FontFamily.Monospace
            )
        }

        if (favorites.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("★", fontSize = 48.sp, color = MaktabaColors.Border2)
                    Spacer(Modifier.height(12.dp))
                    Text("No favorites yet", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = MaktabaColors.TextSecondary)
                    Text("Star books to add them here", fontSize = 12.sp, color = MaktabaColors.TextTertiary)
                }
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(favorites, key = { it.id }) { book ->
                    val emoji = bookEmojis[book.id.toInt() % bookEmojis.size]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaktabaColors.Surface2)
                            .border(1.dp, MaktabaColors.GoldDim, RoundedCornerShape(16.dp))
                            .clickable { onBookClick(book.id) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Gold left accent line
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(56.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(MaktabaColors.Gold)
                        )
                        BookCoverBox(emoji = emoji, size = 42.dp)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(book.title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp,
                                maxLines = 1, overflow = TextOverflow.Ellipsis, color = MaktabaColors.TextPrimary)
                            Text(book.author, fontSize = 11.sp, color = MaktabaColors.TextSecondary)
                            Row(Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                RatingStars(book.rating, size = 10.dp)
                                StatusBadge(book.status)
                            }
                        }
                        IconButton(onClick = { viewModel.onRemoveFavorite(book.id) }) {
                            Text("★", fontSize = 20.sp, color = MaktabaColors.Gold)
                        }
                    }
                }
            }
        }
    }
}

// ════════════════════════════════════════
//  StatisticsScreen
// ════════════════════════════════════════

@Composable
fun StatisticsScreen(
    onBack: () -> Unit,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val stats by viewModel.stats.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaktabaColors.Bg),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 4.dp)) {
                Text("إحصائيات", fontSize = 11.sp, color = MaktabaColors.TextTertiary, letterSpacing = 1.sp)
                Text("Statistics", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold,
                    fontSize = 28.sp, color = MaktabaColors.TextPrimary)
            }
        }

        // Stat cards 2x2
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    BigStatCard("Total Books",   "${stats.totalBooks}",    MaktabaColors.Teal,  Modifier.weight(1f))
                    BigStatCard("Favorites",     "${stats.favoriteCount}", MaktabaColors.Gold,  Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    BigStatCard("Reading Now",   "${stats.readingCount}",  MaktabaColors.Blue,  Modifier.weight(1f))
                    BigStatCard("Completed",     "${stats.completedCount}",MaktabaColors.Green, Modifier.weight(1f))
                }
            }
        }

        // Genre breakdown
        item {
            SectionLabel("Books by Genre", Modifier.padding(top = 8.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val colors = listOf(MaktabaColors.Teal, MaktabaColors.Blue, MaktabaColors.Gold, MaktabaColors.Purple)
                stats.genreBreakdown.entries
                    .sortedByDescending { it.value }
                    .forEachIndexed { i, (genre, count) ->
                        val pct = if (stats.totalBooks > 0) count.toFloat() / stats.totalBooks else 0f
                        val barColor = colors[i % colors.size]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaktabaColors.Surface1)
                                .border(1.dp, MaktabaColors.Border, RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(genre.displayName, fontSize = 12.sp, color = MaktabaColors.TextSecondary, modifier = Modifier.width(80.dp))
                            LinearProgressIndicator(
                                progress = { pct },
                                modifier = Modifier.weight(1f).height(4.dp).clip(RoundedCornerShape(2.dp)),
                                color = barColor,
                                trackColor = MaktabaColors.Border2
                            )
                            Text("$count", fontSize = 11.sp, color = MaktabaColors.TextTertiary,
                                fontFamily = FontFamily.Monospace, modifier = Modifier.width(20.dp))
                        }
                    }
            }
        }

        // Overall progress
        item {
            SectionLabel("Reading Progress", Modifier.padding(top = 12.dp))
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaktabaColors.Surface2)
                    .border(1.dp, MaktabaColors.Border, RoundedCornerShape(14.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Overall completion", fontSize = 13.sp, color = MaktabaColors.TextSecondary)
                    Text("${stats.completionPercent}%", fontSize = 13.sp, color = MaktabaColors.Teal, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.SemiBold)
                }
                LinearProgressIndicator(
                    progress = { stats.completionPercent / 100f },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = MaktabaColors.Teal,
                    trackColor = MaktabaColors.Border2
                )
                Text(
                    "${stats.completedCount} completed · ${stats.readingCount} reading · ${stats.toReadCount} to read",
                    fontSize = 10.sp,
                    color = MaktabaColors.TextTertiary,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
private fun BigStatCard(label: String, value: String, accentColor: androidx.compose.ui.graphics.Color, modifier: Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaktabaColors.Surface2)
            .border(1.dp, MaktabaColors.Border, RoundedCornerShape(16.dp))
    ) {
        // Left accent bar
        Box(
            modifier = Modifier
                .width(3.dp)
                .fillMaxHeight()
                .background(accentColor)
                .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
        )
        Column(modifier = Modifier.padding(start = 16.dp, top = 14.dp, end = 12.dp, bottom = 14.dp)) {
            Text(value, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold,
                fontSize = 32.sp, color = accentColor, lineHeight = 36.sp)
            Text(label.uppercase(), fontSize = 8.sp, color = MaktabaColors.TextTertiary, letterSpacing = 0.8.sp)
        }
    }
}
