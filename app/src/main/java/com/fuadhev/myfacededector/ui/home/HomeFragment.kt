package com.fuadhev.myfacededector.ui.home

import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import android.content.pm.PackageManager
import com.fuadhev.myfacededector.common.base.BaseFragment
import com.fuadhev.myfacededector.databinding.FragmentHomeBinding
import com.fuadhev.myfacededector.ui.home.adapter.ResultAdapter
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    private val viewModel by viewModels<HomeViewModel>()

    private val resultAdapter by lazy {
        ResultAdapter()
    }
    override fun observeEvents() {

        viewModel.getResultData().observe(viewLifecycleOwner){
            resultAdapter.submitList(it)
        }

    }

    override fun onCreateFinish() {
        binding.rvResult.adapter=resultAdapter
    }
    private fun permissionHandler() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    android.Manifest.permission.CAMERA
                )
            ) {
                Snackbar.make(requireView(), "Permission is required for the camera", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Give permission") {
                        requestCameraPermission()
                    }
                    .show()
            } else {
                requestCameraPermission()
            }
        } else {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToCameraFragment())

        }
    }

    private fun requestCameraPermission() {
        requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
    }
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToCameraFragment())
            } else {
                Toast.makeText(
                    requireContext(),
                    "Camera permission denied",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    override fun setupListeners() {
        binding.btnGoTest.setOnClickListener {
            permissionHandler()

        }
    }

}