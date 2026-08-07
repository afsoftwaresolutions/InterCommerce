package com.afsoftwaresolutions.intercommerce.di

import com.afsoftwaresolutions.intercommerce.data.repository.CartRepositoryImpl
import com.afsoftwaresolutions.intercommerce.data.repository.ProductRepositoryImpl
import com.afsoftwaresolutions.intercommerce.domain.repository.CartRepository
import com.afsoftwaresolutions.intercommerce.domain.repository.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        implementation: ProductRepositoryImpl
    ): ProductRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(
        implementation: CartRepositoryImpl
    ): CartRepository
}