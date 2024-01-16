package com.fuadhev.myfacededector.di

import android.content.Context
import androidx.room.Room
import com.fuadhev.myfacededector.data.local.ResultDB
import com.fuadhev.myfacededector.data.local.ResultDao
import com.fuadhev.myfacededector.data.local.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRoomDatabase(@ApplicationContext context: Context): ResultDB =
        Room.databaseBuilder(
            context,
            ResultDB::class.java,
            "ResultDataDB"
        ).build()

    @Singleton
    @Provides
    fun provideResultDao(db: ResultDB): ResultDao = db.getResultDao()

    @Singleton
    @Provides
    fun provideRepository(db:ResultDao): Repository = Repository(db)

}