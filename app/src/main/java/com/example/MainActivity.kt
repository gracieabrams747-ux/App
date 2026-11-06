package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.ui.Alignment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Groups
import com.example.ui.screens.AdvocacionesScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.ComunidadScreen
import com.example.ui.screens.PrayersScreen
import com.example.ui.screens.WallpapersScreen
import com.example.ui.theme.MarianGold
import com.example.ui.theme.VirgenMariaTheme
import com.example.util.configureAppCheck
import com.example.viewmodel.MaryChatViewModel

enum class MarianScreen(val title: String, val icon: ImageVector) {
    CHAT("Voz de María", Icons.AutoMirrored.Filled.Chat),
    COMUNIDAD("Comunidad", Icons.Default.Groups),
    WALLPAPERS("Fondos", Icons.Default.Wallpaper),
    ADVOCACIONES("Advocaciones", Icons.Default.AutoAwesome),
    PRAYERS("Oraciones", Icons.Default.LocalFlorist)
}

class MainActivity : ComponentActivity() {

    private val chatViewModel: MaryChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mandatory Phase 1: Configure App Check before UI or Firebase operations
        configureAppCheck(this, intent)

        // Initialize User Profile and Coins/Rewards system
        com.example.data.UserProfileManager.initialize(this)
        com.example.data.CommunityManager.initialize(this)

        enableEdgeToEdge()

        // High refresh rate mode (up to 120Hz/144Hz) for ultra-smooth 120 FPS animations
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            try {
                val display = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                    display
                } else {
                    @Suppress("DEPRECATION")
                    windowManager.defaultDisplay
                }
                val modes = display?.supportedModes
                val maxRefreshRateMode = modes?.maxByOrNull { it.refreshRate }
                if (maxRefreshRateMode != null && maxRefreshRateMode.refreshRate >= 90f) {
                    val params = window.attributes
                    params.preferredDisplayModeId = maxRefreshRateMode.modeId
                    window.attributes = params
                }
            } catch (_: Exception) {
                // Device does not support changing display modes
            }
        }

        setContent {
            VirgenMariaTheme {
                MarianMainApp(
                    chatViewModel = chatViewModel
                )
            }
        }
    }
}

@Composable
fun MarianMainApp(
    chatViewModel: MaryChatViewModel
) {
    var currentScreen by remember { mutableStateOf(MarianScreen.CHAT) }
    val density = androidx.compose.ui.platform.LocalDensity.current
    val isImeVisible = WindowInsets.ime.getBottom(density) > 0

    // Back handling: return to chat if on any other tab
    if (currentScreen != MarianScreen.CHAT) {
        BackHandler {
            currentScreen = MarianScreen.CHAT
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
        bottomBar = {
            // Hide bottom navigation bar when soft keyboard is open, freeing full screen space
            if (!isImeVisible) {
                NavigationBar(
                    modifier = Modifier.testTag("marian_bottom_navigation"),
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    MarianScreen.entries.forEach { screen ->
                        val isSelected = currentScreen == screen
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentScreen = screen },
                            icon = {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = screen.title
                                )
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag("nav_item_${screen.name.lowercase()}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        // innerPadding accounts for bottomBar when visible and 0 when keyboard is open
        val contentModifier = Modifier
            .fillMaxSize()
            .padding(bottom = innerPadding.calculateBottomPadding())

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 840.dp)
            ) {
                when (currentScreen) {
                    MarianScreen.CHAT -> {
                        ChatScreen(
                            viewModel = chatViewModel,
                            modifier = contentModifier
                        )
                    }
                    MarianScreen.COMUNIDAD -> {
                        ComunidadScreen(
                            modifier = contentModifier
                        )
                    }
                    MarianScreen.WALLPAPERS -> {
                        WallpapersScreen(
                            modifier = contentModifier
                        )
                    }
                    MarianScreen.ADVOCACIONES -> {
                        AdvocacionesScreen(
                            onAskMaryAboutAdvocation = { prompt ->
                                chatViewModel.sendMessage(prompt)
                                currentScreen = MarianScreen.CHAT
                            },
                            modifier = contentModifier
                        )
                    }
                    MarianScreen.PRAYERS -> {
                        PrayersScreen(
                            modifier = contentModifier
                        )
                    }
                }
            }
        }
    }
}
