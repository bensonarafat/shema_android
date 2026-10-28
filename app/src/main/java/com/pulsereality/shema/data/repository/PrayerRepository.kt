package com.pulsereality.shema.data.repository

import com.pulsereality.shema.data.model.PrayerRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

interface PrayerRepository {
    fun getActivePrayer(): Flow<PrayerRequest?>
    fun getCommunityPrayers(): Flow<List<PrayerRequest>>
    suspend fun incrementPrayCount(prayerId: String)
    suspend fun submitPrayer(title: String, body: String, author: String): Result<PrayerRequest>
}

class FakePrayerRepository : PrayerRepository {
    private val activePrayerState = MutableStateFlow<PrayerRequest?>(
        PrayerRequest(
            id = "active_1",
            userId = "user_joshua",
            displayName = "Joshua T.",
            type = "intercession",
            title = "Strength & Peace for Families in Need",
            body = "Lord, grant peace to every home struggling with provision and anxiety. We pray for renewal, comfort, and Your abiding presence.",
            reference = "Philippians 4:19",
            submittedAt = System.currentTimeMillis() - 600000,
            prayedAt = System.currentTimeMillis() - 300000,
            prayCount = 42,
            replyCount = 5
        )
    )

    private val communityPrayersState = MutableStateFlow(
        listOf(
            PrayerRequest(
                id = "p_1",
                userId = "user_sarah",
                displayName = "Sarah M.",
                title = "Healing for my grandmother",
                body = "Please lift up my grandmother in prayer as she undergoes surgery tomorrow morning.",
                prayCount = 28,
                submittedAt = System.currentTimeMillis() - 7200000
            ),
            PrayerRequest(
                id = "p_2",
                userId = "user_david",
                displayName = "David K.",
                title = "Guidance on career decision",
                body = "Praying for wisdom and peace to discern God's path for our family.",
                prayCount = 19,
                submittedAt = System.currentTimeMillis() - 14400000
            ),
            PrayerRequest(
                id = "p_3",
                userId = "user_grace",
                displayName = "Grace E.",
                title = "Thanksgiving for answered prayer",
                body = "Praising the Lord for restoring health and renewing hope in our household!",
                prayCount = 54,
                submittedAt = System.currentTimeMillis() - 28800000
            )
        )
    )

    override fun getActivePrayer(): Flow<PrayerRequest?> = activePrayerState.asStateFlow()

    override fun getCommunityPrayers(): Flow<List<PrayerRequest>> = communityPrayersState.asStateFlow()

    override suspend fun incrementPrayCount(prayerId: String) {
        val currentActive = activePrayerState.value
        if (currentActive?.id == prayerId) {
            activePrayerState.value = currentActive.copy(prayCount = currentActive.prayCount + 1)
        } else {
            communityPrayersState.value = communityPrayersState.value.map { item ->
                if (item.id == prayerId) item.copy(prayCount = item.prayCount + 1) else item
            }
        }
    }

    override suspend fun submitPrayer(
        title: String,
        body: String,
        author: String
    ): Result<PrayerRequest> {
        val newPrayer = PrayerRequest(
            id = "p_${System.currentTimeMillis()}",
            userId = "current_user",
            displayName = author.ifBlank { "Anonymous Believer" },
            title = title,
            body = body,
            prayCount = 1,
            submittedAt = System.currentTimeMillis()
        )
        communityPrayersState.value = listOf(newPrayer) + communityPrayersState.value
        return Result.success(newPrayer)
    }
}
