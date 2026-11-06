package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.PremiumPackage
import com.example.data.UserProfileManager
import com.example.ui.theme.MarianGold
import com.example.ui.theme.MarianGoldDeep
import com.example.ui.theme.MarianRosePink

@Composable
fun PremiumShopDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val isVip by UserProfileManager.isVip.collectAsState()
    val isLegendary by UserProfileManager.isLegendary.collectAsState()
    val coins by UserProfileManager.coins.collectAsState()

    var awaitingConfirmationPackage by remember { mutableStateOf<PremiumPackage?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("premium_shop_dialog"),
            color = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 10.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(MarianGold, Color(0xFFE65100))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Tienda VIP & Pay-to-Win",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif
                                ),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Pagos directos a PayPal: ${UserProfileManager.PAYPAL_EMAIL}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // VIP Status Banner
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = if (isLegendary) {
                        Color(0xFF261900)
                    } else if (isVip) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    },
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isLegendary) MarianGold else MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isLegendary) Icons.Default.WorkspacePremium else Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = if (isLegendary) MarianGold else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isLegendary) "👑 Estado: Serafín Legendario (x3 Monedas)" else if (isVip) "★ Estado: Devoto VIP Activo (x2 Monedas)" else "Estado: Devoto Estándar (x1)",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isLegendary) MarianGold else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Saldo actual: $coins monedas de la Virgen",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Packages List
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(UserProfileManager.premiumPackages, key = { it.id }) { pkg ->
                        PremiumPackageCard(
                            pkg = pkg,
                            onBuy = {
                                openPayPalPayment(context, pkg)
                                awaitingConfirmationPackage = pkg
                            }
                        )
                    }
                }

                // If awaiting confirmation after returning from PayPal
                awaitingConfirmationPackage?.let { pkg ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = MarianGold.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MarianGold)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "¿Completaste el pago en PayPal?",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MarianGold
                            )
                            Text(
                                text = "Pulsa el botón de abajo para activar ${pkg.title} en tu cuenta.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(
                                    onClick = {
                                        UserProfileManager.activatePremiumPackage(pkg)
                                        Toast.makeText(context, "¡${pkg.title} activado con éxito! Monedas agregadas.", Toast.LENGTH_LONG).show()
                                        awaitingConfirmationPackage = null
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MarianGoldDeep),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Confirmar y Activar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PremiumPackageCard(
    pkg: PremiumPackage,
    onBuy: () -> Unit
) {
    val isLegendary = pkg.isLegendary

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("premium_card_${pkg.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLegendary) Color(0xFF1E1708) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isLegendary) 6.dp else 2.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isLegendary) 2.dp else 1.dp,
            color = if (isLegendary) MarianGold else MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tag pill
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isLegendary) MarianGold else MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = pkg.tag,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isLegendary) Color.Black else MaterialTheme.colorScheme.primary
                        )
                    )
                }

                // Price Badge
                Text(
                    text = pkg.priceFormatted,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isLegendary) MarianGold else MaterialTheme.colorScheme.primary
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = pkg.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif
                ),
                color = if (isLegendary) MarianGold else MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = pkg.description,
                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = null,
                        tint = MarianGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "+${pkg.coinsReward} monedas",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MarianGold
                        )
                    )
                }

                Button(
                    onClick = onBuy,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isLegendary) MarianGoldDeep else Color(0xFF0070BA) // PayPal brand blue
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Payment,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "PayPal",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

private fun openPayPalPayment(context: Context, pkg: PremiumPackage) {
    try {
        // Direct PayPal Checkout URL configured to send funds to the user's PayPal email
        val encodedItemName = Uri.encode("${pkg.title} - Virgen Maria App")
        val paypalUrl = "https://www.paypal.com/cgi-bin/webscr?cmd=_xclick&business=${UserProfileManager.PAYPAL_EMAIL}&item_name=$encodedItemName&amount=${pkg.priceUsd}&currency_code=USD"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(paypalUrl))
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "No se pudo abrir el navegador para PayPal", Toast.LENGTH_SHORT).show()
    }
}
