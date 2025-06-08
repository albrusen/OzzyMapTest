package com.example.data.di

import android.app.Application
import com.example.data.impl.CellProviderImpl
import com.example.data.room.AppDatabase
import com.example.domain.api.CellProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
class MapsDI {

    @Provides
    fun provideDB(appContext: Application): AppDatabase {
        return AppDatabase.getDatabase(appContext)
    }

    @Provides
    fun provideCell(database: AppDatabase): CellProvider {
        return CellProviderImpl(database)
    }

}