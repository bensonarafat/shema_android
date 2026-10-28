package com.pulsereality.shema.data.model

data class Devotion(
    val id: String = "",
    val reference: String = "",
    val gems: Int = 0,
    val xp: Int = 0,
    val date: String = "" // YYYY-MM-DD
)

data class DevotionLocale(
    val lang: String = "en",
    val theme: String = "",
    val reflection: String = "",
    val prayer: String = "",
    val actionStep: String = "",
    val verses: List<ScriptureVerse> = emptyList()
)

data class ScriptureVerse(
    val vs: String = "",
    val translations: List<VerseTranslation> = emptyList()
) {
    val id: String get() = vs
}

data class VerseTranslation(
    val book: String = "",
    val text: String = "",
    val reference: String = ""
) {
    val id: String get() = "$book-$reference"
}
