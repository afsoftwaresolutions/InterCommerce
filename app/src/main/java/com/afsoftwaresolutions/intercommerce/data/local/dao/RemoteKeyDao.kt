package com.afsoftwaresolutions.intercommerce.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.afsoftwaresolutions.intercommerce.data.local.entity.RemoteKeyEntity

@Dao
interface RemoteKeyDao {

    @Query("SELECT * FROM remote_keys WHERE scope = :scope")
    suspend fun getRemoteKey(scope: String): RemoteKeyEntity?

    @Upsert
    suspend fun upsertRemoteKey(key: RemoteKeyEntity)

    @Query("DELETE FROM remote_keys WHERE scope = :scope")
    suspend fun deleteRemoteKey(scope: String)

    @Query("DELETE FROM remote_keys")
    suspend fun clearRemoteKeys()
}