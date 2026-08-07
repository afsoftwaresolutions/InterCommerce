package com.afsoftwaresolutions.intercommerce.data.local.model

import androidx.room.Embedded
import androidx.room.Relation
import com.afsoftwaresolutions.intercommerce.data.local.entity.ProductEntity
import com.afsoftwaresolutions.intercommerce.data.local.entity.ProductImageEntity
import com.afsoftwaresolutions.intercommerce.data.local.entity.ProductReviewEntity
import com.afsoftwaresolutions.intercommerce.data.local.entity.ProductTagEntity

data class ProductWithDetails(
    @Embedded val product: ProductEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "productId",
        entity = ProductTagEntity::class
    )
    val tags: List<ProductTagEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "productId",
        entity = ProductImageEntity::class
    )
    val images: List<ProductImageEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "productId",
        entity = ProductReviewEntity::class
    )
    val reviews: List<ProductReviewEntity>
)