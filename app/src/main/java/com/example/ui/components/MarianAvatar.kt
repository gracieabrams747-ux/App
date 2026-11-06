package com.example.ui.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ui.theme.MarianGold
import com.example.ui.theme.MarianRosePink
import com.example.ui.theme.MarianSkyBlue

data class AvatarPreset(
    val id: String,
    val name: String,
    val description: String,
    val icon: ImageVector,
    val tint: Color,
    val isVipOnly: Boolean = false
)

object AvatarPresets {
    val presets = listOf(
        AvatarPreset(
            id = "avatar_corona",
            name = "Corona de Doce Estrellas",
            description = "Símbolo de la realeza y amparo de María",
            icon = Icons.Default.Stars,
            tint = MarianGold
        ),
        AvatarPreset(
            id = "avatar_rosa",
            name = "Rosa Mística",
            description = "Flor espiritual de amor inmaculado",
            icon = Icons.Default.LocalFlorist,
            tint = MarianRosePink
        ),
        AvatarPreset(
            id = "avatar_manto",
            name = "Manto Celestial",
            description = "Protección bajo el velo azul de la Madre",
            icon = Icons.Default.AutoAwesome,
            tint = MarianSkyBlue
        ),
        AvatarPreset(
            id = "avatar_lirio",
            name = "Lirio Inmaculado",
            description = "Pureza de corazón y humildad perpetua",
            icon = Icons.Default.Spa,
            tint = Color(0xFFE0E0E0)
        ),
        AvatarPreset(
            id = "avatar_corazon",
            name = "Corazón Inmaculado",
            description = "Fuego maternal de ternura y refugio",
            icon = Icons.Default.Favorite,
            tint = Color(0xFFE57373)
        ),
        AvatarPreset(
            id = "avatar_medalla",
            name = "Medalla Milagrosa",
            description = "Escudo de gracias y bendición constante",
            icon = Icons.Default.Shield,
            tint = MarianGold
        ),
        AvatarPreset(
            id = "avatar_paloma",
            name = "Paloma de Paz",
            description = "Consuelo dulce para serenar el espíritu",
            icon = Icons.Default.Flight,
            tint = Color.White
        ),
        AvatarPreset(
            id = "avatar_reina",
            name = "Reina Soberana VIP",
            description = "Distintivo dorado de honor supremo mariano",
            icon = Icons.Default.WorkspacePremium,
            tint = MarianGold,
            isVipOnly = true
        )
    )

    fun getPreset(id: String): AvatarPreset {
        return presets.firstOrNull { it.id == id } ?: presets[0]
    }
}

@Composable
fun MarianAvatar(
    avatarId: String,
    customUri: String? = null,
    isVip: Boolean = false,
    isLegendary: Boolean = false,
    size: Dp = 42.dp,
    modifier: Modifier = Modifier
) {
    val preset = AvatarPresets.getPreset(avatarId)

    val borderModifier = when {
        isLegendary -> Modifier.border(
            width = 2.5.dp,
            brush = Brush.sweepGradient(
                listOf(MarianGold, Color.White, MarianRosePink, MarianGold)
            ),
            shape = CircleShape
        )
        isVip -> Modifier.border(
            width = 2.dp,
            color = MarianGold,
            shape = CircleShape
        )
        else -> Modifier.border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
            shape = CircleShape
        )
    }

    Box(
        modifier = modifier
            .size(size)
            .then(borderModifier)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = if (isLegendary || isVip) {
                        listOf(MarianGold.copy(alpha = 0.45f), MaterialTheme.colorScheme.primaryContainer)
                    } else {
                        listOf(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.surface)
                    }
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        if (!customUri.isNullOrBlank()) {
            AsyncImage(
                model = Uri.parse(customUri),
                contentDescription = "Avatar de usuario",
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = preset.icon,
                contentDescription = preset.name,
                tint = preset.tint,
                modifier = Modifier.size(size * 0.58f)
            )
        }
    }
}
