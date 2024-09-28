package com.example.digitalrobot.presentation.robot

enum class RobotBodyPart(val code: Int){
    HEAD(1),
    CHEST(2),
    RIGHT_HAND(3),
    LEFT_HEAD(4),
    LEFT_FACE(5),
    RIGHT_FACE(6);

    companion object {
        fun fromCode(code: Int?): RobotBodyPart? {
            return entries.find { it.code == code}
        }
    }
}

