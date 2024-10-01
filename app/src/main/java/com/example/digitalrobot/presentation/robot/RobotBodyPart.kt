package com.example.digitalrobot.presentation.robot

enum class RobotBodyPart(val code: Int, val description: String, val touchedTag: String){
    HEAD(1, "HEAD", "HEAD TOUCHED"),
    CHEST(2, "CHEST", "BELLY TOUCHED"),
    RIGHT_HAND(3, "RIGHT HAND", "RIGHT HAND TOUCHED"),
    LEFT_HAND(4, "LEFT HAND", "LEFT HAND TOUCHED"),
    LEFT_FACE(5, "LEFT FACE", "LEFT CHEEK TOUCHED"),
    RIGHT_FACE(6, "RIGHT FACE", "RIGHT CHEEK TOUCHED");

    companion object {
        fun fromCode(code: Int?): RobotBodyPart? {
            return entries.find { it.code == code}
        }
        fun fromDescription(description: String?): RobotBodyPart? {
            return entries.find { it.description.equals(description, ignoreCase = true) }
        }
        fun fromTouchedTag(touchedTag: String): RobotBodyPart? {
            return entries.find { it.touchedTag.equals(touchedTag, ignoreCase = true)}
        }
    }
}

