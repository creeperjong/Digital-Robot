package com.example.digitalrobot.presentation.startup

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class StartUpViewModel @Inject constructor(): ViewModel() {

    private val _macAddress = MutableStateFlow("")
    val macAddress: StateFlow<String> = _macAddress.asStateFlow()

    fun onEvent(event: StartUpEvent) {
        when(event) {
            is StartUpEvent.ChangeMacAddress -> {
                _macAddress.value = event.result
            }
        }
    }

}