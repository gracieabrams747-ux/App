package com.example.util

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.SystemClock
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

object VoiceNoteRecorder {
    private var mediaRecorder: MediaRecorder? = null
    private var currentOutputFile: File? = null
    private var recordStartTime: Long = 0L

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _recordingSeconds = MutableStateFlow(0)
    val recordingSeconds: StateFlow<Int> = _recordingSeconds.asStateFlow()

    private var timerJob: Job? = null

    fun startRecording(context: Context): Boolean {
        return try {
            stopRecording() // Clean up any active recorder

            val audioDir = File(context.cacheDir, "marian_voice_notes").apply { mkdirs() }
            val outputFile = File(audioDir, "audio_note_${System.currentTimeMillis()}.m4a")
            currentOutputFile = outputFile

            val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            recorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)
                setOutputFile(outputFile.absolutePath)
                prepare()
                start()
            }

            mediaRecorder = recorder
            recordStartTime = SystemClock.elapsedRealtime()
            _isRecording.value = true
            _recordingSeconds.value = 0

            timerJob = CoroutineScope(Dispatchers.Main).launch {
                while (isActive && _isRecording.value) {
                    delay(1000)
                    val elapsed = ((SystemClock.elapsedRealtime() - recordStartTime) / 1000).toInt()
                    _recordingSeconds.value = elapsed
                }
            }
            true
        } catch (e: Exception) {
            Log.e("VoiceNoteRecorder", "Error starting recording: ${e.message}")
            stopRecording()
            false
        }
    }

    data class RecordingResult(
        val filePath: String,
        val durationSeconds: Int
    )

    fun stopAndGetResult(): RecordingResult? {
        val duration = _recordingSeconds.value
        val file = currentOutputFile
        stopRecording()

        return if (file != null && file.exists() && file.length() > 0) {
            RecordingResult(
                filePath = file.absolutePath,
                durationSeconds = maxOf(1, duration)
            )
        } else {
            null
        }
    }

    fun stopRecording() {
        timerJob?.cancel()
        timerJob = null
        try {
            mediaRecorder?.apply {
                stop()
                reset()
                release()
            }
        } catch (_: Exception) {}
        mediaRecorder = null
        _isRecording.value = false
        _recordingSeconds.value = 0
    }
}

object VoiceNotePlayer {
    private var mediaPlayer: MediaPlayer? = null
    private var progressJob: Job? = null

    private val _playingAudioId = MutableStateFlow<String?>(null)
    val playingAudioId: StateFlow<String?> = _playingAudioId.asStateFlow()

    private val _playbackProgress = MutableStateFlow(0f)
    val playbackProgress: StateFlow<Float> = _playbackProgress.asStateFlow()

    fun play(audioId: String, filePath: String?, durationSeconds: Int = 5, onSimulatedText: (() -> Unit)? = null) {
        if (_playingAudioId.value == audioId) {
            stop()
            return
        }

        stop()

        val file = filePath?.let { File(it) }
        if (file != null && file.exists()) {
            try {
                val player = MediaPlayer().apply {
                    setDataSource(file.absolutePath)
                    prepare()
                    start()
                    setOnCompletionListener {
                        stop()
                    }
                }
                mediaPlayer = player
                _playingAudioId.value = audioId

                progressJob = CoroutineScope(Dispatchers.Main).launch {
                    val totalDuration = player.duration.toFloat().coerceAtLeast(1000f)
                    while (isActive && player.isPlaying) {
                        _playbackProgress.value = (player.currentPosition / totalDuration).coerceIn(0f, 1f)
                        delay(100)
                    }
                }
            } catch (e: Exception) {
                Log.e("VoiceNotePlayer", "Failed to play audio file: ${e.message}")
                fallbackSimulatedPlayback(audioId, durationSeconds, onSimulatedText)
            }
        } else {
            // Simulated voice note playback with Mary's or speaker's gentle audio
            fallbackSimulatedPlayback(audioId, durationSeconds, onSimulatedText)
        }
    }

    private fun fallbackSimulatedPlayback(audioId: String, durationSeconds: Int, onSimulatedText: (() -> Unit)?) {
        _playingAudioId.value = audioId
        onSimulatedText?.invoke()

        progressJob = CoroutineScope(Dispatchers.Main).launch {
            val totalSteps = durationSeconds * 10
            for (i in 0..totalSteps) {
                if (_playingAudioId.value != audioId) break
                _playbackProgress.value = (i.toFloat() / totalSteps).coerceIn(0f, 1f)
                delay(100)
            }
            if (_playingAudioId.value == audioId) {
                stop()
            }
        }
    }

    fun stop() {
        progressJob?.cancel()
        progressJob = null
        try {
            mediaPlayer?.apply {
                if (isPlaying) stop()
                reset()
                release()
            }
        } catch (_: Exception) {}
        mediaPlayer = null
        _playingAudioId.value = null
        _playbackProgress.value = 0f
    }
}
