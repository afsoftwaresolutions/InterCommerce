package com.afsoftwaresolutions.intercommerce.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.afsoftwaresolutions.intercommerce.data.local.entity.CatalogEntryEntity

@Dao
interface CatalogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCatalogEntries(entries: List<CatalogEntryEntity>)

    @Query("DELETE FROM catalog_entries")
    suspend fun clearCatalogEntries()

    @Query("SELECT COUNT(*) FROM catalog_entries")
    suspend fun countCatalogEntries(): Int

    @Query("SELECT * FROM catalog_entries ORDER BY position ASC, productId ASC")
    suspend fun getCatalogEntriesOrdered(): List<CatalogEntryEntity>
}