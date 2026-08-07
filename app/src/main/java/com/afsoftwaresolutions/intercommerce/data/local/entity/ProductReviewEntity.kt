package com.afsoftwaresolutions.intercommerce.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "product_reviews",
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
data class ProductReviewEntity(
    val productId: Int,
    val position: Int,
    val rating: Int,
    val comment: String,
    val date: String,
    val reviewerName: String,
    val reviewerEmail: String
)
