package com.example.digitalrobot.util

import androidx.annotation.RawRes
import com.example.digitalrobot.R

object Constants {

    object Robot {

        val EXPRESSION: Map<String, Int> = mapOf(
            "SMILE" to R.raw.smile,
            "ENJOYABLE" to R.raw.enjoyable,
            "PLAYFUL" to R.raw.playful,
            "EXCITED" to R.raw.excited,
            "ADORATION" to R.raw.adoration,
            "SAD" to R.raw.sad,
            "CONFUSED" to R.raw.confused,
            "SLOWEST" to R.raw.slowest,
            "SLOWER" to R.raw.slower,
            "NORMAL" to R.raw.normal
        )

        // TODO: Action map
    }

    object Mqtt {

        const val BROKER_URL = "tcp://mqtt1.rcsl.online:1883"

        const val BROKER_NAME = "NTNU-MQTT-Server-1"

        const val USERNAME = "rcsl"

        const val PASSWORD = "rcslmqtt"

        object Topic {

            const val TTS = "$BROKER_NAME/mqtt/TTS/{{deviceId}}"

            const val STT = "$BROKER_NAME/mqtt/STT/{{deviceId}}"

            const val IMAGE = "$BROKER_NAME/mqtt/image/{{deviceId}}"

            const val ARGV = "$BROKER_NAME/mqtt/argv/{{deviceId}}"

            const val TEXT_INPUT = "$BROKER_NAME/mqtt/TextInput/{{deviceId}}"

            const val RESPONSE = "$BROKER_NAME/mqtt/response/{{deviceId}}"

            const val GET_CATEGORY = "{{deviceId}}/getRobotCategory/response"

            const val API_KEY = "$BROKER_NAME/mqtt/Apikey/{{deviceId}}"

            const val ASST_ID = "$BROKER_NAME/mqtt/AsstId/{{deviceId}}"

            const val SEND_IMAGE = "$BROKER_NAME/mqtt/SendImage/{{deviceId}}"

            const val SEND_FILE = "$BROKER_NAME/mqtt/SendFile/{{deviceId}}"

            const val NFC_TAG = "{{deviceId}}/wifiNFCReader/tag_scanned"

            const val TABLET = "{{deviceId}}-tablet"

            const val ROBOT = "{{deviceId}}"

        }

    }

    object LanguageModel {

        const val BASE_URL = "https://api.openai.com/v1/"

    }

}