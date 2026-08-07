package com.afsoftwaresolutions.intercommerce.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeyEntity(
    @PrimaryKey val scope: String,
    val nextSkip: Int?,
    val endOfPaginationReached: Boolean,
    val updatedAtEpochMillis: Long
)
