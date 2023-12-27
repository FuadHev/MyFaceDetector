package com.fuadhev.myfacededector.ui.camera

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.fuadhev.myfacededector.common.utils.CurrentTest
import com.google.mlkit.vision.face.Face
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class CameraViewModel : ViewModel() {

    private val _currentTest = MutableStateFlow(CurrentTestState("Head to Left", CurrentTest.LEFT))
    val currentTest = _currentTest.asStateFlow()



    fun getTestResult(face: Face) {

        val smilingProbability = face.smilingProbability

        val head = face.headEulerAngleY

        when (currentTest.value.test) {
            CurrentTest.LEFT -> {
                if (head.toInt() > 20) {
                    Log.e("FaceDetection", "Kafa sola döndü")
                    _currentTest.update {
                        it.copy(test = CurrentTest.RIGHT)
                    }
                }


            }

            CurrentTest.RIGHT -> {
                if (head.toInt() < -20) {
                    Log.e("FaceDetection", "Kafa saga döndü")
                    _currentTest.update {
                        it.copy(test = CurrentTest.SMILE)
                    }
                }

            }

            CurrentTest.SMILE -> {
                if (smilingProbability!! > 0.7) {
                    Log.e("FaceDetection", "Gülüyor")
                    _currentTest.update {
                        it.copy(test = CurrentTest.NEUTRAL)
                    }
                }

            }

            CurrentTest.NEUTRAL -> {
                if (smilingProbability!! < 0.7) {
                    Log.e("FaceDetection", "Gülmüyor")
                }
            }
        }

//        if (head.toInt() > 10) {
//            Log.e("FaceDetection", "Kafa sola döndü")
//        } else if (head.toInt() < -10) {
//            Log.e("FaceDetection", "Kafa saga döndü")
//        } else {
//            Log.e("FaceDetection", "Gülmüyor ve Kafa düz")
//        }
//        if (smilingProbability != null) {
//            if (smilingProbability > 0.7) {
//                Log.e("FaceDetection", "Gülüyor")
//            } else {
//                Log.e("FaceDetection", "Gülmüyor")
//            }
//        }

    }

}