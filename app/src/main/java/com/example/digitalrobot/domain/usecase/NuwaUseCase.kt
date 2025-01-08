package com.example.digitalrobot.domain.usecase

import android.content.Context
import android.util.Log
import com.example.digitalrobot.BuildConfig
import com.example.digitalrobot.presentation.robot.RobotBodyPart
import com.nuwarobotics.service.IClientId
import com.nuwarobotics.service.agent.NuwaRobotAPI
import com.nuwarobotics.service.agent.RobotEventCallback

class NuwaUseCase {

    private lateinit var clientId: IClientId
    private lateinit var robotApi: NuwaRobotAPI

    fun init(context: Context, onTap: (RobotBodyPart) -> Unit, onInit: (Boolean) -> Unit) {
        clientId = IClientId(context.packageName)
        robotApi = NuwaRobotAPI(context, clientId)

        robotApi.registerRobotEventListener(object: RobotEventCallback() {
            override fun onWikiServiceStart() {
                robotApi.requestSensor(NuwaRobotAPI.SENSOR_TOUCH)
                onInit(true)
            }

            override fun onTouchEvent(type: Int, touch: Int) {
                val bodyPart = RobotBodyPart.fromCode(type)
                if (touch == 1 && bodyPart != null) {
                    onTap(bodyPart)
                }
            }
        })
    }

    fun getServiceReadyStatus(): Boolean {
        return robotApi.isKiWiServiceReady
    }

    fun playMotion(motion: String) {
        if(robotApi.isKiWiServiceReady) {
            robotApi.motionPlay(motion, true)
        }

    }

    fun stopMotion() {
        if(robotApi.isKiWiServiceReady) {
            robotApi.motionStop(true)
        }
    }


    fun release() {
        robotApi.release()
    }


}