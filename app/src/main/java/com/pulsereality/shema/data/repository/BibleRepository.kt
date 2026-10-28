package com.pulsereality.shema.data.repository

import android.content.Context
import com.pulsereality.shema.data.model.Book
import com.pulsereality.shema.data.model.Verse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

interface BibleRepository {
    suspend fun getBooks(): List<Book>
    suspend fun getChapter(translation: String, bookId: Int, chapter: Int): Result<List<Verse>>
}

class DefaultBibleRepository(
    private val context: Context
) : BibleRepository {

    private var cachedBooks: List<Book>? = null

    override suspend fun getBooks(): List<Book> = withContext(Dispatchers.IO) {
        cachedBooks?.let { return@withContext it }

        try {
            val inputStream = context.assets.open("books.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(jsonString)
            val list = mutableListOf<Book>()

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    Book(
                        bookid = obj.getInt("bookid"),
                        name = obj.getString("name"),
                        chapters = obj.getInt("chapters")
                    )
                )
            }
            cachedBooks = list
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getChapter(
        translation: String,
        bookId: Int,
        chapter: Int
    ): Result<List<Verse>> = withContext(Dispatchers.IO) {
        val safeTranslation = when (translation.uppercase()) {
            "NIV" -> "ESV"
            else -> translation
        }
        val urlString = "https://bolls.life/get-text/$safeTranslation/$bookId/$chapter/"

        try {
            val url = URL(urlString)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 8000
                readTimeout = 8000
                setRequestProperty("Accept", "application/json")
            }

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonArray = JSONArray(response)
                val verses = mutableListOf<Verse>()

                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    verses.add(
                        Verse(
                            pk = obj.optInt("pk", i + 1),
                            translation = safeTranslation,
                            book = bookId,
                            chapter = chapter,
                            verse = obj.optInt("verse", i + 1),
                            text = obj.optString("text", "")
                        )
                    )
                }
                Result.success(verses)
            } else {
                Result.failure(Exception("HTTP error ${connection.responseCode}"))
            }
        } catch (e: Exception) {
            // Return fallback Genesis 1 sample verses if offline
            val fallback = listOf(
                Verse(1, safeTranslation, bookId, chapter, 1, "In the beginning, God created the heavens and the earth."),
                Verse(2, safeTranslation, bookId, chapter, 2, "The earth was without form and void, and darkness was over the face of the deep. And the Spirit of God was hovering over the face of the waters."),
                Verse(3, safeTranslation, bookId, chapter, 3, "And God said, “Let there be light,” and there was light."),
                Verse(4, safeTranslation, bookId, chapter, 4, "And God saw that the light was good. And God separated the light from the darkness."),
                Verse(5, safeTranslation, bookId, chapter, 5, "God called the light Day, and the darkness he called Night. And there was evening and there was morning, the first day.")
            )
            Result.success(fallback)
        }
    }
}
