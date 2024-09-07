package com.example.digitalrobot.presentation.robot

sealed class RobotEvent {

    object ConnectMqttBroker: RobotEvent()

    object DisconnectMqttBroker: RobotEvent()

}