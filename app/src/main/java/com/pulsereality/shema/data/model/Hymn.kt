package com.pulsereality.shema.data.model

enum class HymnStem(val rawValue: String) {
    VOCAL("Vocal"),
    INSTRUMENTAL("Instrumental"),
    CONGREGATION("Congregation");

    companion object {
        fun fromString(value: String?): HymnStem {
            return entries.firstOrNull { it.rawValue.equals(value, ignoreCase = true) } ?: VOCAL
        }
    }
}

data class LyricLine(
    val id: String = "",
    val time: Double = 0.0, // seconds from start
    val text: String = "",
    val verseNumber: Int? = null
)

data class Hymn(
    val id: String = "",
    val number: Int = 0,
    val title: String = "",
    val author: String? = null,
    val category: String = "",
    val duration: Double = 0.0,
    val coverImageUrl: String? = null,
    val vocalURL: String? = null,
    val isHymn: Boolean = true,
    val instrumentalURL: String? = null,
    val congregationURL: String? = null,
    val lyricsFullText: String? = null,
    val lyrics: List<LyricLine> = emptyList(),
    val isDailyFeatured: Boolean = false
) {
    private fun isValidUrl(url: String?): Boolean {
        if (url.isNullOrBlank()) return false
        val trimmed = url.trim()
        return trimmed.isNotEmpty() && !trimmed.equals("null", ignoreCase = true)
    }

    val availableStems: List<HymnStem>
        get() = buildList {
            if (isValidUrl(vocalURL)) add(HymnStem.VOCAL)
            if (isValidUrl(instrumentalURL)) add(HymnStem.INSTRUMENTAL)
            if (isValidUrl(congregationURL)) add(HymnStem.CONGREGATION)
        }

    fun urlForStem(stem: HymnStem): String? {
        val path = when (stem) {
            HymnStem.VOCAL -> vocalURL
            HymnStem.INSTRUMENTAL -> instrumentalURL
            HymnStem.CONGREGATION -> congregationURL
        }
        return if (isValidUrl(path)) path?.trim() else null
    }
}
