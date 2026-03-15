package com.ElOuedUniv.maktaba.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ElOuedUniv.maktaba.domain.model.Book
import com.ElOuedUniv.maktaba.domain.model.Genre
import com.ElOuedUniv.maktaba.domain.model.ReadingStatus
import com.ElOuedUniv.maktaba.ui.theme.MaktabaColors
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// ── Book Cover Emoji ──────────────────────────────────────────────

@Composable
fun BookCoverBox(
    emoji: String = "📗",
    size: Dp = 52.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(width = size, height = (size * 1.4f))
            .clip(RoundedCornerShape(10.dp))
            .background(MaktabaColors.Surface3)
            .border(1.dp, MaktabaColors.Border2, RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(emoji, fontSize = (size.value * 0.5f).sp)
    }
}

// ── Progress Ring ─────────────────────────────────────────────────

@Composable
fun ProgressRing(
    progress: Float,           // 0f..1f
    size: Dp = 32.dp,
    strokeWidth: Dp = 2.5.dp,
    color: Color = MaktabaColors.Teal,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "progress"
    )
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val s = strokeWidth.toPx()
            val r = (this.size.minDimension - s) / 2f
            val cx = this.size.width / 2f
            val cy = this.size.height / 2f
            // Track
            drawArc(
                color = MaktabaColors.Border2,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = s, cap = StrokeCap.Round),
                topLeft = Offset(cx - r, cy - r),
                size = androidx.compose.ui.geometry.Size(r * 2, r * 2)
            )
            // Fill
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                style = Stroke(width = s, cap = StrokeCap.Round),
                topLeft = Offset(cx - r, cy - r),
                size = androidx.compose.ui.geometry.Size(r * 2, r * 2)
            )
        }
        Text(
            text = "${(progress * 100).toInt()}",
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun Canvas(modifier: Modifier, onDraw: androidx.compose.ui.graphics.drawscope.DrawScope.() -> Unit) {
    androidx.compose.foundation.Canvas(modifier = modifier, onDraw = onDraw)
}

// ── Rating Stars ──────────────────────────────────────────────────

@Composable
fun RatingStars(
    rating: Int,
    maxRating: Int = 5,
    size: Dp = 12.dp,
    activeColor: Color = MaktabaColors.Gold,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(1.dp)) {
        repeat(maxRating) { i ->
            Text(
                text = if (i < rating) "★" else "☆",
                fontSize = size.value.sp,
                color = if (i < rating) activeColor else MaktabaColors.Border2
            )
        }
    }
}

// ── Interactive Rating Selector ───────────────────────────────────

@Composable
fun RatingSelectorRow(
    rating: Int,
    onRatingChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(5) { i ->
            Text(
                text = if (i < rating) "★" else "★",
                fontSize = 26.sp,
                color = if (i < rating) MaktabaColors.Gold else MaktabaColors.Border2,
                modifier = Modifier.clickable { onRatingChange(i + 1) }
            )
        }
    }
}

// ── Status Badge ──────────────────────────────────────────────────

@Composable
fun StatusBadge(status: ReadingStatus, modifier: Modifier = Modifier) {
    val (text, bg, fg) = when (status) {
        ReadingStatus.READING   -> Triple("Reading",   MaktabaColors.TealBg,  MaktabaColors.Teal)
        ReadingStatus.COMPLETED -> Triple("Done",      Color(0x1E4ADE80),     MaktabaColors.Green)
        ReadingStatus.TO_READ   -> Triple("To Read",   MaktabaColors.Surface3, MaktabaColors.TextSecondary)
        ReadingStatus.DROPPED   -> Triple("Dropped",   Color(0x1EF87171),     MaktabaColors.Red)
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .border(0.5.dp, fg.copy(alpha = .4f), RoundedCornerShape(20.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            color = fg,
            letterSpacing = 0.4.sp
        )
    }
}

// ── Genre Chip ────────────────────────────────────────────────────

@Composable
fun GenreChip(genre: Genre, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaktabaColors.GoldBg)
            .border(0.5.dp, MaktabaColors.GoldDim, RoundedCornerShape(20.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = genre.displayName.uppercase(),
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaktabaColors.Gold,
            letterSpacing = 0.5.sp
        )
    }
}

// ── Filter Chip Row ───────────────────────────────────────────────

@Composable
fun MaktabaFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (selected) MaktabaColors.GoldDim else MaktabaColors.Surface2
    val border = if (selected) MaktabaColors.Gold else MaktabaColors.Border
    val textColor = if (selected) MaktabaColors.Gold else MaktabaColors.TextTertiary

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor,
            letterSpacing = 0.3.sp
        )
    }
}

// ── Top Accent Line Card ──────────────────────────────────────────

@Composable
fun AccentCard(
    modifier: Modifier = Modifier,
    accentColor: Color = MaktabaColors.Gold,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaktabaColors.Surface2)
            .border(1.dp, MaktabaColors.Border, RoundedCornerShape(20.dp))
            .drawBehind {
                drawRect(
                    color = accentColor,
                    topLeft = Offset.Zero,
                    size = androidx.compose.ui.geometry.Size(size.width, 2.dp.toPx())
                )
            },
        content = content
    )
}

// ── Section Label ─────────────────────────────────────────────────

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        fontSize = 9.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaktabaColors.TextTertiary,
        letterSpacing = 1.2.sp,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 6.dp)
    )
}

// ── Maktaba Text Field ────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaktabaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardOptions: androidx.compose.foundation.text.KeyboardOptions =
        androidx.compose.foundation.text.KeyboardOptions.Default,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    minLines: Int = 1,
) {
    Column(modifier = modifier) {
        Text(
            text = label.uppercase(),
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isError) MaktabaColors.Red else MaktabaColors.TextTertiary,
            letterSpacing = 0.8.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(placeholder, color = MaktabaColors.TextTertiary, fontSize = 13.sp)
            },
            isError = isError,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            keyboardOptions = keyboardOptions,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = MaktabaColors.Gold,
                unfocusedBorderColor = MaktabaColors.Border,
                errorBorderColor     = MaktabaColors.Red,
                focusedTextColor     = MaktabaColors.TextPrimary,
                unfocusedTextColor   = MaktabaColors.TextPrimary,
                cursorColor          = MaktabaColors.Gold,
                focusedContainerColor   = MaktabaColors.Surface2,
                unfocusedContainerColor = MaktabaColors.Surface2,
                errorContainerColor     = MaktabaColors.Surface2,
            ),
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = MaktabaColors.TextPrimary
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                fontSize = 10.sp,
                color = MaktabaColors.Red,
                modifier = Modifier.padding(top = 2.dp, start = 4.dp)
            )
        }
    }
}
