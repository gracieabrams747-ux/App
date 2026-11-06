package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Wallpaper
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
import com.example.ui.screens.AdvocacionesScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.PrayersScreen
import com.example.ui.screens.WallpapersScreen
import com.example.ui.theme.MarianGold
import com.example.ui.theme.VirgenMariaTheme
import com.example.util.configureAppCheck
import com.example.viewmodel.MaryChatViewModel

enum class MarianScreen(val title: String, val icon: ImageVector) {
    CHAT("Voz de María", Icons.AutoMirrored.Filled.Chat),
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

        enableEdgeToEdge()

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

    // Back handling: return to chat if on any other tab
    if (currentScreen != MarianScreen.CHAT) {
        BackHandler {
            currentScreen = MarianScreen.CHAT
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
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
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)

        when (currentScreen) {
            MarianScreen.CHAT -> {
                ChatScreen(
                    viewModel = chatViewModel,
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
