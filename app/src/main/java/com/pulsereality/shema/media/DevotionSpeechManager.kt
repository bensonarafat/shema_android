package com.pulsereality.shema.media

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class DevotionSpeechManager(context: Context) {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _activeRange = MutableStateFlow<IntRange?>(null)
    val activeRange: StateFlow<IntRange?> = _activeRange.asStateFlow()

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

    private var currentText: String = ""

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                isInitialized = true
                setupProgressListener()
            }
        }
    }

    private fun setupProgressListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _isPlaying.value = true
            }

            override fun onDone(utteranceId: String?) {
                _isPlaying.value = false
                _activeRange.value = null
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                _isPlaying.value = false
                _activeRange.value = null
            }

            override fun onRangeStart(utteranceId: String?, start: Int, end: Int, frame: Int) {
                // Real-time word-by-word boundary tracking matching iOS AVSpeechSynthesizerDelegate
                _activeRange.value = IntRange(start, end)
            }
        })
    }

    fun speak(text: String) {
        if (!isInitialized || text.isBlank()) return
        currentText = text
        stop()
        tts?.setSpeechRate(_playbackSpeed.value)
        val params = android.os.Bundle()
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "devotion_utterance")
        _isPlaying.value = true
    }

    fun pauseOrStop() {
        if (_isPlaying.value) {
            tts?.stop()
            _isPlaying.value = false
            _activeRange.value = null
        }
    }

    fun togglePlayPause(text: String) {
        if (_isPlaying.value) {
            pauseOrStop()
        } else {
            speak(text)
        }
    }

    fun cycleSpeed(): Float {
        val speeds = listOf(1.0f, 1.25f, 1.5f, 2.0f)
        val nextIndex = (speeds.indexOf(_playbackSpeed.value) + 1) % speeds.size
        val newSpeed = speeds[nextIndex]
        _playbackSpeed.value = newSpeed
        tts?.setSpeechRate(newSpeed)
        if (_isPlaying.value && currentText.isNotBlank()) {
            speak(currentText)
        }
        return newSpeed
    }

    fun stop() {
        tts?.stop()
        _isPlaying.value = false
        _activeRange.value = null
    }

    fun shutdown() {
        stop()
        tts?.shutdown()
        tts = null
    }
}
