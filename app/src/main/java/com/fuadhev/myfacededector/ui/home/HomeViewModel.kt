package com.fuadhev.myfacededector.ui.home

import androidx.lifecycle.ViewModel
import com.fuadhev.myfacededector.data.local.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repo: Repository):ViewModel() {

    fun getResultData()=repo.getResultData()
}