package com.pulsereality.shema.data.model

data class Book(
    val bookid: Int = 0,
    val name: String = "",
    val chapters: Int = 0
) {
    val id: Int get() = bookid
}

data class Verse(
    val pk: Int = 0,
    val translation: String? = null,
    val book: Int? = null,
    val chapter: Int? = null,
    val verse: Int = 0,
    val text: String = "",
    val comment: String? = null
) {
    val id: Int get() = pk

    val cleanText: String
        get() = text
            .replace(Regex("<S>[^>]*</S>"), " ")
            .replace("<br/>", " ")
            .replace(Regex("<[^>]+>"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
}

data class BibleReference(
    val book: Int,
    val chapter: Int,
    val verse: Int? = null,
    val translation: String = "ESV"
)

data class Translation(
    val shortName: String = "",
    val fullName: String = ""
) {
    val id: String get() = shortName
}
