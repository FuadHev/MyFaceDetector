package com.fuadhev.myfacededector.ui.camera

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fuadhev.myfacededector.common.utils.CurrentTest
import com.fuadhev.myfacededector.data.local.Result
import com.fuadhev.myfacededector.data.local.ResultDB
import com.fuadhev.myfacededector.data.local.ResultDao
import com.fuadhev.myfacededector.repository.Repository
import com.google.mlkit.vision.face.Face
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CameraViewModel @Inject constructor(val repo: Repository) : ViewModel() {

    private val _currentTest = MutableStateFlow(CurrentTestState("Head to Left", CurrentTest.LEFT))
    val currentTest = _currentTest.asStateFlow()

//    private val allTests = listOf(
//        CurrentTestState("Head to Left", CurrentTest.LEFT),
//        CurrentTestState("Head to Right", CurrentTest.RIGHT),
//        CurrentTestState("Smile", CurrentTest.SMILE),
//        CurrentTestState("Neutral", CurrentTest.NEUTRAL),
//    )

    fun updateCurrentTest(currentTestState: CurrentTestState) {
        _currentTest.value = currentTestState
    }

    companion object {
        var result = Result(0)
    }

    fun setTestResult(face: Face) {

        val smilingProbability = face.smilingProbability

        val head = face.headEulerAngleY

        when (currentTest.value.test) {
            CurrentTest.LEFT -> {
                if (head.toInt() > 20) {
                    Log.e("FaceDetection", "Kafa sola döndü")
                    result.left = true
                    _currentTest.value = CurrentTestState("Head to Right", CurrentTest.RIGHT)
                }
            }

            CurrentTest.RIGHT -> {
                if (head.toInt() < -20) {
                    Log.e("FaceDetection", "Kafa saga döndü")
                    result.right = true
                    _currentTest.value = CurrentTestState("Smile", CurrentTest.SMILE)
                }
            }

            CurrentTest.SMILE -> {
                if (smilingProbability!! > 0.7) {
                    result.smile = true
                    Log.e("FaceDetection", "Gülüyor")
                    _currentTest.value = CurrentTestState("Neutral", CurrentTest.NEUTRAL)
                }
            }

            CurrentTest.NEUTRAL -> {
                if (smilingProbability!! < 0.7) {
                    Log.e("FaceDetection", "Gülmüyor")
                    result.neutral = true
                    insertResult()
                    _currentTest.value = CurrentTestState("Head to Left", CurrentTest.LEFT)
                    result=Result(0)
                }

            }

        }

    }


    fun insertResult() {
        viewModelScope.launch {
            repo.insertResult(result)
        }
    }
}