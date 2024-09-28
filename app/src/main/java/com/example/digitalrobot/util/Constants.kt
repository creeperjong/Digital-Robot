package com.example.digitalrobot.util

object Constants {

    object Mqtt {

        const val BROKER_URL = "tcp://mqtt1.rcsl.online:1883"

        const val BROKER_NAME = "NTNU-MQTT-Server-1"

        const val USERNAME = "rcsl"

        const val PASSWORD = "rcslmqtt"

        object Topic {

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