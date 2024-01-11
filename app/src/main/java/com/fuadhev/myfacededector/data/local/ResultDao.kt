package com.fuadhev.myfacededector.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface ResultDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertResult(result:Result)

    @Query("SELECT*FROM result")
    fun getResultData():LiveData<List<Result>>
}