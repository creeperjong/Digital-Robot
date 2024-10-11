package com.example.digitalrobot.presentation.startup

import android.content.Context
import android.content.SharedPreferences
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

    private lateinit var sharedPreferences: SharedPreferences

    fun onEvent(event: StartUpEvent) {
        when(event) {
            is StartUpEvent.ChangeMacAddress -> {
                _macAddress.value = event.result
                saveMacAddress(event.result)
            }
            is StartUpEvent.InitSharedPreferences -> {
                initSharedPreferences(event.context)
            }
        }
    }

    private fun initSharedPreferences(context: Context) {
        sharedPreferences = context.getSharedPreferences("macAddress", Context.MODE_PRIVATE)
        loadMacAddress()
    }

    private fun loadMacAddress() {
        _macAddress.value = sharedPreferences.getString("macAddress", "") ?: ""
    }

    private fun saveMacAddress(text: String) {
        sharedPreferences.edit().putString("macAddress", text).apply()
    }

}