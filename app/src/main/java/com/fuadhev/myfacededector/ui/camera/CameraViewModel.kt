package com.fuadhev.myfacededector.ui.camera

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import com.fuadhev.myfacededector.common.utils.FaceTestType
import com.fuadhev.myfacededector.data.local.Result
import com.fuadhev.myfacededector.data.local.Repository
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

    private val _isEndTest =
        MutableStateFlow(false)
    val isEndTest = _isEndTest.asStateFlow()


    private val allTests = listOf(
        FaceTestState("Head to Left", FaceTestType.LEFT),
        FaceTestState("Head to Right", FaceTestType.RIGHT),
        FaceTestState("Smile", FaceTestType.SMILE),
        FaceTestState("Neutral", FaceTestType.NEUTRAL),
    )

    fun updateTestIndexOrInsert() {
        if (_currentTestIndex.value < allTests.size) {
            _currentTestIndex.value = _currentTestIndex.value + 1
            Log.e("index", "updateCurrentTestIndex:${_currentTestIndex.value} ")
        } else {
            insertResult()
            _currentTestIndex.value = 0
            _isEndTest.value = true
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
                    updateTestIndexOrInsert()
                }
            }

            FaceTestType.RIGHT -> {
                if (head.toInt() < -20) {
                    result.right = true
                    updateTestIndexOrInsert()

                }
            }

            FaceTestType.SMILE -> {
                if (smilingProbability!! > 0.7) {
                    result.smile = true
                    updateTestIndexOrInsert()
                }
            }

            FaceTestType.NEUTRAL -> {
                if (smilingProbability!! < 0.7) {
                    result.neutral = true
                    updateTestIndexOrInsert()
                }

            }
        }
    }


    private fun insertResult() {
        viewModelScope.launch {
            repo.insertResult(result)
        }
    }
}