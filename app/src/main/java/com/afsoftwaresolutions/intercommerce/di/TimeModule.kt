package com.afsoftwaresolutions.intercommerce.di

import com.afsoftwaresolutions.intercommerce.core.time.SystemTimeProvider
import com.afsoftwaresolutions.intercommerce.core.time.TimeProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TimeModule {

    @Provides
    @Singleton
    fun provideTimeProvider(): TimeProvider = SystemTimeProvider
}