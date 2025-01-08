package com.example.digitalrobot.domain.usecase

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

class SpeechToTextUseCase (
    private val speechRecognizer: SpeechRecognizer
) {
    private var manualStop: Boolean = false
    private var isListening: Boolean = false

    suspend fun startListening(
        keepListening: Boolean,
        language: Locale = Locale.US,
        onSTTPartialResult: (String) -> Unit,
        onSTTDone: (String) -> Unit,
    ) {
        withContext(Dispatchers.Main) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, language.toLanguageTag())
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language.toString())
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, keepListening)
            }

            speechRecognizer.setRecognitionListener(object: RecognitionListener {
                override fun onReadyForSpeech(p0: Bundle?) {}

                override fun onBeginningOfSpeech() {}

                override fun onRmsChanged(p0: Float) {}

                override fun onBufferReceived(p0: ByteArray?) {}

                override fun onEndOfSpeech() {}

                override fun onError(errorCode: Int) {
                    if (manualStop) {
                        manualStop = false
                        isListening = false
                    } else {
                        speechRecognizer.startListening(intent)
                    }
                }

                override fun onResults(results: Bundle) {
                    val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        if (keepListening) {
                            onSTTPartialResult(matches[0])
                            speechRecognizer.startListening(intent)
                        } else {
                            onSTTDone(matches[0])
                            isListening = false
                        }
                    } else {
                        speechRecognizer.startListening(intent)
                    }
                }

                override fun onPartialResults(results: Bundle?) {}

                override fun onEvent(p0: Int, p1: Bundle?) {}

            })
            speechRecognizer.startListening(intent)
            isListening = true
        }
    }

    fun stopListening() {
        manualStop = true
        CoroutineScope(Dispatchers.Main).launch {
            if (isListening) speechRecognizer.stopListening()
        }
    }


}