package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserProfileManager
import com.example.ui.theme.MarianGold

@Composable
fun MarianHeader(
    title: String,
    subtitle: String? = null,
    actions: @Composable (() -> Unit)? = null
) {
    val coins by UserProfileManager.coins.collectAsState()
    val avatarId by UserProfileManager.avatarId.collectAsState()
    val customAvatarUri by UserProfileManager.customAvatarUri.collectAsState()
    val isVip by UserProfileManager.isVip.collectAsState()
    val isLegendary by UserProfileManager.isLegendary.collectAsState()
    var showShop by remember { mutableStateOf(false) }
    var showPremiumShop by remember { mutableStateOf(false) }
    var showProfile by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("marian_header"),
        color = MaterialTheme.colorScheme.background,
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User's custom or sacred Marian avatar
                MarianAvatar(
                    avatarId = avatarId,
                    customUri = customAvatarUri,
                    isVip = isVip,
                    isLegendary = isLegendary,
                    size = 40.dp,
                    modifier = Modifier.clickable { showProfile = true }
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            letterSpacing = 0.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1
                        )
                    }
                }

                if (actions != null) {
                    actions()
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Interactive Coins Chip (Tap to open Shop)
                Surface(
                    color = MarianGold.copy(alpha = 0.18f),
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MarianGold.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { showShop = true }
                        .testTag("header_coins_chip")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = "Monedas de la Virgen",
                            tint = MarianGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$coins",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MarianGold
                            )
                        )
                    }
                }

                // VIP / Pay-to-win button
                IconButton(
                    onClick = { showPremiumShop = true },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("header_vip_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = "Tienda VIP & Pay-to-Win",
                        tint = MarianGold,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Profile button
                IconButton(
                    onClick = { showProfile = true },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("header_profile_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Perfil Mariano",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    }

    if (showShop) {
        ShopDialog(
            onDismiss = { showShop = false },
            onOpenVipShop = {
                showShop = false
                showPremiumShop = true
            }
        )
    }

    if (showPremiumShop) {
        PremiumShopDialog(onDismiss = { showPremiumShop = false })
    }

    if (showProfile) {
        ProfileDialog(
            onDismiss = { showProfile = false },
            onOpenShop = { showShop = true }
        )
    }
}
