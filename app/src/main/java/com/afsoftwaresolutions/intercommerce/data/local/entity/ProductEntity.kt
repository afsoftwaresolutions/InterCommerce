package com.afsoftwaresolutions.intercommerce.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val priceCents: Long,
    val discountBasisPoints: Int,
    val rating: Double,
    val stock: Int,
    val brand: String?,
    val sku: String,
    val weight: Int,
    val dimensionWidth: Double,
    val dimensionHeight: Double,
    val dimensionDepth: Double,
    val warrantyInformation: String,
    val shippingInformation: String,
    val availabilityStatus: String,
    val returnPolicy: String,
    val minimumOrderQuantity: Int,
    val thumbnail: String,
    val cachedAtEpochMillis: Long
)