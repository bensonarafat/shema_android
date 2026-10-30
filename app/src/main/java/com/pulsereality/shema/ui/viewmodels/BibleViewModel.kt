package com.pulsereality.shema.ui.viewmodels

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pulsereality.shema.data.model.Book
import com.pulsereality.shema.data.model.Verse
import com.pulsereality.shema.data.repository.BibleRepository
import com.pulsereality.shema.data.repository.DefaultBibleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BibleViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val bibleRepository: BibleRepository = DefaultBibleRepository(application)

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books.asStateFlow()

    private val _selectedBook = MutableStateFlow(Book(1, "Genesis", 50))
    val selectedBook: StateFlow<Book> = _selectedBook.asStateFlow()

    private val _selectedChapter = MutableStateFlow(1)
    val selectedChapter: StateFlow<Int> = _selectedChapter.asStateFlow()

    private val _selectedTranslation = MutableStateFlow("ESV")
    val selectedTranslation: StateFlow<String> = _selectedTranslation.asStateFlow()

    private val _verses = MutableStateFlow<List<Verse>>(emptyList())
    val verses: StateFlow<List<Verse>> = _verses.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Highlighted verses mapping: verse pk -> highlight Color
    private val _highlights = MutableStateFlow<Map<Int, Color>>(emptyMap())
    val highlights: StateFlow<Map<Int, Color>> = _highlights.asStateFlow()

    // Selected verse pks for action palette
    private val _selectedVerses = MutableStateFlow<Set<Int>>(emptySet())
    val selectedVerses: StateFlow<Set<Int>> = _selectedVerses.asStateFlow()

    init {
        viewModelScope.launch {
            val loadedBooks = bibleRepository.getBooks()
            if (loadedBooks.isNotEmpty()) {
                _books.value = loadedBooks
                _selectedBook.value = loadedBooks.first()
            }
            loadChapter(_selectedBook.value, _selectedChapter.value)
        }
    }

    fun loadChapter(book: Book, chapter: Int) {
        _selectedBook.value = book
        _selectedChapter.value = chapter.coerceIn(1, book.chapters)
        _selectedVerses.value = emptySet()

        viewModelScope.launch {
            _isLoading.value = true
            val result = bibleRepository.getChapter(
                translation = _selectedTranslation.value,
                bookId = book.bookid,
                chapter = _selectedChapter.value
            )
            result.onSuccess { fetchedVerses ->
                _verses.value = fetchedVerses
            }.onFailure {
                // Keep current or fallback
            }
            _isLoading.value = false
        }
    }

    fun nextChapter() {
        val currentBook = _selectedBook.value
        val currentChap = _selectedChapter.value
        if (currentChap < currentBook.chapters) {
            loadChapter(currentBook, currentChap + 1)
        } else {
            // Jump to next book chapter 1 if available
            val allBooks = _books.value
            val nextIndex = allBooks.indexOfFirst { it.bookid == currentBook.bookid } + 1
            if (nextIndex in allBooks.indices) {
                loadChapter(allBooks[nextIndex], 1)
            }
        }
    }

    fun previousChapter() {
        val currentBook = _selectedBook.value
        val currentChap = _selectedChapter.value
        if (currentChap > 1) {
            loadChapter(currentBook, currentChap - 1)
        } else {
            // Jump to previous book last chapter if available
            val allBooks = _books.value
            val prevIndex = allBooks.indexOfFirst { it.bookid == currentBook.bookid } - 1
            if (prevIndex in allBooks.indices) {
                val prevBook = allBooks[prevIndex]
                loadChapter(prevBook, prevBook.chapters)
            }
        }
    }

    fun setTranslation(translation: String) {
        if (_selectedTranslation.value == translation) return
        _selectedTranslation.value = translation
        loadChapter(_selectedBook.value, _selectedChapter.value)
    }

    fun toggleVerseSelection(versePk: Int) {
        val current = _selectedVerses.value
        _selectedVerses.value = if (current.contains(versePk)) {
            current - versePk
        } else {
            current + versePk
        }
    }

    fun highlightSelected(color: Color) {
        val currentMap = _highlights.value.toMutableMap()
        _selectedVerses.value.forEach { pk ->
            currentMap[pk] = color
        }
        _highlights.value = currentMap
        _selectedVerses.value = emptySet()
    }

    fun removeHighlightFromSelected() {
        val currentMap = _highlights.value.toMutableMap()
        _selectedVerses.value.forEach { pk ->
            currentMap.remove(pk)
        }
        _highlights.value = currentMap
        _selectedVerses.value = emptySet()
    }

    fun clearSelection() {
        _selectedVerses.value = emptySet()
    }
}
