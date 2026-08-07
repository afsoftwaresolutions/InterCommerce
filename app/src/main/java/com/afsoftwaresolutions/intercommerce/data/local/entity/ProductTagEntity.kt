package com.afsoftwaresolutions.intercommerce.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "product_tags",
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
data class ProductTagEntity(
    val productId: Int,
    val position: Int,
    val value: String
)
