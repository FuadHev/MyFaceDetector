package com.fuadhev.myfacededector.ui.camera

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fuadhev.myfacededector.common.utils.FaceTestType
import com.fuadhev.myfacededector.data.local.Result
import com.fuadhev.myfacededector.repository.Repository
import com.google.mlkit.vision.face.Face
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CameraViewModel @Inject constructor(val repo: Repository) : ViewModel() {

    private val _faceTestType =
        MutableStateFlow(FaceTestState("Head to Left", FaceTestType.NEUTRAL))
    val faceTestType = _faceTestType.asStateFlow()

    private val _currentTestIndex = MutableStateFlow(0)

    private val _successTest = MutableStateFlow(false)
    val successTest = _successTest.asStateFlow()

    private val allTests = listOf(
        FaceTestState("Head to Left", FaceTestType.LEFT),
        FaceTestState("Head to Right", FaceTestType.RIGHT),
        FaceTestState("Smile", FaceTestType.SMILE),
        FaceTestState("Neutral", FaceTestType.NEUTRAL),
    )

    fun updateTestIndexOrInsert() {
        viewModelScope.launch(Dispatchers.Default){
            delay(2000)
            _successTest.value = false
        }
        if (_currentTestIndex.value < allTests.size) {
            _currentTestIndex.value = _currentTestIndex.value + 1
            Log.e("index", "updateCurrentTestIndex:${_currentTestIndex.value} ")
        } else {
            insertResult()
            _currentTestIndex.value = 0
        }

    }

    var result = Result(0)

     fun setTestResult(face: Face) {

        val smilingProbability = face.smilingProbability

        val head = face.headEulerAngleY
        if (_currentTestIndex.value < allTests.size) {
            _faceTestType.value = allTests[_currentTestIndex.value]
        }

        when (faceTestType.value.test) {
            FaceTestType.LEFT -> {
                if (head.toInt() > 20) {
                    result.left = true
                    _successTest.value = true
                    updateTestIndexOrInsert()
                }
            }

            FaceTestType.RIGHT -> {
                if (head.toInt() < -20) {

                    result.right = true
                    _successTest.value = true
                    updateTestIndexOrInsert()

                }
            }

            FaceTestType.SMILE -> {
                if (smilingProbability!! > 0.7) {
                    result.smile = true
                    _successTest.value = true

                    updateTestIndexOrInsert()
                }
            }

            FaceTestType.NEUTRAL -> {
                if (smilingProbability!! < 0.7) {

                    result.neutral = true
                    _successTest.value = true

                    updateTestIndexOrInsert()
                }

            }
        }


//        when (FaceTestType.value.test) {
//            FaceTestType.LEFT -> {
//                if (head.toInt() > 20) {
//                    Log.e("FaceDetection", "Kafa sola döndü")
//                    result.left = true
//                    _faceTestType.value = FaceTestState("Head to Right", FaceTestType.RIGHT)
//                }
//            }
//
//            FaceTestType.RIGHT -> {
//                if (head.toInt() < -20) {
//                    Log.e("FaceDetection", "Kafa saga döndü")
//                    result.right = true
//                    _faceTestType.value = FaceTestState("Smile", FaceTestType.SMILE)
//                }
//            }
//
//            FaceTestType.SMILE -> {
//                if (smilingProbability!! > 0.7) {
//                    result.smile = true
//                    Log.e("FaceDetection", "Gülüyor")
//                    _faceTestType.value = FaceTestState("Neutral", FaceTestType.NEUTRAL)
//                }
//            }
//
//            FaceTestType.NEUTRAL -> {
//                if (smilingProbability!! < 0.7) {
//                    Log.e("FaceDetection", "Gülmüyor")
//                    result.neutral = true
//                    insertResult()
//                    _faceTestType.value = FaceTestState("Head to Left", FaceTestType.LEFT)
//                }
//
//            }
//
//        }

    }


    fun insertResult() {
        viewModelScope.launch {
            repo.insertResult(result)
        }
    }
}