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

data class PremiumPackage(
    val id: String,
    val title: String,
    val priceUsd: Double,
    val priceFormatted: String,
    val description: String,
    val coinsReward: Int,
    val badgeTitle: String,
    val isLegendary: Boolean = false,
    val tag: String
)

object UserProfileManager {
    const val PAYPAL_EMAIL = "vabe950@gmail.com"

    private const val PREFS_NAME = "marian_user_profile"
    private const val KEY_COINS = "user_coins"
    private const val KEY_CREATED_TIMESTAMP = "user_created_timestamp"
    private const val KEY_USER_NAME = "user_display_name"
    private const val KEY_UNLOCKED_ITEMS = "user_unlocked_items"
    private const val KEY_ROSES_GIVEN = "user_roses_given_total"
    private const val KEY_MESSAGES_SENT = "user_messages_sent_total"
    private const val KEY_LAST_DAILY_BONUS = "user_last_daily_bonus_day"
    private const val KEY_SELECTED_TITLE = "user_selected_title"
    private const val KEY_IS_VIP = "user_is_vip_pass"
    private const val KEY_IS_LEGENDARY = "user_is_legendary_crown"
    private const val KEY_AVATAR_ID = "user_selected_avatar_id"
    private const val KEY_CUSTOM_AVATAR_URI = "user_custom_avatar_uri"

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

    private val _isVip = MutableStateFlow(false)
    val isVip: StateFlow<Boolean> = _isVip.asStateFlow()

    private val _isLegendary = MutableStateFlow(false)
    val isLegendary: StateFlow<Boolean> = _isLegendary.asStateFlow()

    private val _avatarId = MutableStateFlow("avatar_corona")
    val avatarId: StateFlow<String> = _avatarId.asStateFlow()

    private val _customAvatarUri = MutableStateFlow<String?>(null)
    val customAvatarUri: StateFlow<String?> = _customAvatarUri.asStateFlow()

    val premiumPackages = listOf(
        PremiumPackage(
            id = "pkg_gracia_inicial",
            title = "Paquete Gracia Inicial",
            priceUsd = 0.99,
            priceFormatted = "$0.99 USD",
            description = "+500 Monedas de la Virgen inmediatas para compras rápidas en la tienda.",
            coinsReward = 500,
            badgeTitle = "Devoto Apoyador",
            tag = "MÁS BARATO"
        ),
        PremiumPackage(
            id = "pkg_pase_vip",
            title = "Pase Devoto VIP (x2 Monedas)",
            priceUsd = 1.99,
            priceFormatted = "$1.99 USD",
            description = "+1,200 Monedas + MULTIPLICADOR x2 permanente + Acceso a TODOS los Fondos Exclusivos VIP + Corona e Insignia dorada en Chat Social.",
            coinsReward = 1200,
            badgeTitle = "Hijo Predilecto VIP",
            tag = "POPULAR"
        ),
        PremiumPackage(
            id = "pkg_manto_celestial",
            title = "Cofre del Manto Celestial",
            priceUsd = 4.99,
            priceFormatted = "$4.99 USD",
            description = "+3,500 Monedas + Desbloquea Fondos VIP + Todas las Bendiciones Secretas y Avatar VIP Reina Soberana.",
            coinsReward = 3500,
            badgeTitle = "Custodio Mayor del Manto",
            tag = "RECOMENDADO"
        ),
        PremiumPackage(
            id = "pkg_corona_legendaria",
            title = "Reliquia: Corona de 12 Estrellas",
            priceUsd = 19.99,
            priceFormatted = "$19.99 USD",
            description = "+15,000 Monedas + Rango SUPREMO 'Serafín Custodio' + Multiplicador x3 permanente + Desbloquea Fondos VIP y TODO el catálogo de la tienda.",
            coinsReward = 15000,
            badgeTitle = "Serafín Custodio Legendario",
            isLegendary = true,
            tag = "👑 LEGENDARIO SUPREMO"
        )
    )

    fun initialize(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Account creation timestamp
        var createdTime = prefs.getLong(KEY_CREATED_TIMESTAMP, 0L)
        if (createdTime == 0L) {
            createdTime = System.currentTimeMillis()
            prefs.edit().putLong(KEY_CREATED_TIMESTAMP, createdTime).apply()
        }

        val dateFormat = SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale("es", "ES"))
        _createdDateFormatted.value = dateFormat.format(Date(createdTime))

