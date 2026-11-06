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
import androidx.compose.material.icons.filled.Done
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

            // Top gradient overlay with close button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Black.copy(alpha = 0.7f), Color.Transparent)
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 28.dp)
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .testTag("close_preview_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = Color.White
                    )
                }
            }

            // Bottom controls overlay
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                color = Color.Black.copy(alpha = 0.85f),
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

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = wallpaper.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (!showTargetSelector) {
                        Button(
                            onClick = { showTargetSelector = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
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
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Establecer como Fondo de Pantalla",
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    } else {
                        // Options: Home screen, Lock screen, or Both
                        Text(
                            text = "¿Dónde deseas aplicar este fondo?",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (isSettingWallpaper) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(28.dp),
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
