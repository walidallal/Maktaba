package com.ElOuedUniv.maktaba.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ElOuedUniv.maktaba.ui.navigation.Screen
import com.ElOuedUniv.maktaba.ui.theme.MaktabaColors

data class BottomNavItem(
    val screen: Screen,
    val icon: String,     // emoji icon
    val label: String,
    val isCenter: Boolean = false
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.BookList,   "⊞", "Library"),
    BottomNavItem(Screen.Favorites,  "★", "Favs"),
    BottomNavItem(Screen.AddBook,    "＋", "Add", isCenter = true),
    BottomNavItem(Screen.Statistics, "◎", "Stats"),
    BottomNavItem(Screen.BookList,   "◈", "Profile"),   // placeholder
)

@Composable
fun MaktabaBottomBar(
    currentRoute: String?,
    onNavigate: (Screen) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaktabaColors.Surface1)
            .border(
                width = 0.5.dp,
                color = MaktabaColors.Border,
                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            bottomNavItems.forEach { item ->
                if (item.isCenter) {
                    CenterNavButton(onClick = { onNavigate(item.screen) })
                } else {
                    val isActive = currentRoute == item.screen.route
                    RegularNavItem(
                        item = item,
                        isActive = isActive,
                        onClick = { onNavigate(item.screen) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RowScope.RegularNavItem(
    item: BottomNavItem,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1.08f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )
    val iconColor = if (isActive) MaktabaColors.Teal else MaktabaColors.TextTertiary
    val labelColor = if (isActive) MaktabaColors.Teal else MaktabaColors.TextTertiary

    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable(onClick = onClick)
            .scale(scale),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = item.icon,
            fontSize = 20.sp,
            color = iconColor,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = item.label.uppercase(),
            fontSize = 8.sp,
            fontWeight = FontWeight.SemiBold,
            color = labelColor,
            letterSpacing = 0.5.sp
        )
        // Active dot
        if (isActive) {
            Spacer(Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(MaktabaColors.Teal)
            )
        }
    }
}

@Composable
private fun RowScope.CenterNavButton(onClick: () -> Unit) {
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "fab_scale"
    )

    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .offset(y = (-10).dp)
                .size(52.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(MaktabaColors.Gold)
                .border(3.dp, MaktabaColors.Surface1, CircleShape)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "＋",
                fontSize = 24.sp,
                color = MaktabaColors.Bg,
                fontWeight = FontWeight.Bold
            )
        }
    }
}