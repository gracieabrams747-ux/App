package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object CommunityManager {
    private const val PREFS_COMMUNITY = "marian_community_prefs"
    private lateinit var prefs: SharedPreferences

    private val _publicMessages = MutableStateFlow<List<SocialMessage>>(emptyList())
    val publicMessages: StateFlow<List<SocialMessage>> = _publicMessages.asStateFlow()

    private val _devotees = MutableStateFlow<List<DevoteeProfile>>(emptyList())
    val devotees: StateFlow<List<DevoteeProfile>> = _devotees.asStateFlow()

    private val _privateChats = MutableStateFlow<Map<String, List<SocialMessage>>>(emptyMap())
    val privateChats: StateFlow<Map<String, List<SocialMessage>>> = _privateChats.asStateFlow()

    fun initialize(context: Context) {
        prefs = context.getSharedPreferences(PREFS_COMMUNITY, Context.MODE_PRIVATE)

        // Seed initial devotees if empty
        if (_devotees.value.isEmpty()) {
            _devotees.value = listOf(
                DevoteeProfile(
                    id = "dev_claudia",
                    name = "Claudia de Guadalupe",
                    avatarId = "avatar_corona",
                    veteranRank = "Hija Predilecta VIP",
                    daysActive = 240,
                    isVip = true,
                    location = "Tepeyac, México",
                    bio = "Consagrada a la Virgen de Guadalupe desde hace años. Orando siempre bajo su bendito manto.",
                    friendStatus = FriendStatus.FRIENDS
                ),
                DevoteeProfile(
                    id = "dev_mateo",
                    name = "Mateo Custodio del Manto",
                    avatarId = "avatar_medalla",
                    veteranRank = "Veterana Mariana de Oro",
                    daysActive = 390,
                    isVip = true,
                    isLegendary = true,
                    location = "Zaragoza, España",
                    bio = "Custodio del manto sagrado. Comparto la paz y el consuelo de María con cada hermano.",
                    friendStatus = FriendStatus.NONE
                ),
                DevoteeProfile(
                    id = "dev_esperanza",
                    name = "Esperanza de Lourdes",
                    avatarId = "avatar_lirio",
                    veteranRank = "Peregrina Fiel",
                    daysActive = 72,
                    isVip = false,
                    location = "Lima, Perú",
                    bio = "Pidiendo amparo y consuelo a nuestra Madre de Lourdes para todos los enfermos.",
                    friendStatus = FriendStatus.REQUEST_RECEIVED
                ),
                DevoteeProfile(
                    id = "dev_lucia",
                    name = "Lucía del Inmaculado Corazón",
                    avatarId = "avatar_rosa",
                    veteranRank = "Custodia del Manto",
                    daysActive = 115,
                    isVip = false,
                    location = "Bogotá, Colombia",
                    bio = "Ofreciendo rosas a María cada amanecer. Si necesitas oración, agrégame.",
                    friendStatus = FriendStatus.NONE
                ),
                DevoteeProfile(
                    id = "dev_ignacio",
                    name = "Ignacio Peregrino",
                    avatarId = "avatar_manto",
                    veteranRank = "Devoto Constante",
                    daysActive = 28,
                    isVip = false,
                    location = "Córdoba, Argentina",
                    bio = "Caminante mariano aprendiendo la ternura y la docilidad de nuestra Madre.",
                    friendStatus = FriendStatus.NONE
                )
            )
        }

        // Seed initial community public messages
        if (_publicMessages.value.isEmpty()) {
            _publicMessages.value = listOf(
                SocialMessage(
                    id = "msg_init_1",
                    senderId = "dev_claudia",
                    senderName = "Claudia de Guadalupe",
                    senderAvatarId = "avatar_corona",
                    senderRank = "Hija Predilecta VIP",
                    isVipSender = true,
                    text = "¡Paz y gracia en María para todos los devotos! Hoy encendí una lámpara virtual a los pies de nuestra Madre de Guadalupe pidiendo amparo para todas nuestras familias.",
                    likesCount = 14,
                    timestamp = System.currentTimeMillis() - 7200000
                ),
                SocialMessage(
                    id = "msg_init_2",
                    senderId = "dev_mateo",
                    senderName = "Mateo Custodio del Manto",
                    senderAvatarId = "avatar_medalla",
                    senderRank = "Veterana Mariana de Oro",
                    isVipSender = true,
                    isLegendarySender = true,
                    text = "Les envío esta breve nota de voz con una plegaria cariñosa a la Reina del Cielo:",
                    audioDurationSeconds = 6,
                    likesCount = 21,
                    timestamp = System.currentTimeMillis() - 3600000
                ),
                SocialMessage(
                    id = "msg_init_3",
                    senderId = "dev_esperanza",
                    senderName = "Esperanza de Lourdes",
                    senderAvatarId = "avatar_lirio",
                    senderRank = "Peregrina Fiel",
                    isVipSender = false,
                    text = "Hermanos, ¿alguien más sintió una profunda calma al rezar la oración a la Virgen de la Paz? Es maravilloso saber que nunca caminamos solos.",
                    likesCount = 9,
                    timestamp = System.currentTimeMillis() - 1800000
                )
            )
        }

        // Seed initial private messages with Claudia
        if (!_privateChats.value.containsKey("dev_claudia")) {
            _privateChats.value = mapOf(
                "dev_claudia" to listOf(
                    SocialMessage(
                        id = "priv_1",
                        senderId = "dev_claudia",
                        senderName = "Claudia de Guadalupe",
                        senderAvatarId = "avatar_corona",
                        senderRank = "Hija Predilecta VIP",
                        isVipSender = true,
                        text = "¡Hola querido hermano/a en María! Qué alegría ser compañeros de oración. Que la Madre te colme de gracias hoy.",
                        timestamp = System.currentTimeMillis() - 10800000
                    )
                )
            )
        }
    }

    fun sendPublicMessage(text: String, audioPath: String? = null, audioDuration: Int = 0) {
        val currentUserName = UserProfileManager.userName.value
        val avatarId = UserProfileManager.avatarId.value
        val customUri = UserProfileManager.customAvatarUri.value
        val rank = UserProfileManager.veteranRank.value
        val isVip = UserProfileManager.isVip.value
        val isLegendary = UserProfileManager.isLegendary.value

        val newMsg = SocialMessage(
            id = UUID.randomUUID().toString(),
            senderId = "my_user_id",
            senderName = currentUserName,
            senderAvatarId = avatarId,
            senderCustomAvatarUri = customUri,
            senderRank = rank,
            isVipSender = isVip,
            isLegendarySender = isLegendary,
            text = text,
            audioPath = audioPath,
            audioDurationSeconds = audioDuration,
            timestamp = System.currentTimeMillis()
        )

        _publicMessages.value = listOf(newMsg) + _publicMessages.value
        UserProfileManager.addCoins(15) // Reward for participating in community
    }

    fun sendPrivateMessage(friendId: String, text: String, audioPath: String? = null, audioDuration: Int = 0) {
        val currentUserName = UserProfileManager.userName.value
        val avatarId = UserProfileManager.avatarId.value
        val customUri = UserProfileManager.customAvatarUri.value
        val rank = UserProfileManager.veteranRank.value
        val isVip = UserProfileManager.isVip.value
        val isLegendary = UserProfileManager.isLegendary.value

        val newMsg = SocialMessage(
            id = UUID.randomUUID().toString(),
            senderId = "my_user_id",
            senderName = currentUserName,
            senderAvatarId = avatarId,
            senderCustomAvatarUri = customUri,
            senderRank = rank,
            isVipSender = isVip,
            isLegendarySender = isLegendary,
            text = text,
            audioPath = audioPath,
            audioDurationSeconds = audioDuration,
            timestamp = System.currentTimeMillis()
        )

        val currentList = _privateChats.value[friendId] ?: emptyList()
        val updatedMap = _privateChats.value.toMutableMap()
        updatedMap[friendId] = currentList + newMsg
        _privateChats.value = updatedMap
    }

    fun sendFriendRequest(devoteeId: String) {
        _devotees.value = _devotees.value.map { devotee ->
            if (devotee.id == devoteeId) {
                devotee.copy(friendStatus = FriendStatus.REQUEST_SENT)
            } else devotee
        }
    }

    fun acceptFriendRequest(devoteeId: String) {
        _devotees.value = _devotees.value.map { devotee ->
            if (devotee.id == devoteeId) {
                devotee.copy(friendStatus = FriendStatus.FRIENDS)
            } else devotee
        }
    }

    fun rejectFriendRequest(devoteeId: String) {
        _devotees.value = _devotees.value.map { devotee ->
            if (devotee.id == devoteeId) {
                devotee.copy(friendStatus = FriendStatus.NONE)
            } else devotee
        }
    }

    fun toggleLike(messageId: String) {
        _publicMessages.value = _publicMessages.value.map { msg ->
            if (msg.id == messageId) {
                val newLiked = !msg.isLikedByMe
                val newCount = if (newLiked) msg.likesCount + 1 else msg.likesCount - 1
                msg.copy(isLikedByMe = newLiked, likesCount = maxOf(0, newCount))
            } else msg
        }
    }
}
