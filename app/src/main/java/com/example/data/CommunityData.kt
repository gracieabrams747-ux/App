package com.example.data

import java.util.UUID

enum class FriendStatus {
    NONE,
    REQUEST_SENT,
    REQUEST_RECEIVED,
    FRIENDS
}

data class DevoteeProfile(
    val id: String,
    val name: String,
    val avatarId: String,
    val customAvatarUri: String? = null,
    val veteranRank: String,
    val daysActive: Int,
    val isVip: Boolean = false,
    val isLegendary: Boolean = false,
    val location: String,
    val bio: String,
    val friendStatus: FriendStatus = FriendStatus.NONE
)

data class SocialMessage(
    val id: String = UUID.randomUUID().toString(),
    val senderId: String,
    val senderName: String,
    val senderAvatarId: String,
    val senderCustomAvatarUri: String? = null,
    val senderRank: String,
    val isVipSender: Boolean = false,
    val isLegendarySender: Boolean = false,
    val text: String,
    val audioPath: String? = null,
    val audioDurationSeconds: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val likesCount: Int = 0,
    val isLikedByMe: Boolean = false
)
