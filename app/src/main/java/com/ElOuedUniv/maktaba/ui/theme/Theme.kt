package com.ElOuedUniv.maktaba.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ── Brand Palette ─────────────────────────────────────────────────

object MaktabaColors {
    // Backgrounds
    val Bg          = Color(0xFF0A0C10)
    val Surface1    = Color(0xFF111318)
    val Surface2    = Color(0xFF181C23)
    val Surface3    = Color(0xFF1E222B)
    val Border      = Color(0xFF252A35)
    val Border2     = Color(0xFF2E3545)

    // Accents
    val Gold        = Color(0xFFC9A84C)
    val GoldLight   = Color(0xFFE8C97A)
    val GoldDim     = Color(0xFF6B5520)
    val GoldBg      = Color(0x28C9A84C)

    val Teal        = Color(0xFF2ABFA3)
    val TealLight   = Color(0xFF50D4BA)
    val TealDim     = Color(0xFF0E6B5A)
    val TealBg      = Color(0x1E2ABFA3)

    // Text
    val TextPrimary   = Color(0xFFE8EAF0)
    val TextSecondary = Color(0xFF8B90A0)
    val TextTertiary  = Color(0xFF4A5060)

    // Status
    val Blue   = Color(0xFF60A5FA)
    val Green  = Color(0xFF4ADE80)
    val Purple = Color(0xFFC084FC)
    val Red    = Color(0xFFF87171)
}

// ── Color Scheme ──────────────────────────────────────────────────

private val MaktabaDarkColorScheme = darkColorScheme(
    primary            = MaktabaColors.Gold,
    onPrimary          = MaktabaColors.Bg,
    primaryContainer   = MaktabaColors.GoldDim,
    onPrimaryContainer = MaktabaColors.GoldLight,
    secondary          = MaktabaColors.Teal,
    onSecondary        = MaktabaColors.Bg,
    secondaryContainer = MaktabaColors.TealDim,
    onSecondaryContainer = MaktabaColors.TealLight,
    tertiary           = MaktabaColors.Blue,
    background         = MaktabaColors.Bg,
    surface            = MaktabaColors.Surface1,
    surfaceVariant     = MaktabaColors.Surface2,
    onBackground       = MaktabaColors.TextPrimary,
    onSurface          = MaktabaColors.TextPrimary,
    onSurfaceVariant   = MaktabaColors.TextSecondary,
    outline            = MaktabaColors.Border,
    outlineVariant     = MaktabaColors.Border2,
    error              = MaktabaColors.Red,
)

// ── Typography ────────────────────────────────────────────────────

val MaktabaTypography = Typography(
    displayLarge = TextStyle(
        fontFamily  = FontFamily.Serif,
        fontWeight  = FontWeight.Bold,
        fontSize    = 32.sp,
        lineHeight  = 38.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineLarge = TextStyle(
        fontFamily  = FontFamily.Serif,
        fontWeight  = FontWeight.SemiBold,
        fontSize    = 26.sp,
        lineHeight  = 32.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily  = FontFamily.Serif,
        fontWeight  = FontWeight.SemiBold,
        fontSize    = 20.sp,
        lineHeight  = 26.sp,
    ),
    titleLarge = TextStyle(
        fontWeight  = FontWeight.SemiBold,
        fontSize    = 16.sp,
        lineHeight  = 22.sp,
    ),
    titleMedium = TextStyle(
        fontWeight  = FontWeight.Medium,
        fontSize    = 14.sp,
        lineHeight  = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontWeight  = FontWeight.Normal,
        fontSize    = 14.sp,
        lineHeight  = 21.sp,
    ),
    bodyMedium = TextStyle(
        fontWeight  = FontWeight.Normal,
        fontSize    = 12.sp,
        lineHeight  = 18.sp,
    ),
    bodySmall = TextStyle(
        fontWeight  = FontWeight.Normal,
        fontSize    = 11.sp,
        lineHeight  = 16.sp,
        letterSpacing = 0.2.sp
    ),
    labelSmall = TextStyle(
        fontWeight  = FontWeight.SemiBold,
        fontSize    = 9.sp,
        lineHeight  = 14.sp,
        letterSpacing = 0.8.sp
    )
)

// ── Theme ─────────────────────────────────────────────────────────

@Composable
fun MaktabaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MaktabaDarkColorScheme,
        typography  = MaktabaTypography,
        shapes      = Shapes(
            extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(8),
            small      = androidx.compose.foundation.shape.RoundedCornerShape(10),
            medium     = androidx.compose.foundation.shape.RoundedCornerShape(14),
            large      = androidx.compose.foundation.shape.RoundedCornerShape(20),
            extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(28)
        ),
        content = content
    )
}
