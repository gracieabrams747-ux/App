package com.example.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

object MaryVoiceManager {
    private const val TAG = "MaryVoiceManager"
    private var tts: TextToSpeech? = null

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

    private var currentSpeakingId: String? = null

    fun initialize(context: Context) {
        if (tts != null) return

        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val localeSpanish = Locale("es", "ES")
                val result = tts?.setLanguage(localeSpanish)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts?.setLanguage(Locale.getDefault())
                }
                // Gentle, maternal, calming tone and cadence
                tts?.setPitch(1.04f)
                tts?.setSpeechRate(0.86f)

                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        currentSpeakingId = utteranceId
                        _isSpeaking.value = true
                    }

                    override fun onDone(utteranceId: String?) {
                        if (currentSpeakingId == utteranceId) {
                            currentSpeakingId = null
                            _isSpeaking.value = false
                        }
                    }

                    override fun onError(utteranceId: String?) {
                        if (currentSpeakingId == utteranceId) {
                            currentSpeakingId = null
                            _isSpeaking.value = false
                        }
                    }
                })
                _isInitialized.value = true
                Log.d(TAG, "Mary TTS initialized successfully")
            } else {
                Log.e(TAG, "Failed to initialize Mary TTS: status $status")
            }
        }
    }

    fun speak(text: String, utteranceId: String = "MARY_${System.currentTimeMillis()}") {
        val cleanText = text.replace("*", "").replace("#", "").trim()
        if (cleanText.isEmpty()) return

        stop()
        currentSpeakingId = utteranceId
        _isSpeaking.value = true
        tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    fun stop() {
        tts?.stop()
        currentSpeakingId = null
        _isSpeaking.value = false
    }

    fun shutdown() {
        stop()
        tts?.shutdown()
        tts = null
        _isInitialized.value = false
    }
}
