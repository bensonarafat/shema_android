package com.pulsereality.shema.data.repository

import com.pulsereality.shema.data.model.Hymn
import com.pulsereality.shema.data.model.LyricLine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

interface HymnRepository {
    fun getHymns(isHymn: Boolean = true, category: String? = null): Flow<List<Hymn>>
    fun getCategories(): List<String>
    suspend fun getDailyFeaturedHymn(): Hymn?
    suspend fun getHymnById(id: String): Hymn?
}

class FakeHymnRepository : HymnRepository {
    private val hymnsList = MutableStateFlow(
        listOf(
            Hymn(
                id = "hymn_1",
                number = 1,
                title = "Holy, Holy, Holy! Lord God Almighty",
                author = "Reginald Heber",
                category = "Praise & Adoration",
                duration = 222.0,
                vocalURL = "https://cdn.tryshema.com/audio/hymns/001_vocal.mp3",
                instrumentalURL = "https://cdn.tryshema.com/audio/hymns/001_instrumental.mp3",
                congregationURL = "https://cdn.tryshema.com/audio/hymns/001_congregation.mp3",
                isDailyFeatured = true,
                lyrics = listOf(
                    LyricLine("l1", 0.0, "Holy, holy, holy! Lord God Almighty!", 1),
                    LyricLine("l2", 6.5, "Early in the morning our song shall rise to Thee;", 1),
                    LyricLine("l3", 14.0, "Holy, holy, holy, merciful and mighty!", 1),
                    LyricLine("l4", 21.5, "God in three Persons, blessed Trinity!", 1),
                    LyricLine("l5", 30.0, "Holy, holy, holy! All the saints adore Thee,", 2),
                    LyricLine("l6", 37.0, "Casting down their golden crowns around the glassy sea;", 2),
                    LyricLine("l7", 45.0, "Cherubim and seraphim falling down before Thee,", 2),
                    LyricLine("l8", 52.0, "Which wert, and art, and evermore shalt be.", 2)
                )
            ),
            Hymn(
                id = "hymn_2",
                number = 23,
                title = "Amazing Grace (How Sweet the Sound)",
                author = "John Newton",
                category = "Grace & Mercy",
                duration = 255.0,
                vocalURL = "https://cdn.tryshema.com/audio/hymns/023_vocal.mp3",
                instrumentalURL = "https://cdn.tryshema.com/audio/hymns/023_instrumental.mp3",
                congregationURL = "https://cdn.tryshema.com/audio/hymns/023_congregation.mp3",
                lyrics = listOf(
                    LyricLine("l21", 0.0, "Amazing grace! how sweet the sound", 1),
                    LyricLine("l22", 7.0, "That saved a wretch like me!", 1),
                    LyricLine("l23", 15.0, "I once was lost, but now am found,", 1),
                    LyricLine("l24", 22.0, "Was blind, but now I see.", 1)
                )
            ),
            Hymn(
                id = "hymn_3",
                number = 45,
                title = "It Is Well with My Soul",
                author = "Horatio Spafford",
                category = "Peace & Comfort",
                duration = 290.0,
                vocalURL = "https://cdn.tryshema.com/audio/hymns/045_vocal.mp3",
                instrumentalURL = "https://cdn.tryshema.com/audio/hymns/045_instrumental.mp3",
                congregationURL = "https://cdn.tryshema.com/audio/hymns/045_congregation.mp3",
                lyrics = listOf(
                    LyricLine("l31", 0.0, "When peace like a river attendeth my way,", 1),
                    LyricLine("l32", 8.0, "When sorrows like sea billows roll;", 1),
                    LyricLine("l33", 17.0, "Whatever my lot, Thou hast taught me to say,", 1),
                    LyricLine("l34", 25.0, "It is well, it is well with my soul.", 1)
                )
            ),
            Hymn(
                id = "hymn_4",
                number = 88,
                title = "Great Is Thy Faithfulness",
                author = "Thomas Chisholm",
                category = "Faith & Trust",
                duration = 238.0,
                vocalURL = "https://cdn.tryshema.com/audio/hymns/088_vocal.mp3",
                instrumentalURL = "https://cdn.tryshema.com/audio/hymns/088_instrumental.mp3",
                congregationURL = "https://cdn.tryshema.com/audio/hymns/088_congregation.mp3"
            )
        )
    )

    override fun getHymns(isHymn: Boolean, category: String?): Flow<List<Hymn>> {
        return hymnsList.asStateFlow().map { list ->
            list.filter { item ->
                item.isHymn == isHymn &&
                        (category == null || category == "All" || item.category == category)
            }
        }
    }

    override fun getCategories(): List<String> {
        return listOf("All", "Praise & Adoration", "Grace & Mercy", "Peace & Comfort", "Faith & Trust")
    }

    override suspend fun getDailyFeaturedHymn(): Hymn? {
        return hymnsList.value.firstOrNull { it.isDailyFeatured } ?: hymnsList.value.firstOrNull()
    }

    override suspend fun getHymnById(id: String): Hymn? {
        return hymnsList.value.firstOrNull { it.id == id }
    }
}
