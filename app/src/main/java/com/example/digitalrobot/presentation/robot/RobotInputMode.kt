package com.example.digitalrobot.presentation.robot

sealed class RobotInputMode {

    data object Start: RobotInputMode()

    data object ManualSTT: RobotInputMode()

    data object AutoSTT: RobotInputMode()

    data object ScanObject: RobotInputMode()

    data class TouchSensor(val targetBodyParts: List<RobotBodyPart>): RobotInputMode()
}