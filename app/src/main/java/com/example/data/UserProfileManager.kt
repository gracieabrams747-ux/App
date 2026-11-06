package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

data class ShopItem(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val costCoins: Int,
    val iconName: String,
    val unlockedContent: String? = null
)

object UserProfileManager {
    private const val PREFS_NAME = "marian_user_profile"
    private const val KEY_COINS = "user_coins"
    private const val KEY_CREATED_TIMESTAMP = "user_created_timestamp"
    private const val KEY_USER_NAME = "user_display_name"
    private const val KEY_UNLOCKED_ITEMS = "user_unlocked_items"
    private const val KEY_ROSES_GIVEN = "user_roses_given_total"
    private const val KEY_MESSAGES_SENT = "user_messages_sent_total"
    private const val KEY_LAST_DAILY_BONUS = "user_last_daily_bonus_day"
    private const val KEY_SELECTED_TITLE = "user_selected_title"

    private lateinit var prefs: SharedPreferences

    private val _coins = MutableStateFlow(100)
    val coins: StateFlow<Int> = _coins.asStateFlow()

    private val _userName = MutableStateFlow("Devoto/a de María")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _createdDateFormatted = MutableStateFlow("")
    val createdDateFormatted: StateFlow<String> = _createdDateFormatted.asStateFlow()

    private val _daysActive = MutableStateFlow(1)
    val daysActive: StateFlow<Int> = _daysActive.asStateFlow()

    private val _veteranRank = MutableStateFlow("Hijo Devoto")
    val veteranRank: StateFlow<String> = _veteranRank.asStateFlow()

    private val _unlockedItemIds = MutableStateFlow<Set<String>>(emptySet())
    val unlockedItemIds: StateFlow<Set<String>> = _unlockedItemIds.asStateFlow()

    private val _rosesCount = MutableStateFlow(0)
    val rosesCount: StateFlow<Int> = _rosesCount.asStateFlow()

    private val _messagesCount = MutableStateFlow(0)
    val messagesCount: StateFlow<Int> = _messagesCount.asStateFlow()

    fun initialize(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Account creation timestamp: record today if brand new
        var createdTime = prefs.getLong(KEY_CREATED_TIMESTAMP, 0L)
        if (createdTime == 0L) {
            createdTime = System.currentTimeMillis()
            prefs.edit().putLong(KEY_CREATED_TIMESTAMP, createdTime).apply()
        }

        val dateFormat = SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale("es", "ES"))
        _createdDateFormatted.value = dateFormat.format(Date(createdTime))

        // Calculate days since account creation
        val diffMillis = System.currentTimeMillis() - createdTime
        val days = (TimeUnit.MILLISECONDS.toDays(diffMillis) + 1).toInt()
        _daysActive.value = days

        // Load coins (give 100 welcome coins if first time)
        val savedCoins = prefs.getInt(KEY_COINS, 100)
        _coins.value = savedCoins

        // Load user name
        _userName.value = prefs.getString(KEY_USER_NAME, "Hijo/a de María") ?: "Hijo/a de María"

        // Load unlocked items
        val savedUnlocked = prefs.getStringSet(KEY_UNLOCKED_ITEMS, setOf("title_iniciado")) ?: setOf("title_iniciado")
        _unlockedItemIds.value = savedUnlocked

        // Selected title
        _veteranRank.value = prefs.getString(KEY_SELECTED_TITLE, calculateVeteranRank(days)) ?: calculateVeteranRank(days)

        _rosesCount.value = prefs.getInt(KEY_ROSES_GIVEN, 0)
        _messagesCount.value = prefs.getInt(KEY_MESSAGES_SENT, 0)

