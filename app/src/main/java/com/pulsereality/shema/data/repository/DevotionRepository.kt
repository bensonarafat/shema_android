package com.pulsereality.shema.data.repository

import com.pulsereality.shema.data.model.Devotion
import com.pulsereality.shema.data.model.DevotionLocale
import com.pulsereality.shema.data.model.ScriptureVerse
import com.pulsereality.shema.data.model.VerseTranslation
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

interface DevotionRepository {
    suspend fun getDevotion(
        date: String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()),
        lang: String = "en"
    ): Result<Pair<Devotion, DevotionLocale>>
}

class FakeDevotionRepository : DevotionRepository {
    override suspend fun getDevotion(
        date: String,
        lang: String
    ): Result<Pair<Devotion, DevotionLocale>> {
        val core = Devotion(
            id = date,
            date = date,
            reference = "Philippians 4:6-7",
            gems = 5,
            xp = 20
        )
        val locale = DevotionLocale(
            lang = lang,
            theme = "Peace Beyond All Understanding",
            reflection = "In the midst of life's relentless demands and unforeseen storms, anxiety often knocks at the door of our hearts. Yet Scripture invites us not to suppress our fears, but to transform them into conversation with the Living God.",
            prayer = "Heavenly Father, quiet my racing thoughts. Guard my heart and mind in Christ Jesus, and let Your transcendent peace anchor my soul today. In Jesus' name, Amen.",
            actionStep = "Identify one worry that occupied your thoughts this morning. Write it down, present it to God in prayer, and release the outcome into His hands.",
            verses = listOf(
                ScriptureVerse(
                    vs = "6",
                    translations = listOf(
                        VerseTranslation("ESV", "Do not be anxious about anything, but in everything by prayer and supplication with thanksgiving let your requests be made known to God.", "Philippians 4:6"),
                        VerseTranslation("KJV", "Be careful for nothing; but in every thing by prayer and supplication with thanksgiving let your requests be made known unto God.", "Philippians 4:6"),
                        VerseTranslation("NLT", "Don’t worry about anything; instead, pray about everything. Tell God what you need, and thank him for all he has done.", "Philippians 4:6")
                    )
                ),
                ScriptureVerse(
                    vs = "7",
                    translations = listOf(
                        VerseTranslation("ESV", "And the peace of God, which surpasses all understanding, will guard your hearts and your minds in Christ Jesus.", "Philippians 4:7"),
                        VerseTranslation("KJV", "And the peace of God, which passeth all understanding, shall keep your hearts and minds through Christ Jesus.", "Philippians 4:7"),
                        VerseTranslation("NLT", "Then you will experience God’s peace, which exceeds anything we can understand. His peace will guard your hearts and minds as you live in Christ Jesus.", "Philippians 4:7")
                    )
                )
            )
        )
        return Result.success(Pair(core, locale))
    }
}
