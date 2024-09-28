package com.example.digitalrobot.presentation.robot

sealed class RobotStage {

    data object Start: RobotStage()

    data object InProgress: RobotStage()

    data object ManualSTT: RobotStage()

    data object AutoSTT: RobotStage()

}