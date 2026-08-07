package com.afsoftwaresolutions.intercommerce.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.afsoftwaresolutions.intercommerce.data.local.dao.CartDao
import com.afsoftwaresolutions.intercommerce.data.local.dao.CatalogDao
import com.afsoftwaresolutions.intercommerce.data.local.dao.ProductDao
import com.afsoftwaresolutions.intercommerce.data.local.dao.RemoteKeyDao
import com.afsoftwaresolutions.intercommerce.data.local.entity.CartItemEntity
import com.afsoftwaresolutions.intercommerce.data.local.entity.CatalogEntryEntity
import com.afsoftwaresolutions.intercommerce.data.local.entity.ProductEntity
import com.afsoftwaresolutions.intercommerce.data.local.entity.ProductImageEntity
import com.afsoftwaresolutions.intercommerce.data.local.entity.ProductReviewEntity
import com.afsoftwaresolutions.intercommerce.data.local.entity.ProductTagEntity
import com.afsoftwaresolutions.intercommerce.data.local.entity.RemoteKeyEntity

@Database(
    entities = [
        ProductEntity::class,
        ProductTagEntity::class,
        ProductImageEntity::class,
        ProductReviewEntity::class,
        CatalogEntryEntity::class,
        RemoteKeyEntity::class,
        CartItemEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao

    abstract fun catalogDao(): CatalogDao

    abstract fun remoteKeyDao(): RemoteKeyDao

    abstract fun cartDao(): CartDao

    companion object {
        const val DATABASE_NAME = "intercommerce.db"
        const val CATALOG_SCOPE = "catalog"
    }
}