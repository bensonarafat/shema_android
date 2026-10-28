package com.pulsereality.shema.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulsereality.shema.data.model.Hymn
import com.pulsereality.shema.data.model.HymnStem
import com.pulsereality.shema.data.repository.FakeHymnRepository
import com.pulsereality.shema.data.repository.HymnRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class RepeatMode { OFF, ALL, ONE }

class WorshipViewModel(
    private val hymnRepository: HymnRepository = FakeHymnRepository()
) : ViewModel() {

    private val _isHymnTab = MutableStateFlow(true)
    val isHymnTab: StateFlow<Boolean> = _isHymnTab.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val categories: List<String> = hymnRepository.getCategories()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val hymns: StateFlow<List<Hymn>> = combine(_isHymnTab, _selectedCategory) { isHymn, category ->
        Pair(isHymn, category)
    }.flatMapLatest { (isHymn, category) ->
        hymnRepository.getHymns(isHymn, category)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Player State
    private val _currentHymn = MutableStateFlow<Hymn?>(null)
    val currentHymn: StateFlow<Hymn?> = _currentHymn.asStateFlow()

    private val _currentStem = MutableStateFlow(HymnStem.VOCAL)
    val currentStem: StateFlow<HymnStem> = _currentStem.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentTime = MutableStateFlow(0.0)
    val currentTime: StateFlow<Double> = _currentTime.asStateFlow()

    private val _duration = MutableStateFlow(222.0)
    val duration: StateFlow<Double> = _duration.asStateFlow()

    private val _activeLyricIndex = MutableStateFlow(0)
    val activeLyricIndex: StateFlow<Int> = _activeLyricIndex.asStateFlow()

    private val _repeatMode = MutableStateFlow(RepeatMode.OFF)
    val repeatMode: StateFlow<RepeatMode> = _repeatMode.asStateFlow()

    private val _playbackSpeed = MutableStateFlow(1.0)
    val playbackSpeed: StateFlow<Double> = _playbackSpeed.asStateFlow()

    init {
        viewModelScope.launch {
            val featured = hymnRepository.getDailyFeaturedHymn()
            if (_currentHymn.value == null) {
                _currentHymn.value = featured
                featured?.let { _duration.value = it.duration }
            }
        }

        // Synchronize with background ShemaAudioService state
        viewModelScope.launch {
            com.pulsereality.shema.media.ShemaAudioService.currentHymn.collect { hymn ->
                if (hymn != null) {
                    _currentHymn.value = hymn
                }
            }
        }

        viewModelScope.launch {
            com.pulsereality.shema.media.ShemaAudioService.currentStem.collect { stem ->
                _currentStem.value = stem
            }
        }

        viewModelScope.launch {
            com.pulsereality.shema.media.ShemaAudioService.isPlaying.collect { playing ->
                _isPlaying.value = playing
            }
        }

        viewModelScope.launch {
            com.pulsereality.shema.media.ShemaAudioService.currentPositionSec.collect { pos ->
                _currentTime.value = pos
                updateActiveLyricIndex()
            }
        }

        viewModelScope.launch {
            com.pulsereality.shema.media.ShemaAudioService.durationSec.collect { dur ->
                if (dur > 0) {
                    _duration.value = dur
                }
            }
        }
    }

    fun selectTab(isHymn: Boolean) {
        _isHymnTab.value = isHymn
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun playHymn(hymn: Hymn) {
        _currentHymn.value = hymn
        _duration.value = if (hymn.duration > 0) hymn.duration else 222.0
        _currentTime.value = 0.0
        _activeLyricIndex.value = 0
        _isPlaying.value = true

        val service = com.pulsereality.shema.media.ShemaAudioService.activeInstance
        if (service != null) {
            service.playHymn(hymn, _currentStem.value, 0.0)
        }
    }

    fun togglePlayPause() {
        val service = com.pulsereality.shema.media.ShemaAudioService.activeInstance
        if (service != null) {
            service.togglePlayPause()
        } else {
            _isPlaying.value = !_isPlaying.value
        }
    }

    /**
     * Seamlessly switches audio stem (Vocal, Instrumental, Congregation)
     * preserving current playback timestamp matching iOS AudioPlayerManager.switchStem.
     */
    fun switchStem(stem: HymnStem) {
        if (_currentStem.value == stem) return
        _currentStem.value = stem

        val service = com.pulsereality.shema.media.ShemaAudioService.activeInstance
        if (service != null) {
            service.switchStem(stem)
        }
    }

    fun seekTo(seconds: Double) {
        _currentTime.value = seconds.coerceIn(0.0, _duration.value)
        updateActiveLyricIndex()

        val service = com.pulsereality.shema.media.ShemaAudioService.activeInstance
        service?.seekTo(seconds)
    }

    fun updateProgress(seconds: Double) {
        _currentTime.value = seconds
        updateActiveLyricIndex()
    }

    private fun updateActiveLyricIndex() {
        val lyrics = _currentHymn.value?.lyrics ?: return
        var matched = 0
        for ((idx, line) in lyrics.withIndex()) {
            if (_currentTime.value >= line.time) {
                matched = idx
            } else {
                break
            }
        }
        _activeLyricIndex.value = matched
    }

    fun cycleRepeatMode() {
        _repeatMode.value = when (_repeatMode.value) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
    }

    fun cyclePlaybackSpeed() {
        val speeds = listOf(0.75, 1.0, 1.25, 1.5)
        val nextIdx = (speeds.indexOf(_playbackSpeed.value) + 1) % speeds.size
        val newSpeed = speeds[nextIdx]
        _playbackSpeed.value = newSpeed

        val service = com.pulsereality.shema.media.ShemaAudioService.activeInstance
        service?.setPlaybackSpeed(newSpeed.toFloat())
    }
}