        val diffMillis = System.currentTimeMillis() - createdTime
        val days = (TimeUnit.MILLISECONDS.toDays(diffMillis) + 1).toInt()
        _daysActive.value = days

        _coins.value = prefs.getInt(KEY_COINS, 100)
        _userName.value = prefs.getString(KEY_USER_NAME, "Hijo/a de María") ?: "Hijo/a de María"

        val savedUnlocked = prefs.getStringSet(KEY_UNLOCKED_ITEMS, setOf("title_iniciado")) ?: setOf("title_iniciado")
        _unlockedItemIds.value = savedUnlocked

        _isVip.value = prefs.getBoolean(KEY_IS_VIP, false)
        _isLegendary.value = prefs.getBoolean(KEY_IS_LEGENDARY, false)

        val defaultRank = calculateVeteranRank(days)
        _veteranRank.value = prefs.getString(KEY_SELECTED_TITLE, defaultRank) ?: defaultRank

        _rosesCount.value = prefs.getInt(KEY_ROSES_GIVEN, 0)
        _messagesCount.value = prefs.getInt(KEY_MESSAGES_SENT, 0)

        _avatarId.value = prefs.getString(KEY_AVATAR_ID, "avatar_corona") ?: "avatar_corona"
        _customAvatarUri.value = prefs.getString(KEY_CUSTOM_AVATAR_URI, null)

        checkDailyBonus()
    }

    private fun calculateVeteranRank(days: Int): String {
        return when {
            _isLegendary.value -> "Serafín Custodio Legendario (Supremo)"
            _isVip.value -> "Hijo Predilecto VIP (Monedas x2)"
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
            val bonus = if (_isLegendary.value) 75 else if (_isVip.value) 50 else 25
            addCoins(bonus)
        }
    }

    fun addCoins(amount: Int) {
        val multiplier = if (_isLegendary.value) 3 else if (_isVip.value) 2 else 1
        val finalAmount = amount * multiplier
        val newAmount = _coins.value + finalAmount
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

            if (item.category == "Títulos de Veteranía") {
                setVeteranRank(item.title)
            }
            return true
        }
        return false
    }

    fun activatePremiumPackage(pkg: PremiumPackage) {
        // Direct coin reward
        val newCoins = _coins.value + pkg.coinsReward
        _coins.value = newCoins
        prefs.edit().putInt(KEY_COINS, newCoins).apply()

        // Activate VIP or Legendary perks
        if (pkg.id == "pkg_pase_vip") {
            _isVip.value = true
            prefs.edit().putBoolean(KEY_IS_VIP, true).apply()
        }
        if (pkg.isLegendary) {
            _isLegendary.value = true
            _isVip.value = true
            prefs.edit().putBoolean(KEY_IS_LEGENDARY, true).putBoolean(KEY_IS_VIP, true).apply()

            // Unlock all shop items for the legendary supporter!
            val allIds = shopCatalog.map { it.id }.toSet()
            _unlockedItemIds.value = allIds
            prefs.edit().putStringSet(KEY_UNLOCKED_ITEMS, allIds).apply()
        }

        // Equip prestige title
        setVeteranRank(pkg.badgeTitle)
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

    fun setAvatarId(id: String) {
        _avatarId.value = id
        _customAvatarUri.value = null
        prefs.edit().putString(KEY_AVATAR_ID, id).remove(KEY_CUSTOM_AVATAR_URI).apply()
    }

    fun setCustomAvatarUri(uriString: String?) {
        _customAvatarUri.value = uriString
        if (uriString != null) {
            prefs.edit().putString(KEY_CUSTOM_AVATAR_URI, uriString).apply()
        } else {
            prefs.edit().remove(KEY_CUSTOM_AVATAR_URI).apply()
        }
    }

    fun isWallpaperUnlocked(wallpaper: WallpaperItem): Boolean {
        if (!wallpaper.isPremium) return true
        return _isVip.value || _isLegendary.value || _unlockedItemIds.value.contains("wp_${wallpaper.id}")
    }

    fun unlockWallpaper(wallpaperId: String): Boolean {
        val key = "wp_$wallpaperId"
        if (_unlockedItemIds.value.contains(key)) return true
        if (spendCoins(150)) {
            val updated = _unlockedItemIds.value + key
            _unlockedItemIds.value = updated
            prefs.edit().putStringSet(KEY_UNLOCKED_ITEMS, updated).apply()
            return true
        }
        return false
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
