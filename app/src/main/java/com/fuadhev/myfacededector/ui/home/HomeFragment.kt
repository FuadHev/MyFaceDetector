package com.fuadhev.myfacededector.ui.home

import androidx.navigation.fragment.findNavController
import com.fuadhev.myfacededector.common.base.BaseFragment
import com.fuadhev.myfacededector.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    override fun observeEvents() {

    }

    override fun onCreateFinish() {

    }

    override fun setupListeners() {
        binding.btnGoTest.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToCameraFragment())
        }
    }

}