package com.pulsereality.shema.media

import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.pulsereality.shema.MainActivity
import com.pulsereality.shema.data.model.Hymn
import com.pulsereality.shema.data.model.HymnStem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Media3 Background Audio Service for Shema Worship Sanctuary.
 * Provides:
 * 1. Background ExoPlayer instance with audio focus handling.
 * 2. MediaSession integration for Lock Screen & Notification media controls (play/pause, seek, skip).
 * 3. Continuous-timestamp 3-stem switching (Vocal, Instrumental, Congregation) matching iOS AudioPlayerManager.
 */
class ShemaAudioService : MediaSessionService() {

    private var player: ExoPlayer? = null
    private var mediaSession: MediaSession? = null

    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var progressTickerJob: Job? = null

    companion object {
        // Shared observable playback state for ViewModels
        private val _currentHymn = MutableStateFlow<Hymn?>(null)
        val currentHymn: StateFlow<Hymn?> = _currentHymn.asStateFlow()

        private val _currentStem = MutableStateFlow(HymnStem.VOCAL)
        val currentStem: StateFlow<HymnStem> = _currentStem.asStateFlow()

        private val _isPlaying = MutableStateFlow(false)
        val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

        private val _currentPositionSec = MutableStateFlow(0.0)
        val currentPositionSec: StateFlow<Double> = _currentPositionSec.asStateFlow()

        private val _durationSec = MutableStateFlow(222.0)
        val durationSec: StateFlow<Double> = _durationSec.asStateFlow()

        // Active singleton instance reference when running
        var activeInstance: ShemaAudioService? = null
            private set
    }

    override fun onCreate() {
        super.onCreate()
        activeInstance = this

        val audioAttributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

        val exoPlayer = ExoPlayer.Builder(this)
            .setAudioAttributes(audioAttributes, /* handleAudioFocus = */ true)
            .setHandleAudioBecomingNoisy(true)
            .build()

        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                _isPlaying.value = playing
                if (playing) {
                    startProgressTicker()
                } else {
                    stopProgressTicker()
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    val durMs = exoPlayer.duration
                    if (durMs > 0) {
                        _durationSec.value = durMs / 1000.0
                    }
                } else if (playbackState == Player.STATE_ENDED) {
                    _isPlaying.value = false
                    stopProgressTicker()
                }
            }
        })

        this.player = exoPlayer

        val sessionActivityIntent = Intent(this, MainActivity::class.java)
        val sessionActivityPendingIntent = PendingIntent.getActivity(
            this,
            0,
            sessionActivityIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        mediaSession = MediaSession.Builder(this, exoPlayer)
            .setSessionActivity(sessionActivityPendingIntent)
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    private fun startProgressTicker() {
        stopProgressTicker()
        progressTickerJob = serviceScope.launch {
            while (isActive) {
                player?.let { p ->
                    _currentPositionSec.value = p.currentPosition / 1000.0
                }
                delay(200)
            }
        }
    }

    private fun stopProgressTicker() {
        progressTickerJob?.cancel()
        progressTickerJob = null
    }

    /**
     * Plays a hymn with the specified stem and starting position.
     */
    fun playHymn(hymn: Hymn, stem: HymnStem = HymnStem.VOCAL, startPositionSec: Double = 0.0) {
        val p = player ?: return
        _currentHymn.value = hymn
        _currentStem.value = stem
        _durationSec.value = if (hymn.duration > 0) hymn.duration else 222.0

        val stemUrl = hymn.urlForStem(stem)
        val mediaMetadata = MediaMetadata.Builder()
            .setTitle(hymn.title)
            .setArtist(hymn.author ?: "Traditional")
            .setAlbumTitle(hymn.category)
            .apply {
                hymn.coverImageUrl?.let { setArtworkUri(Uri.parse(it)) }
            }
            .build()

        val mediaItem = if (!stemUrl.isNullOrBlank()) {
            MediaItem.Builder()
                .setMediaId(hymn.id)
                .setUri(stemUrl)
                .setMediaMetadata(mediaMetadata)
                .build()
        } else {
            // Demo fallback stream if remote URL is blank
            MediaItem.Builder()
                .setMediaId(hymn.id)
                .setUri("https://commondatastorage.googleapis.com/codeskulptor-demos/DDR_assets/Kangaroo_MusiQue_-_The_Neverwritten_Role_Playing_Game.mp3")
                .setMediaMetadata(mediaMetadata)
                .build()
        }

        p.setMediaItem(mediaItem)
        p.prepare()
        if (startPositionSec > 0) {
            p.seekTo((startPositionSec * 1000).toLong())
        }
        p.play()
    }

    /**
     * Seamlessly switches stem (Vocal, Instrumental, Congregation)
     * preserving the current playback position in milliseconds.
     */
    fun switchStem(newStem: HymnStem) {
        val hymn = _currentHymn.value ?: return
        if (_currentStem.value == newStem) return
        val p = player ?: return

        val currentPosMs = p.currentPosition
        val wasPlaying = p.isPlaying

        _currentStem.value = newStem
        val newUrl = hymn.urlForStem(newStem)

        val mediaMetadata = MediaMetadata.Builder()
            .setTitle(hymn.title)
            .setArtist(hymn.author ?: "Traditional")
            .setAlbumTitle("${hymn.category} • ${newStem.rawValue}")
            .build()

        val mediaItem = if (!newUrl.isNullOrBlank()) {
            MediaItem.Builder()
                .setMediaId("${hymn.id}_${newStem.rawValue}")
                .setUri(newUrl)
                .setMediaMetadata(mediaMetadata)
                .build()
        } else {
            MediaItem.Builder()
                .setMediaId("${hymn.id}_${newStem.rawValue}")
                .setUri("https://commondatastorage.googleapis.com/codeskulptor-demos/DDR_assets/Kangaroo_MusiQue_-_The_Neverwritten_Role_Playing_Game.mp3")
                .setMediaMetadata(mediaMetadata)
                .build()
        }

        p.setMediaItem(mediaItem)
        p.prepare()
        p.seekTo(currentPosMs)
        if (wasPlaying) {
            p.play()
        }
    }

    fun togglePlayPause() {
        val p = player ?: return
        if (p.isPlaying) {
            p.pause()
        } else {
            p.play()
        }
    }

    fun seekTo(seconds: Double) {
        val p = player ?: return
        val posMs = (seconds * 1000).toLong()
        p.seekTo(posMs)
        _currentPositionSec.value = seconds
    }

    fun setPlaybackSpeed(speed: Float) {
        val p = player ?: return
        p.playbackParameters = PlaybackParameters(speed)
    }

    override fun onDestroy() {
        activeInstance = null
        stopProgressTicker()
        mediaSession?.run {
            player?.release()
            release()
            mediaSession = null
        }
        player = null
        super.onDestroy()
    }
}
