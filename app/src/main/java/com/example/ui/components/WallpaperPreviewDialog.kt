package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.WallpaperItem
import com.example.ui.theme.MarianGold
import com.example.ui.theme.MarianGoldDeep
import com.example.util.WallpaperHelper
import com.example.util.WallpaperTarget
import kotlinx.coroutines.launch

@Composable
fun WallpaperPreviewDialog(
    wallpaper: WallpaperItem,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isSettingWallpaper by remember { mutableStateOf(false) }
    var isSavingToGallery by remember { mutableStateOf(false) }
    var showTargetSelector by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .testTag("wallpaper_preview_dialog")
        ) {
            // Full screen wallpaper image
            Image(
                painter = painterResource(id = wallpaper.drawableRes),
                contentDescription = wallpaper.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Top gradient overlay with action buttons
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Black.copy(alpha = 0.75f), Color.Transparent)
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 28.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Share button
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                val shareResult = WallpaperHelper.shareWallpaper(context, wallpaper.drawableRes, wallpaper.title)
                                shareResult.onSuccess { intent ->
                                    context.startActivity(android.content.Intent.createChooser(intent, "Compartir fondo mariano"))
                                }.onFailure {
                                    Toast.makeText(context, "No se pudo compartir la imagen", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Compartir fondo",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Close button
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                            .testTag("close_preview_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Bottom controls overlay
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                color = Color.Black.copy(alpha = 0.88f),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = wallpaper.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        ),
                        color = MarianGold
                    )
                    Text(
                        text = wallpaper.subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = wallpaper.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    var isUnlocked by remember { mutableStateOf(com.example.data.UserProfileManager.isWallpaperUnlocked(wallpaper)) }
                    var showPremiumShop by remember { mutableStateOf(false) }

                    if (!isUnlocked) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Surface(
                                color = MarianGold.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MarianGold.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                            ) {
                                Text(
                                    text = "👑 Fondo Exclusivo VIP. Desbloquéalo con tu Pase VIP o por 150 Monedas de la Virgen.",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(10.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        val success = com.example.data.UserProfileManager.unlockWallpaper(wallpaper.id)
                                        if (success) {
                                            isUnlocked = true
                                            Toast.makeText(context, "¡Fondo VIP desbloqueado!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Monedas insuficientes (Cuesta 150)", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MarianGold)
                                ) {
                                    Text("Usar 150 Monedas", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MarianGold)
                                }

                                Button(
                                    onClick = { showPremiumShop = true },
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MarianGold, contentColor = Color.Black)
                                ) {
                                    Text("Pase VIP ($1.99)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        if (showPremiumShop) {
                            PremiumShopDialog(onDismiss = {
                                showPremiumShop = false
                                isUnlocked = com.example.data.UserProfileManager.isWallpaperUnlocked(wallpaper)
                            })
                        }
                    } else if (!showTargetSelector) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Save to gallery button
                            OutlinedButton(
                                onClick = {
                                    isSavingToGallery = true
                                    coroutineScope.launch {
                                        val result = WallpaperHelper.saveWallpaperToGallery(context, wallpaper.drawableRes, wallpaper.title)
                                        isSavingToGallery = false
                                        if (result.isSuccess) {
                                            com.example.data.UserProfileManager.addCoins(20)
                                            Toast.makeText(context, "¡Guardado con éxito en tu Galería (+20 Monedas de la Virgen)!", Toast.LENGTH_LONG).show()
                                        } else {
                                            Toast.makeText(context, "No se pudo guardar en la galería", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .testTag("save_gallery_button"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)),
                                enabled = !isSavingToGallery
                            ) {
                                if (isSavingToGallery) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        strokeWidth = 2.dp,
                                        color = Color.White
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Download,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Guardar",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            // Set as wallpaper button
                            Button(
                                onClick = { showTargetSelector = true },
                                modifier = Modifier
                                    .weight(2f)
                                    .height(50.dp)
                                    .testTag("set_wallpaper_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MarianGoldDeep,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Wallpaper,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Aplicar Fondo",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    } else {
                        // Options: Home screen, Lock screen, or Both
                        Text(
                            text = "¿Dónde deseas aplicar este fondo?",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        if (isSettingWallpaper) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(26.dp),
                                    strokeWidth = 2.dp,
                                    color = MarianGold
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Aplicando fondo sagrado...",
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilledTonalButton(
                                    onClick = {
                                        applyWallpaper(
                                            wallpaper = wallpaper,
                                            target = WallpaperTarget.HOME,
                                            onStart = { isSettingWallpaper = true },
                                            onFinish = { success ->
                                                isSettingWallpaper = false
                                                if (success) {
                                                    Toast.makeText(context, "¡Fondo aplicado a Pantalla de Inicio!", Toast.LENGTH_SHORT).show()
                                                    onDismiss()
                                                } else {
                                                    Toast.makeText(context, "No se pudo cambiar el fondo", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            coroutineScope = coroutineScope,
                                            context = context
                                        )
                                    },
                                    modifier = Modifier.weight(1f).height(46.dp),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Inicio", fontSize = 12.sp)
                                }

                                FilledTonalButton(
                                    onClick = {
                                        applyWallpaper(
                                            wallpaper = wallpaper,
                                            target = WallpaperTarget.LOCK,
                                            onStart = { isSettingWallpaper = true },
                                            onFinish = { success ->
                                                isSettingWallpaper = false
                                                if (success) {
                                                    Toast.makeText(context, "¡Fondo aplicado a Pantalla de Bloqueo!", Toast.LENGTH_SHORT).show()
                                                    onDismiss()
                                                } else {
                                                    Toast.makeText(context, "No se pudo cambiar el fondo", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            coroutineScope = coroutineScope,
                                            context = context
                                        )
                                    },
                                    modifier = Modifier.weight(1f).height(46.dp),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Bloqueo", fontSize = 12.sp)
                                }

                                Button(
                                    onClick = {
                                        applyWallpaper(
                                            wallpaper = wallpaper,
                                            target = WallpaperTarget.BOTH,
                                            onStart = { isSettingWallpaper = true },
                                            onFinish = { success ->
                                                isSettingWallpaper = false
                                                if (success) {
                                                    Toast.makeText(context, "¡Fondo sagrado aplicado a todo tu dispositivo!", Toast.LENGTH_SHORT).show()
                                                    onDismiss()
                                                } else {
                                                    Toast.makeText(context, "No se pudo cambiar el fondo", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            coroutineScope = coroutineScope,
                                            context = context
                                        )
                                    },
                                    modifier = Modifier.weight(1f).height(46.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MarianGoldDeep),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Ambas", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun applyWallpaper(
    wallpaper: WallpaperItem,
    target: WallpaperTarget,
    onStart: () -> Unit,
    onFinish: (Boolean) -> Unit,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
    context: android.content.Context
) {
    onStart()
    coroutineScope.launch {
        val result = WallpaperHelper.setWallpaperFromResource(context, wallpaper.drawableRes, target)
        onFinish(result.isSuccess)
    }
}
