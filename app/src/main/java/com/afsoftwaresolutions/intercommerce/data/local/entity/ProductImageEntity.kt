package com.afsoftwaresolutions.intercommerce.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "product_images",
    primaryKeys = ["productId", "position"],
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("productId")]
)
data class ProductImageEntity(
    val productId: Int,
    val position: Int,
    val url: String
)
