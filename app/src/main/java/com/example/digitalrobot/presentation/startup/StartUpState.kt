package com.example.digitalrobot.presentation.startup

import android.os.Parcelable
import com.example.digitalrobot.BuildConfig
import com.example.digitalrobot.util.Constants
import kotlinx.parcelize.Parcelize

@Parcelize
data class StartUpState(
    val robotName: String = "",
    val macAddress: String = "",
    val robotOptions: Map<String, String> = emptyMap()
): Parcelable
