package com.afsoftwaresolutions.intercommerce.di

import com.afsoftwaresolutions.intercommerce.data.remote.source.DefaultProductRemoteDataSource
import com.afsoftwaresolutions.intercommerce.data.remote.source.ProductRemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Binds
    @Singleton
    abstract fun bindProductRemoteDataSource(
        implementation: DefaultProductRemoteDataSource
    ): ProductRemoteDataSource
}