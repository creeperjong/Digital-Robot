package com.example.digitalrobot.presentation.robot

sealed class RobotInputMode {

    data object Start: RobotInputMode()

    data object ManualSTT: RobotInputMode()

    data object AutoSTT: RobotInputMode()

    data class ScanOrTouch(var scanObject: Boolean, var touchTablet: Boolean): RobotInputMode()

    data class TouchSensor(val targetBodyParts: List<RobotBodyPart>): RobotInputMode()
}