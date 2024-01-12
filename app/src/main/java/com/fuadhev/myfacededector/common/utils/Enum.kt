package com.fuadhev.myfacededector.common.utils

enum class FaceTestType(val testType: String){
    LEFT("LEFT"),
    RIGHT("RIGHT"),
    SMILE("SMILE"),
    NEUTRAL("NEUTRAL");

//    companion object {
//        fun from(value: String) =
//            values().find { it.currentTest.lowercase() == value.lowercase() }
//    }
}