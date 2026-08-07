package com.afsoftwaresolutions.intercommerce.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val productId: Int,
    val title: String,
    val thumbnail: String,
    val unitPriceCents: Long,
    val discountBasisPoints: Int,
    val quantity: Int,
    val availableStock: Int,
    val updatedAtEpochMillis: Long
)