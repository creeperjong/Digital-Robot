package com.example.digitalrobot.util

import com.example.digitalrobot.BuildConfig
import com.example.digitalrobot.R

object Constants {

    object Robot {

        val EXPRESSION: Map<String, Int> = mapOf(
            "SMILE" to R.raw.e_smile,
            "ENJOYABLE" to R.raw.e_enjoyable,
            "PLAYFUL" to R.raw.e_playful,
            "EXCITED" to R.raw.e_excited,
            "ADORATION" to R.raw.e_adoration,
            "SAD" to R.raw.e_sad,
            "CONFUSED" to R.raw.e_confused,
            "SLOWEST" to R.raw.e_slowest,
            "SLOWER" to R.raw.e_slower,
            "NORMAL" to R.raw.e_normal
        )

        val MOTION: Map<String, Int> = mapOf(
            "APPLAUD" to R.raw.m_applaud,
            "ASK" to R.raw.m_ask,
            "BATHE" to R.raw.m_bathe,
            "BOW" to R.raw.m_bow,
            "BYE" to R.raw.m_bye,
            "CHEER" to R.raw.m_cheer,
            "CURSE" to R.raw.m_curse,
            "DICTATEL" to R.raw.m_dictate_l,
            "DICTATER" to R.raw.m_dictate_r,
            "DRAWCIRCLE" to R.raw.m_draw_circle,
            "EMBRACE" to R.raw.m_embrace,
            "HAPPY01" to R.raw.m_happy01,
            "HELLO" to R.raw.m_hello,
            "HITKEYBOARD" to R.raw.m_hit_keyboard,
            "HORIZONTALBAR" to R.raw.m_horizontal_bar,
            "IDLE" to R.raw.m_idle,
            "INTIMIDATEB" to R.raw.m_intimidate_b,
            "KNOCKDOOR" to R.raw.m_knock_door,
            "LISTEN" to R.raw.m_listen,
            "LISTENSONG" to R.raw.m_listen_song,
            "LOOKD038" to R.raw.m_look_d038,
            "LOOKFOR" to R.raw.m_look_for,
            "MARIO" to R.raw.m_mario,
            "NODHEAD" to R.raw.m_nod_head,
            "PUSHFAST" to R.raw.m_push_fast,
            "ROTATEA" to R.raw.m_rotate_a,
            "ROTATEC" to R.raw.m_rotate_c,
            "RZARML90" to R.raw.m_rz_arm_l90,
            "RZARMR90" to R.raw.m_rz_arm_r90,
            "SAD03" to R.raw.m_sad03,
            "SICK" to R.raw.m_sick,
            "SNEAK" to R.raw.m_sneak,
            "TALKS" to R.raw.m_talk_s,
            "TALKY" to R.raw.m_talk_y,
            "THINK" to R.raw.m_think,
            "TIDY" to R.raw.m_tidy,
            "WEAR" to R.raw.m_wear,
            "YOL" to R.raw.m_yo_l,
            "YOR" to R.raw.m_yo_r
        )
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

            const val TABLET_QR = "$BROKER_NAME/mqtt/tabletqr/{{deviceId}}"

            const val NFC_TAG = "{{deviceId}}/wifiNFCReader/tag_scanned"

            const val TABLET = "{{deviceId}}-tablet"

            const val ROBOT = "{{deviceId}}"

        }

    }

    object LanguageModel {

        const val BASE_URL = "https://api.openai.com/v1/"

        val PROJECTS = mapOf(
            "AI-based Research" to BuildConfig.AI_BASED_RESEARCH,
            "Chinese Language" to BuildConfig.CHINESE_LANGUAGE,
            "Creativity with Chinese Language" to BuildConfig.CREATIVITY_WITH_CHINESE_LANGUAGE,
            "Kindergarten" to BuildConfig.KINDERGARTEN,
            "Making prompts invisible" to BuildConfig.MAKING_PROMPTS_INVISIBLE,
            "Order people care" to BuildConfig.ORDER_PEOPLE_CARE,
            "Robot Storyteller" to BuildConfig.ROBOT_STORYTELLER,
            "Social emotional learning" to BuildConfig.SOCIAL_EMOTIONAL_LEARNING,
            "STEM Education" to BuildConfig.STEM_EDUCATION,
            "Testing Agents" to BuildConfig.TESTING_AGENTS
        )
    }

    object Rcsl {

        const val BASE_URL = "http://api.rcsl.online:8887/"

    }

}