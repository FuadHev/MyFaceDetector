package com.fuadhev.myfacededector.ui.camera

import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.fuadhev.myfacededector.common.base.BaseFragment
import com.fuadhev.myfacededector.databinding.FragmentCameraBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@AndroidEntryPoint
class CameraFragment : BaseFragment<FragmentCameraBinding>(FragmentCameraBinding::inflate) {
    private lateinit var cameraExecutor: ExecutorService
    private val viewModel by viewModels<CameraViewModel>()

    private var countDownTimer: CountDownTimer? = null

    override fun observeEvents() {

        lifecycleScope.launch {
            viewModel.faceTestType.collectLatest {
                binding.txtTest.text = it.testName
                countDownTimer?.cancel()
                setTimer()
            }
            viewModel.successTest.collectLatest {
                Log.e("success", "observeEvents: $it", )
                if (it){
                    binding.viewSuccess.visibility=VISIBLE

                }else{
                    binding.viewSuccess.visibility=GONE

                }
            }
        }


    }

    private fun setTimer() {

        val totalMillis = 11000L
        val intervalMillis = 1000L
        countDownTimer = object : CountDownTimer(totalMillis, intervalMillis) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                binding.txtTime.text = secondsRemaining.toString()
            }

            override fun onFinish() {

                viewModel.updateTestIndexOrInsert()
            }
        }
        (countDownTimer as CountDownTimer).start()
    }

    override fun onCreateFinish() {
        startCamera(binding.preview)
    }


    private fun startCamera(viewFinder: PreviewView) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                .build()
                .also { it ->
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        lifecycleScope.launch {
                            imageProxy.proxyProcess().collectLatest { face ->
                                Log.e("TAG", "startCamera: $face")
                                viewModel.setTestResult(face)
                            }
                        }

//                        proxyProgress(imageProxy)
                    }
                }

            cameraProvider.bindToLifecycle(
                viewLifecycleOwner,
                cameraSelector,
                preview,
                imageAnalysis
            )

        }, ContextCompat.getMainExecutor(requireContext()))

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

//    @OptIn(ExperimentalGetImage::class)
//    private fun proxyProgress(imageProxy: ImageProxy) {
//        val mediaImage = imageProxy.image
//        if (mediaImage != null) {
//            val image = InputImage.fromMediaImage(
//                mediaImage,
//                imageProxy.imageInfo.rotationDegrees
//            )
//
//            val options = FaceDetectorOptions.Builder()
//                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
//                .build()
//
//            val faceDetector = FaceDetection.getClient(options)
//
//            faceDetector.process(image)
//                .addOnSuccessListener { faces ->
//                    if (faces.size > 0) {
//                        viewModel.setTestResult(faces[0])
//                    }
//                    Log.e("face", "Yüz ${faces.size} yüz bulundu.")
//                }
//                .addOnFailureListener { e ->
//                    Log.e("FaceDetection", "Yüz tespiti başarısız", e)
//                }
//                .addOnCompleteListener {
//                    imageProxy.close()
//                }
//        }
//    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}

@OptIn(ExperimentalGetImage::class)
fun ImageProxy.proxyProcess() = callbackFlow<Face> {
    val mediaImage = this@proxyProcess.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(
            mediaImage,
            this@proxyProcess.imageInfo.rotationDegrees
        )

        val options = FaceDetectorOptions.Builder()
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()

        val faceDetector = FaceDetection.getClient(options)

        faceDetector.process(image)
            .addOnSuccessListener { faces ->
                if (faces.size > 0) {
                    trySend(faces[0])
                }
                Log.e("face", "Yüz ${faces.size} yüz bulundu.")
            }
            .addOnFailureListener { e ->
                Log.e("FaceDetection", "Yüz tespiti başarısız", e)
            }
            .addOnCompleteListener {
                this@proxyProcess.close()
            }
        awaitClose {
            this@proxyProcess.close()
        }
    }


}