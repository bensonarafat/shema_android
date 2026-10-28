package com.pulsereality.shema.data.model

data class Currency(
    val xp: Int = 0,
    val gem: Int = 0,
    val key: Int = 0
)

data class Streak(
    val id: String = "",
    val userId: String = "",
    val date: Long = System.currentTimeMillis()
)

data class ShemaUser(
    val id: String = "",
    val fullName: String = "",
    val username: String = "",
    val email: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val lastLogin: Long = System.currentTimeMillis(),
    val pushNotification: Boolean = true,
    val completeOnboarding: Boolean = false,
    val goal: String? = null,
    val focus: String? = null,
    val translation: String = "ESV",
    val devotionTime: Long? = null,
    val gender: String? = null,
    val profilePicture: String? = null,
    val subscriptionStatus: String? = null, // "free", "active", "expired", "cancelled"
    val subscriptionPlan: String? = null,   // "weekly", "monthly", "yearly"
    val currency: Currency = Currency(),
    val fcmToken: String? = null
)
