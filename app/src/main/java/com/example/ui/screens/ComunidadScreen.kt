package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.core.content.ContextCompat
import com.example.data.CommunityManager
import com.example.data.DevoteeProfile
import com.example.data.FriendStatus
import com.example.data.SocialMessage
import com.example.data.UserProfileManager
import com.example.ui.components.MarianAvatar
import com.example.ui.components.MarianHeader
import com.example.ui.theme.MarianGold
import com.example.ui.theme.MarianGoldDeep
import com.example.ui.theme.MarianRosePink
import com.example.ui.theme.MarianSkyBlue
import com.example.util.MaryVoiceManager
import com.example.util.VoiceNotePlayer
import com.example.util.VoiceNoteRecorder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ComunidadScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        CommunityManager.initialize(context)
    }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Muro Comunitario", "Devotos y Amigos", "Solicitudes")

    val publicMessages by CommunityManager.publicMessages.collectAsState()
    val devotees by CommunityManager.devotees.collectAsState()

    var activeChatFriend by remember { mutableStateOf<DevoteeProfile?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("comunidad_screen")
    ) {
        MarianHeader(
            title = "Comunidad Mariana",
            subtitle = "Muro de fe, notas de voz y hermanos en María"
        )

        // 3 Navigation Tabs
        PrimaryTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, title ->
                val requestCount = devotees.count { it.friendStatus == FriendStatus.REQUEST_RECEIVED }
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                            if (index == 2 && requestCount > 0) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(MarianRosePink),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$requestCount",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }

        when (selectedTabIndex) {
            0 -> MuroComunitarioView(
                messages = publicMessages,
                modifier = Modifier.weight(1f)
            )
            1 -> DevotosListView(
                devotees = devotees,
                onStartChat = { devotee -> activeChatFriend = devotee },
                modifier = Modifier.weight(1f)
            )
            2 -> SolicitudesView(
                devotees = devotees.filter { it.friendStatus == FriendStatus.REQUEST_RECEIVED },
                onStartChat = { devotee -> activeChatFriend = devotee },
                modifier = Modifier.weight(1f)
            )
        }
    }

    // Direct 1-on-1 private chat dialog
    activeChatFriend?.let { friend ->
        PrivateChatDialog(
            friend = friend,
            onDismiss = { activeChatFriend = null }
        )
    }
}

