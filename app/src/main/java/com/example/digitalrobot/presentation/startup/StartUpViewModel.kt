package com.example.digitalrobot.presentation.startup

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.digitalrobot.domain.usecase.LanguageModelUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartUpViewModel @Inject constructor(
    private val languageModelUseCase: LanguageModelUseCase
): ViewModel() {

    private val _state = MutableStateFlow(StartUpState())
    val state: StateFlow<StartUpState> = _state.asStateFlow()

    private lateinit var sharedPreferences: SharedPreferences

    fun onEvent(event: StartUpEvent) {
        when(event) {
            is StartUpEvent.SetMacAddress -> {
                setMacAddress(event.result)
            }
            is StartUpEvent.InitSharedPreferences -> {
                initSharedPreferences(event.context)
            }
            is StartUpEvent.SetProject -> {
                setProject(event.projectName)
            }
            is StartUpEvent.SetAssistant -> {
                setAssistant(event.assistantName)
            }
            is StartUpEvent.SetAssistantList -> {
                setAssistantList(
                    gptApiKey = event.gptApiKey,
                    firstAsDefault = false
                )
            }
        }
    }

    private fun setMacAddress(macAddress: String) {
        _state.value = _state.value.copy(macAddress = macAddress)
        saveMacAddress(macAddress)
    }

    private fun setProject(projectName: String) {
        val gptApiKey = _state.value.projectOptions[projectName] ?: ""
        _state.value = _state.value.copy(
            projectName = projectName,
            gptApiKey = gptApiKey
        )
        setAssistantList(gptApiKey)
    }

    private fun setAssistantList(gptApiKey: String, firstAsDefault: Boolean = true) {
        viewModelScope.launch {
            val assistants = languageModelUseCase.getAssistantList(gptApiKey = gptApiKey)
            val updatedState = _state.value.copy(
                assistantOptions = assistants.associate { (it.name ?: "Unknown assistant") to it.id }
            )
            _state.value = if (firstAsDefault) {
                val firstAssistant = assistants.firstOrNull()
                updatedState.copy(
                    assistantName = firstAssistant?.name ?: "",
                    assistantId = firstAssistant?.id ?: ""
                )
            } else {
                updatedState
            }
        }
    }

    private fun setAssistant(assistantName: String) {
        val assistantId = _state.value.assistantOptions[assistantName] ?: ""
        _state.value = _state.value.copy(
            assistantName = assistantName,
            assistantId = assistantId
        )
    }

    private fun initSharedPreferences(context: Context) {
        sharedPreferences = context.getSharedPreferences("macAddress", Context.MODE_PRIVATE)
        loadMacAddress()
    }

    private fun loadMacAddress() {
        _state.value = _state.value.copy(
            macAddress = sharedPreferences.getString("macAddress", "") ?: ""
        )
    }

    private fun saveMacAddress(text: String) {
        sharedPreferences.edit().putString("macAddress", text).apply()
    }

}