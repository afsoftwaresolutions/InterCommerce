package com.afsoftwaresolutions.intercommerce.di

import android.content.Context
import androidx.room.Room
import com.afsoftwaresolutions.intercommerce.data.local.dao.CartDao
import com.afsoftwaresolutions.intercommerce.data.local.dao.CatalogDao
import com.afsoftwaresolutions.intercommerce.data.local.dao.ProductDao
import com.afsoftwaresolutions.intercommerce.data.local.dao.RemoteKeyDao
import com.afsoftwaresolutions.intercommerce.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext applicationContext: Context): AppDatabase {
        return Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideProductDao(database: AppDatabase): ProductDao = database.productDao()

    @Provides
    fun provideCatalogDao(database: AppDatabase): CatalogDao = database.catalogDao()

    @Provides
    fun provideRemoteKeyDao(database: AppDatabase): RemoteKeyDao = database.remoteKeyDao()

    @Provides
    fun provideCartDao(database: AppDatabase): CartDao = database.cartDao()
}