        // Check daily devotional bonus (+25 coins)
        checkDailyBonus()
    }

    private fun calculateVeteranRank(days: Int): String {
        return when {
            days >= 365 -> "Veterana Mariana de Oro (Más de 1 año)"
            days >= 90 -> "Custodio del Manto (Más de 3 meses)"
            days >= 30 -> "Peregrino Fiel (Más de 1 mes)"
            days >= 7 -> "Devoto Constante (1 semana)"
            else -> "Hijo Devoto (Recién consagrado)"
        }
    }

    private fun checkDailyBonus() {
        val todayStr = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val lastDay = prefs.getString(KEY_LAST_DAILY_BONUS, "")
        if (lastDay != todayStr) {
            prefs.edit().putString(KEY_LAST_DAILY_BONUS, todayStr).apply()
            addCoins(25) // Daily devotion bonus
        }
    }

    fun addCoins(amount: Int) {
        val newAmount = _coins.value + amount
        _coins.value = newAmount
        prefs.edit().putInt(KEY_COINS, newAmount).apply()
    }

    fun spendCoins(amount: Int): Boolean {
        if (_coins.value < amount) return false
        val newAmount = _coins.value - amount
        _coins.value = newAmount
        prefs.edit().putInt(KEY_COINS, newAmount).apply()
        return true
    }

    fun unlockItem(item: ShopItem): Boolean {
        if (_unlockedItemIds.value.contains(item.id)) return true
        if (spendCoins(item.costCoins)) {
            val updated = _unlockedItemIds.value + item.id
            _unlockedItemIds.value = updated
            prefs.edit().putStringSet(KEY_UNLOCKED_ITEMS, updated).apply()

            // If it's a title, select it automatically
            if (item.category == "Títulos de Veteranía") {
                setVeteranRank(item.title)
            }
            return true
        }
        return false
    }

    fun isItemUnlocked(itemId: String): Boolean {
        return _unlockedItemIds.value.contains(itemId)
    }

    fun setUserName(name: String) {
        val trimmed = name.trim()
        if (trimmed.isNotBlank()) {
            _userName.value = trimmed
            prefs.edit().putString(KEY_USER_NAME, trimmed).apply()
        }
    }

    fun setVeteranRank(rank: String) {
        _veteranRank.value = rank
        prefs.edit().putString(KEY_SELECTED_TITLE, rank).apply()
    }

    fun recordRoseGiven() {
        val newTotal = _rosesCount.value + 1
        _rosesCount.value = newTotal
        prefs.edit().putInt(KEY_ROSES_GIVEN, newTotal).apply()
        addCoins(5)
    }

    fun recordMessageSent() {
        val newTotal = _messagesCount.value + 1
        _messagesCount.value = newTotal
        prefs.edit().putInt(KEY_MESSAGES_SENT, newTotal).apply()
        addCoins(10)
    }

    val shopCatalog = listOf(
        // Títulos de Veteranía y Honor
        ShopItem(
            id = "title_iniciado",
            title = "Hijo Devoto",
            category = "Títulos de Veteranía",
            description = "Distintivo concedido a quien inicia con amor su devoción a María.",
            costCoins = 0,
            iconName = "star"
        ),
        ShopItem(
            id = "title_peregrino",
            title = "Peregrino Fiel",
            category = "Títulos de Veteranía",
            description = "Insignia que certifica constancia y devoción continua en la presencia maternal.",
            costCoins = 150,
            iconName = "military_tech"
        ),
        ShopItem(
            id = "title_custodio",
            title = "Custodio del Manto",
            category = "Títulos de Veteranía",
            description = "Rango de honor para quienes buscan el amparo incondicional de María.",
            costCoins = 300,
            iconName = "shield"
        ),
        ShopItem(
            id = "title_veterana_oro",
            title = "Veterana Mariana de Oro",
            category = "Títulos de Veteranía",
            description = "El máximo rango de veteranía mariana. Demuestra entrega, fidelidad y perseverancia.",
            costCoins = 500,
            iconName = "workspace_premium"
        ),
        // Bendiciones Especiales y Mensajes Secretos de María
        ShopItem(
            id = "blessing_paz_interior",
            title = "Bendición de Paz Interior",
            category = "Bendiciones Celestiales",
            description = "Un mensaje sagrado de consuelo personal para serenar las tempestades del alma.",
            costCoins = 80,
            iconName = "spa",
            unlockedContent = "Hijo mío amadísimo, derramo sobre tu frente mi bendición de rocío celestial. Cuando el mundo ruja a tu alrededor, recuerda que en mi corazón tienes un aposento tranquilo que nadie puede perturbar. Camina en calma, que yo velo por ti."
        ),
        ShopItem(
            id = "blessing_desatar_nudos",
            title = "Oráculo de la Madre Desatanudos",
            category = "Bendiciones Celestiales",
            description = "Misterio de liberación para desenredar los nudos del corazón y la vida.",
            costCoins = 120,
            iconName = "auto_awesome",
            unlockedContent = "Pequeño mío, mis manos maternales toman hoy esa situación que te agobia y la desenredan con paciencia de seda. Lo que para ti es confusión, para mí es ocasión de mostrarte mi amor. Descansa y confía en mi auxilio."
        ),
        ShopItem(
            id = "blessing_manto_estrellas",
            title = "Cobijo del Manto de 12 Estrellas",
            category = "Bendiciones Celestiales",
            description = "Consagración suprema bajo las doce estrellas resplandecientes de María.",
            costCoins = 200,
            iconName = "stars",
            unlockedContent = "Extiendo sobre tu hogar y tu vida los pliegues de mi manto azul medianoche. Las doce estrellas que coronan mi cabeza son doce promesas vivas de que nunca caminarás en soledad ni desamparo."
        ),
        // Recompensas Estéticas y Fondos Exclusivos
        ShopItem(
            id = "reward_halo_dorado",
            title = "Aureola de Oro de Fátima",
            category = "Recompensas Visuales",
            description = "Emblema brillante para engalanar tu perfil de devoto con resplandor dorado.",
            costCoins = 180,
            iconName = "lightbulb"
        ),
        ShopItem(
            id = "reward_rosas_misticas",
            title = "Rosas Místicas Eternas",
            category = "Recompensas Visuales",
            description = "Ofrenda de rosas inmarcesibles que perfuman tu trayectoria de devoción.",
            costCoins = 220,
            iconName = "local_florist"
        )
    )
}
