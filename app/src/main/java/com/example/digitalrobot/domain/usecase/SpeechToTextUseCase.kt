package com.example.digitalrobot.domain.usecase

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

class SpeechToTextUseCase (
    private val speechRecognizer: SpeechRecognizer
) {

    suspend fun startListening(
        language: Locale = Locale.US,
        keepListening: Boolean = true,
        onSTTDone: (String) -> Unit
    ) {
        withContext(Dispatchers.Main) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, language.toString())
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language.toString())
                putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, language.toString())
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            }

            speechRecognizer.setRecognitionListener(object: RecognitionListener {
                override fun onReadyForSpeech(p0: Bundle?) {}

                override fun onBeginningOfSpeech() {}

                override fun onRmsChanged(p0: Float) {}

                override fun onBufferReceived(p0: ByteArray?) {}

                override fun onEndOfSpeech() {}

                override fun onError(p0: Int) {
                    if (keepListening) {
                        speechRecognizer.startListening(intent)
                    }
                }

                override fun onResults(results: Bundle) {
                    val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        onSTTDone(matches[0])
                    } else if (keepListening) {
                        speechRecognizer.startListening(intent)
                    }
                }

                override fun onPartialResults(p0: Bundle?) {}

                override fun onEvent(p0: Int, p1: Bundle?) {}

            })
            speechRecognizer.startListening(intent)
        }
    }

    fun stopListening() {
        speechRecognizer.stopListening()
    }


}