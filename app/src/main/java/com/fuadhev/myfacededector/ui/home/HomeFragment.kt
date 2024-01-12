package com.fuadhev.myfacededector.ui.home

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.fuadhev.myfacededector.common.base.BaseFragment
import com.fuadhev.myfacededector.databinding.FragmentHomeBinding
import com.fuadhev.myfacededector.ui.home.adapter.ResultAdapter
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

    override fun setupListeners() {
        binding.btnGoTest.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToCameraFragment())
        }
    }

}