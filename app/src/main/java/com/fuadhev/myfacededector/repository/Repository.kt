package com.fuadhev.myfacededector.repository

import com.fuadhev.myfacededector.data.local.Result
import com.fuadhev.myfacededector.data.local.ResultDao
import javax.inject.Inject

class Repository @Inject constructor(private val db:ResultDao){


    suspend fun insertResult(result: Result){
        db.insertResult(result)
    }
    fun getResultData()=db.getResultData()
}