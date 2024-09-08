package com.example.digitalrobot.domain.usecase

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale

class TextToSpeechUseCase() {
    private var tts: TextToSpeech? = null

    fun init(context: Context, language: Locale, onTTSComplete: () -> Unit) {
        if (tts == null) {
            tts = TextToSpeech(context) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    tts?.language = language
                    tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {}

                        override fun onDone(utteranceId: String?) {
                            onTTSComplete()
                        }

                        @Deprecated("Deprecated in Java")
                        override fun onError(utteranceId: String?) {}
                    })
                }
            }
        }
    }

    fun speak(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, text)
    }

    fun stop() {
        tts?.stop()
    }

    fun setLanguage(locale: Locale) {
        tts?.setLanguage(locale)
    }

    fun cleanUp() {
        tts?.stop()
        tts?.shutdown()
    }
}