@Composable
private fun MuroComunitarioView(
    messages: List<SocialMessage>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var inputText by remember { mutableStateOf("") }
    val isRecording by VoiceNoteRecorder.isRecording.collectAsState()
    val recordingSeconds by VoiceNoteRecorder.recordingSeconds.collectAsState()
    val listState = rememberLazyListState()

    // Permission launcher for voice note microphone recording
    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val started = VoiceNoteRecorder.startRecording(context)
            if (!started) {
                Toast.makeText(context, "No se pudo iniciar la grabación", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Permiso de micrófono necesario para enviar audios", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .imePadding()
    ) {
        // Messages feed
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = messages,
                key = { it.id },
                contentType = { "social_message" }
            ) { msg ->
                SocialMessageCard(
                    message = msg,
                    onToggleLike = { CommunityManager.toggleLike(msg.id) }
                )
            }
        }

        // Recording indicator banner
        AnimatedVisibility(visible = isRecording) {
            Surface(
                color = MarianRosePink.copy(alpha = 0.15f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color.Red)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Grabando nota de voz mariana... 0:${recordingSeconds.toString().padStart(2, '0')}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(
                        onClick = { VoiceNoteRecorder.stopRecording() },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Cancelar", tint = Color.Red, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        // Bottom Input bar for text & voice notes
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("community_input_field"),
                    placeholder = {
                        Text(
                            text = if (isRecording) "Grabando audio..." else "Escribe una petición, alabanza o reflexión...",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    maxLines = 3,
                    enabled = !isRecording
                )

                Spacer(modifier = Modifier.width(6.dp))

                // Microphone button for recording voice notes (free for all)
                IconButton(
                    onClick = {
                        if (isRecording) {
                            val result = VoiceNoteRecorder.stopAndGetResult()
                            if (result != null) {
                                CommunityManager.sendPublicMessage(
                                    text = "Nota de voz mariana (${result.durationSeconds}s)",
                                    audioPath = result.filePath,
                                    audioDuration = result.durationSeconds
                                )
                                Toast.makeText(context, "¡Nota de voz compartida (+15 Monedas)!", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                                VoiceNoteRecorder.startRecording(context)
                            } else {
                                micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            if (isRecording) MarianRosePink
                            else MaterialTheme.colorScheme.primaryContainer
                        )
                        .testTag("record_voice_note_button")
                ) {
                    Icon(
                        imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = if (isRecording) "Detener y enviar" else "Grabar nota de voz",
                        tint = if (isRecording) Color.White else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                if (inputText.isNotBlank()) {
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = {
                            val textToSend = inputText
                            inputText = ""
                            CommunityManager.sendPublicMessage(textToSend)
                            Toast.makeText(context, "¡Mensaje publicado (+15 Monedas)!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .testTag("send_community_message_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Enviar",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SocialMessageCard(
    message: SocialMessage,
    onToggleLike: () -> Unit
) {
    val playingAudioId by VoiceNotePlayer.playingAudioId.collectAsState()
    val playbackProgress by VoiceNotePlayer.playbackProgress.collectAsState()
    val isPlayingThis = playingAudioId == message.id

    val timeStr = remember(message.timestamp) {
        val diff = System.currentTimeMillis() - message.timestamp
        when {
            diff < 60000 -> "Hace un momento"
            diff < 3600000 -> "Hace ${(diff / 60000)} min"
            diff < 86400000 -> "Hace ${(diff / 3600000)} h"
            else -> SimpleDateFormat("d MMM", Locale("es", "ES")).format(Date(message.timestamp))
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("social_message_${message.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (message.isVipSender) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (message.isVipSender) 1.5.dp else 1.dp,
            color = if (message.isVipSender) MarianGold.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Avatar, Name, VIP crown, Rank, Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MarianAvatar(
                    avatarId = message.senderAvatarId,
                    customUri = message.senderCustomAvatarUri,
                    isVip = message.isVipSender,
                    isLegendary = message.isLegendarySender,
                    size = 38.dp
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = message.senderName,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (message.isVipSender) MarianGold else MaterialTheme.colorScheme.onSurface
                        )
                        if (message.isVipSender) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = "Miembro VIP",
                                tint = MarianGold,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Text(
                        text = "${message.senderRank} • $timeStr",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Text content
            if (message.text.isNotBlank()) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 21.sp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Audio Player (Voice Note)
            if (message.audioDurationSeconds > 0 || !message.audioPath.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                VoiceNotePlayer.play(
                                    audioId = message.id,
                                    filePath = message.audioPath,
                                    durationSeconds = maxOf(3, message.audioDurationSeconds),
                                    onSimulatedText = {
                                        MaryVoiceManager.speak("Paz y amparo de nuestra Madre María para tu alma.")
                                    }
                                )
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(
                                imageVector = if (isPlayingThis) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = if (isPlayingThis) "Detener" else "Reproducir audio",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.GraphicEq,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Nota de Voz Mariana",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Text(
                                    text = "0:${message.audioDurationSeconds.toString().padStart(2, '0')}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { if (isPlayingThis) playbackProgress else 0f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action row: Like / Corazón mariano
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onToggleLike)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (message.isLikedByMe) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Me consuela",
                        tint = if (message.isLikedByMe) MarianRosePink else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (message.likesCount > 0) "${message.likesCount}" else "Ofrecer flor",
                        fontSize = 12.sp,
                        color = if (message.isLikedByMe) MarianRosePink else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (message.isLikedByMe) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
private fun DevotosListView(
    devotees: List<DevoteeProfile>,
    onStartChat: (DevoteeProfile) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }

    val filtered = remember(searchQuery, devotees) {
        if (searchQuery.isBlank()) devotees
        else devotees.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.location.contains(searchQuery, ignoreCase = true) ||
            it.veteranRank.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar hermanos en María...", fontSize = 13.sp) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            },
            shape = RoundedCornerShape(18.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(
                items = filtered,
                key = { it.id },
                contentType = { "devotee_item" }
            ) { devotee ->
                DevoteeCard(
                    devotee = devotee,
                    onSendRequest = {
                        CommunityManager.sendFriendRequest(devotee.id)
                        Toast.makeText(context, "Solicitud de oración enviada a ${devotee.name}", Toast.LENGTH_SHORT).show()
                    },
                    onStartChat = { onStartChat(devotee) }
                )
            }
        }
    }
}

@Composable
private fun DevoteeCard(
    devotee: DevoteeProfile,
    onSendRequest: () -> Unit,
    onStartChat: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MarianAvatar(
                avatarId = devotee.avatarId,
                customUri = devotee.customAvatarUri,
                isVip = devotee.isVip,
                isLegendary = devotee.isLegendary,
                size = 48.dp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = devotee.name,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (devotee.isVip) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = "VIP",
                            tint = MarianGold,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Text(
                    text = "${devotee.veteranRank} • ${devotee.location}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 11.sp
                )
                Text(
                    text = "${devotee.daysActive} días en la devoción",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            when (devotee.friendStatus) {
                FriendStatus.NONE -> {
                    Button(
                        onClick = onSendRequest,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.Default.GroupAdd, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Agregar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
                FriendStatus.REQUEST_SENT -> {
                    OutlinedButton(
                        onClick = {},
                        enabled = false,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Enviada ✓", fontSize = 11.sp)
                    }
                }
                FriendStatus.REQUEST_RECEIVED -> {
                    Button(
                        onClick = { CommunityManager.acceptFriendRequest(devotee.id) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MarianGoldDeep),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Aceptar", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
                FriendStatus.FRIENDS -> {
                    Button(
                        onClick = onStartChat,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Chatear", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun SolicitudesView(
    devotees: List<DevoteeProfile>,
    onStartChat: (DevoteeProfile) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    if (devotees.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Groups,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "No tienes solicitudes pendientes",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Conoce hermanos en María en la pestaña 'Devotos y Amigos'",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(devotees, key = { it.id }) { devotee ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            MarianAvatar(
                                avatarId = devotee.avatarId,
                                customUri = devotee.customAvatarUri,
                                isVip = devotee.isVip,
                                isLegendary = devotee.isLegendary,
                                size = 44.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = devotee.name,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Desea ser tu hermano/a de oración",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            OutlinedButton(
                                onClick = { CommunityManager.rejectFriendRequest(devotee.id) },
                                modifier = Modifier.height(36.dp),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Rechazar", fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    CommunityManager.acceptFriendRequest(devotee.id)
                                    Toast.makeText(context, "¡Ahora son compañeros de oración con ${devotee.name}!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.height(36.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("Aceptar en María", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PrivateChatDialog(
    friend: DevoteeProfile,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val allChats by CommunityManager.privateChats.collectAsState()
    val messages = allChats[friend.id] ?: emptyList()

    var inputText by remember { mutableStateOf("") }
    val isRecording by VoiceNoteRecorder.isRecording.collectAsState()
    val recordingSeconds by VoiceNoteRecorder.recordingSeconds.collectAsState()

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            VoiceNoteRecorder.startRecording(context)
        } else {
            Toast.makeText(context, "Permiso de micrófono necesario para enviar audios", Toast.LENGTH_SHORT).show()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(520.dp)
                .clip(RoundedCornerShape(20.dp))
                .testTag("private_chat_dialog"),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Friend Header
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MarianAvatar(
                            avatarId = friend.avatarId,
                            customUri = friend.customAvatarUri,
                            isVip = friend.isVip,
                            isLegendary = friend.isLegendary,
                            size = 36.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = friend.name,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Compañero/a de oración",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                // Chat Messages List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    contentPadding = PaddingValues(vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(messages, key = { it.id }) { msg ->
                        val isMe = msg.senderId == "my_user_id"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                        ) {
                            Surface(
                                color = if (isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(12.dp),
                                tonalElevation = 2.dp,
                                modifier = Modifier.widthIn(max = 240.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = msg.text,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                // Bottom Input
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Escribe un mensaje privado...", fontSize = 12.sp) },
                            shape = RoundedCornerShape(20.dp),
                            maxLines = 2
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = {
                                if (inputText.isNotBlank()) {
                                    val text = inputText
                                    inputText = ""
                                    CommunityManager.sendPrivateMessage(friend.id, text)
                                }
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}
