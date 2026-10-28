package com.pulsereality.shema.data.model

data class PrayerRequest(
    val id: String = "",
    val userId: String = "",
    val displayName: String = "",
    val avatarSeed: String? = null,
    val type: String = "general",
    val title: String = "",
    val body: String = "",
    val reference: String? = null,
    val submittedAt: Long = System.currentTimeMillis(),
    val prayedAt: Long = 0L,
    val prayCount: Int = 0,
    val replyCount: Int = 0
)
