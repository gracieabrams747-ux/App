package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MarianRepository
import com.example.data.WallpaperItem
import com.example.ui.components.MarianHeader
import com.example.ui.components.WallpaperPreviewDialog
import com.example.ui.theme.MarianGold

@Composable
fun WallpapersScreen(
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("Todos") }
    var selectedWallpaperForPreview by remember { mutableStateOf<WallpaperItem?>(null) }

    val categories = listOf("Todos", "Exclusivos VIP", "Advocaciones", "Vitrales y Arte", "Símbolos Celestiales")

    val filteredWallpapers = remember(selectedCategory) {
        if (selectedCategory == "Todos") {
            MarianRepository.wallpapers
        } else {
            MarianRepository.wallpapers.filter { it.category == selectedCategory }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("wallpapers_screen")
    ) {
        MarianHeader(
            title = "Fondos Marianos",
            subtitle = "Viste tu dispositivo con la presencia celestial de María"
        )

        // Category Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                val isSelected = selectedCategory == category
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategory = category },
                    label = { Text(text = category, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("filter_chip_$category")
                )
            }
        }

        // Adaptive Grid of Wallpapers (supports phones, tablets, foldables smoothly at 120 FPS)
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(vertical = 10.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(
                items = filteredWallpapers,
                key = { it.id },
                contentType = { "wallpaper_card" }
            ) { item ->
                WallpaperCard(
                    wallpaper = item,
                    onClick = { selectedWallpaperForPreview = item }
                )
            }
        }
    }

    // Full screen preview dialog
    selectedWallpaperForPreview?.let { wallpaper ->
        WallpaperPreviewDialog(
            wallpaper = wallpaper,
            onDismiss = { selectedWallpaperForPreview = null }
        )
    }
}

@Composable
private fun WallpaperCard(
    wallpaper: WallpaperItem,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("wallpaper_card_${wallpaper.id}"),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 4.dp,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(9f / 16f)
        ) {
            // Wallpaper image
            Image(
                painter = painterResource(id = wallpaper.drawableRes),
                contentDescription = wallpaper.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            val isUnlocked = com.example.data.UserProfileManager.isWallpaperUnlocked(wallpaper)

            // Top-right category pill
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clip(CircleShape)
                    .background(
                        if (wallpaper.isPremium) MarianGold.copy(alpha = 0.9f)
                        else Color.Black.copy(alpha = 0.6f)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (wallpaper.isPremium) Icons.Default.AutoAwesome else Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = if (wallpaper.isPremium) Color.Black else MarianGold,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (wallpaper.isPremium) "👑 VIP EXCLUSIVO" else wallpaper.category,
                        color = if (wallpaper.isPremium) Color.Black else Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Bottom gradient with title and tap prompt
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                        )
                    )
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = wallpaper.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        ),
                        color = Color.White,
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Wallpaper,
                            contentDescription = null,
                            tint = MarianGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Tocar para aplicar",
                            color = MarianGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
