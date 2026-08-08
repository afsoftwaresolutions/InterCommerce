package com.afsoftwaresolutions.intercommerce.di

import android.content.Context
import android.net.ConnectivityManager
import com.afsoftwaresolutions.intercommerce.core.network.NetworkConnectivityValidator
import com.afsoftwaresolutions.intercommerce.data.remote.network.AndroidNetworkConnectivityValidator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ConnectivityModule {

    @Provides
    @Singleton
    fun provideConnectivityManager(
        @ApplicationContext context: Context
    ): ConnectivityManager {
        return context.getSystemService(ConnectivityManager::class.java)
    }

    @Provides
    @Singleton
    fun provideNetworkConnectivityValidator(
        @ApplicationContext context: Context,
        connectivityManager: ConnectivityManager
    ): NetworkConnectivityValidator {
        return AndroidNetworkConnectivityValidator(
            context = context,
            connectivityManager = connectivityManager
        )
    }
}

